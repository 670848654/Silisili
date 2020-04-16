package my.project.silisili.main.desc;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanchen.sniffing.SniffingUICallback;
import com.fanchen.sniffing.SniffingVideo;
import com.fanchen.sniffing.web.SniffingUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import my.project.silisili.R;
import my.project.silisili.adapter.AnimeDescDetailsAdapter;
import my.project.silisili.adapter.AnimeDescDramaAdapter;
import my.project.silisili.adapter.AnimeDescRecommendAdapter;
import my.project.silisili.application.Silisili;
import my.project.silisili.bean.AnimeDescBean;
import my.project.silisili.bean.AnimeDescDetailsBean;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.bean.AnimeDescRecommendBean;
import my.project.silisili.bean.DownBean;
import my.project.silisili.database.DatabaseUtil;
import my.project.silisili.main.base.BaseActivity;
import my.project.silisili.main.video.VideoContract;
import my.project.silisili.main.video.VideoPresenter;
import my.project.silisili.util.SharedPreferencesUtils;
import my.project.silisili.util.StatusBarUtil;
import my.project.silisili.util.SwipeBackLayoutUtil;
import my.project.silisili.util.Utils;
import my.project.silisili.util.VideoUtils;
import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.blurry.Blurry;

public class DescActivity extends BaseActivity<DescContract.View, DescPresenter> implements DescContract.View, VideoContract.View, SniffingUICallback {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.details_list)
    RecyclerView detailsRv;
    @BindView(R.id.recommend_list)
    RecyclerView recommendRv;
    @BindView(R.id.anime_img)
    ImageView animeImg;
    @BindView(R.id.region)
    AppCompatTextView region;
    @BindView(R.id.year)
    AppCompatTextView year;
    @BindView(R.id.tag)
    AppCompatTextView tag;
    @BindView(R.id.show)
    AppCompatTextView show;
    @BindView(R.id.desc)
    AppCompatTextView desc;
    @BindView(R.id.state)
    AppCompatTextView state;
    @BindView(R.id.error_bg)
    RelativeLayout errorBg;
    @BindView(R.id.content_bg)
    NestedScrollView contentBg;
    @BindView(R.id.open_drama)
    AppCompatTextView openDrama;
    private RecyclerView lineRecyclerView;
    private AnimeDescDetailsAdapter animeDescDetailsAdapter;
    private AnimeDescRecommendAdapter animeDescRecommendAdapter;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private AnimeDescBean animeDescBeans = new AnimeDescBean();
    @BindView(R.id.title_img)
    ImageView imageView;
    private String siliUrl, dramaUrl;
    private String animeTitle;
    private String witchTitle;
    private ProgressDialog p;
    @BindView(R.id.favorite)
    FloatingActionButton favorite;
    private boolean isFavorite;
    private VideoPresenter videoPresenter;
    private AnimeDescHeaderBean animeDescHeaderBean = new AnimeDescHeaderBean();
    private List<String> animeUrlList = new ArrayList();
    private boolean mIsLoad = false;
    private List<DownBean> downBeanList;
    private MenuItem downView;
    private BottomSheetDialog mBottomSheetDialog;
    private AnimeDescDramaAdapter animeDescDramaAdapter;

    @Override
    protected DescPresenter createPresenter() {
        return new DescPresenter(siliUrl, this);
    }

    @Override
    protected void loadData() {
        mPresenter.loadData(true);
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_desc;
    }

    @Override
    protected void init() {
        StatusBarUtil.setColorForSwipeBack(this, getResources().getColor(R.color.colorPrimaryDark), 0);
        StatusBarUtil.setTranslucentForImageView(this, 0, toolbar);
        Slidr.attach(this, Utils.defaultInit());
        getBundle();
        initToolbar();
        initFab();
        initSwipe();
        initAdapter();
    }

    @Override
    protected void initBeforeView() {
        SwipeBackLayoutUtil.convertActivityToTranslucent(this);
    }

    public void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            siliUrl = bundle.getString("url");
            animeTitle = bundle.getString("name");
            animeUrlList.add(siliUrl);
        }
    }

    public void initToolbar() {
        toolbar.setTitle(Utils.getString(R.string.loading));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    public void initFab() {
        favorite.setOnClickListener(view -> {
            if (Utils.isFastClick()) favoriteAnime();
        });
    }

    public void initSwipe() {
        mSwipe.setColorSchemeResources(R.color.pink500, R.color.blue500, R.color.purple500);
        mSwipe.setOnRefreshListener(() -> {
            contentBg.setVisibility(View.GONE);
            mPresenter.loadData(true);
        });
        mSwipe.setRefreshing(true);
    }

    @SuppressLint("RestrictedApi")
    public void initAdapter() {
        animeDescDetailsAdapter = new AnimeDescDetailsAdapter(this, animeDescBeans.getAnimeDescDetailsBeans());
        animeDescDetailsAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
            AnimeDescDetailsBean bean = (AnimeDescDetailsBean) adapter.getItem(position);
            playVideo(adapter, position, bean, detailsRv);
        });
        detailsRv.setLayoutManager(getLinearLayoutManager());
        detailsRv.setAdapter(animeDescDetailsAdapter);
        detailsRv.setNestedScrollingEnabled(false);

        animeDescRecommendAdapter = new AnimeDescRecommendAdapter(this, animeDescBeans.getAnimeDescRecommendBeans());
        animeDescRecommendAdapter.setOnItemClickListener((adapter, view, position) -> {
            AnimeDescRecommendBean bean = (AnimeDescRecommendBean) adapter.getItem(position);
            animeTitle = bean.getTitle();
            siliUrl = VideoUtils.getSiliUrl(bean.getUrl());
            animeUrlList.add(siliUrl);
            openAnimeDesc();
        });
        recommendRv.setLayoutManager(getLinearLayoutManager());
        recommendRv.setAdapter(animeDescRecommendAdapter);
        recommendRv.setNestedScrollingEnabled(false);

        View dramaView = LayoutInflater.from(this).inflate(R.layout.dialog_drama, null);
        lineRecyclerView = dramaView.findViewById(R.id.drama_list);
        lineRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        animeDescDramaAdapter = new AnimeDescDramaAdapter(this, animeDescBeans.getAnimeDescDetailsBeans());
        animeDescDramaAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
            mBottomSheetDialog.dismiss();
            AnimeDescDetailsBean bean = (AnimeDescDetailsBean) adapter.getItem(position);
            playVideo(adapter, position, bean, lineRecyclerView);
        });
        lineRecyclerView.setAdapter(animeDescDramaAdapter);
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(dramaView);
    }

    private LinearLayoutManager getLinearLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        return linearLayoutManager;
    }

    @OnClick(R.id.open_drama)
    public void dramaClick() {
        if (!mBottomSheetDialog.isShowing()) mBottomSheetDialog.show();
    }


    @SuppressLint("RestrictedApi")
    public void openAnimeDesc() {
        toolbar.setTitle(Utils.getString(R.string.loading));
        animeImg.setImageDrawable(getDrawable(R.drawable.loading));
        region.setText("");
        year.setText("");
        tag.setText("");
        show.setText("");
        desc.setText("");
        state.setText("");
        mSwipe.setRefreshing(true);
        imageView.setImageDrawable(null);
        animeDescBeans = new AnimeDescBean();
        favorite.setVisibility(View.GONE);
        mPresenter = new DescPresenter(siliUrl, this);
        mPresenter = new DescPresenter(siliUrl, this);
        mPresenter.loadData(true);
    }

    public void playVideo(BaseQuickAdapter adapter, int position, AnimeDescDetailsBean bean, RecyclerView recyclerView) {
        p = Utils.getProDialog(DescActivity.this, R.string.parsing);
        Button v = (Button) adapter.getViewByPosition(recyclerView, position, R.id.tag_group);
        v.setBackgroundResource(R.drawable.button_selected);
        v.setTextColor(getResources().getColor(R.color.item_selected_color));
        bean.setSelected(true);
        dramaUrl = VideoUtils.getSiliUrl(bean.getUrl());
        witchTitle = animeTitle + " - " + bean.getTitle();
        animeDescDetailsAdapter.notifyDataSetChanged();
        videoPresenter = new VideoPresenter(animeTitle, dramaUrl, DescActivity.this);
        videoPresenter.loadData(true);
    }

    /**
     * 播放视频
     *
     * @param animeUrl
     */
    private void playAnime(String animeUrl) {
        if (animeUrl.contains(".mp4") || animeUrl.contains(".m3u8")) {
            cancelDialog();
            switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
                case 0:
                    //调用播放器
                    VideoUtils.openPlayer(true, this, witchTitle, animeUrl, animeTitle, siliUrl, animeDescBeans.getAnimeDescDetailsBeans());
                    break;
                case 1:
                    Utils.selectVideoPlayer(this, animeUrl);
                    break;
            }
        } else {
            Silisili.getInstance().showToastMsg(Utils.getString(R.string.should_be_used_web));
            SniffingUtil.get().activity(this).referer(animeUrl).callback(this).url(animeUrl).start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x10 && resultCode == 0x20) {
            mSwipe.setRefreshing(true);
            mPresenter.loadData(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (animeUrlList.size() == 1) super.onBackPressed();
        else {
            if (!mIsLoad) {
                animeUrlList.remove(animeUrlList.size() - 1);
                siliUrl = animeUrlList.get(animeUrlList.size() - 1);
                openAnimeDesc();
            } else Silisili.getInstance().showToastMsg(Utils.getString(R.string.load_desc_info));
        }
    }

    public void favoriteAnime() {
        setResult(200);
        isFavorite = DatabaseUtil.favorite(animeDescHeaderBean);
        if (isFavorite) {
            Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_white_48dp).into(favorite);
            application.showCustomToastMsg(Utils.getString(R.string.join_ok),
                    R.drawable.ic_add_favorite_48dp, R.color.green300);
        } else {
            Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_border_white_48dp).into(favorite);
            application.showCustomToastMsg(Utils.getString(R.string.join_error),
                    R.drawable.ic_remove_favorite_48dp, R.color.red300);
        }
    }

    public void setCollapsingToolbar() {
        Glide.with(DescActivity.this).asBitmap().load(animeDescHeaderBean.getImg()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Blurry.with(DescActivity.this)
                        .radius(4)
                        .sampling(2)
                        .async()
                        .from(resource)
                        .into(imageView);
            }
        });
        toolbar.setTitle(animeDescHeaderBean.getName());
        Utils.setDefaultImage(this, animeDescHeaderBean.getImg(), animeImg);
        region.setText(animeDescHeaderBean.getRegion().equals("地区：") ? Utils.getString(R.string.no_region_msg) : animeDescHeaderBean.getRegion());
        year.setText(animeDescHeaderBean.getYear().equals("年代：") ? Utils.getString(R.string.no_year_msg) : animeDescHeaderBean.getYear());
        tag.setText(animeDescHeaderBean.getTag().equals("标签：") ? Utils.getString(R.string.no_tag_msg) : animeDescHeaderBean.getTag());
        show.setText(animeDescHeaderBean.getShow().equals("看点：") ? Utils.getString(R.string.no_eye_msg) : animeDescHeaderBean.getShow());
        desc.setText(animeDescHeaderBean.getDesc().equals("简介：") ? Utils.getString(R.string.no_show_msg) : animeDescHeaderBean.getDesc());
        state.setText(animeDescHeaderBean.getState().equals("状态：") ? Utils.getString(R.string.no_state_msg) : animeDescHeaderBean.getState());
    }

    @Override
    public void showLoadingView() {
        mIsLoad = true;
        showEmptyVIew();
        detailsRv.scrollToPosition(0);
        recommendRv.scrollToPosition(0);
    }

    @Override
    public void showLoadErrorView(String msg) {
        runOnUiThread(() -> {
            if (!mActivityFinish) {
                mIsLoad = false;
                mSwipe.setRefreshing(false);
                setCollapsingToolbar();
                contentBg.setVisibility(View.GONE);
                errorBg.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showEmptyVIew() {
        mSwipe.setRefreshing(true);
        if (downView != null && downView.isVisible())
            downView.setVisible(false);
        contentBg.setVisibility(View.GONE);
        errorBg.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.down:
                showDownDialog();
                break;
            case R.id.open_in_browser:
                Utils.viewInChrome(this, siliUrl);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.desc_menu, menu);
        downView = menu.findItem(R.id.down);
        return true;
    }

    private void showDownDialog() {
        AlertDialog alertDialog;
        String[] downArr = new String[downBeanList.size()];
        for (int i = 0; i < downBeanList.size(); i++) {
            downArr[i] = downBeanList.get(i).getTitle();
        }
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(Utils.getString(R.string.down_title));
        builder.setItems(downArr, (dialogInterface, i) -> {
            Utils.putTextIntoClip(downBeanList.get(i).getTitle());
            application.showSuccessToastMsg(downBeanList.get(i).getTitle() + Utils.getString(R.string.down_copy));
            Utils.viewInChrome(DescActivity.this, downBeanList.get(i).getUrl());
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void showSuccessMainView(AnimeDescBean bean) {
        runOnUiThread(() -> {
            if (!mActivityFinish) {
                mIsLoad = false;
                setCollapsingToolbar();
                mSwipe.setRefreshing(false);
                contentBg.setVisibility(View.VISIBLE);
                this.animeDescBeans = bean;
                animeDescDetailsAdapter.setNewData(bean.getAnimeDescDetailsBeans());
                animeDescRecommendAdapter.setNewData(bean.getAnimeDescRecommendBeans());
                animeDescDramaAdapter.setNewData(bean.getAnimeDescDetailsBeans());
            }
        });
    }

    @Override
    public void showSuccessDescView(AnimeDescHeaderBean bean) {
        animeDescHeaderBean = bean;
        animeTitle = bean.getName();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void showSuccessFavorite(boolean is) {
        isFavorite = is;
        runOnUiThread(() -> {
            if (!mActivityFinish) {
                if (!favorite.isShown()) {
                    if (isFavorite)
                        Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_white_48dp).into(favorite);
                    else
                        Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_border_white_48dp).into(favorite);
                    favorite.startAnimation(Utils.animationOut(1));
                    favorite.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void showDownView(List<DownBean> list) {
        runOnUiThread(() -> {
            downBeanList = list;
            if (downView != null)
                downView.setVisible(true);
        });
    }

    @Override
    public void cancelDialog() {
        Utils.cancelProDialog(p);
    }

    @Override
    public void getVideoSuccess(String url) {
        runOnUiThread(() -> playAnime(url));
    }

    @Override
    public void getIframeUrl(String iframeUrl) {
        runOnUiThread(() -> {
            Silisili.getInstance().showToastMsg(Utils.getString(R.string.should_be_used_web));
            SniffingUtil.get().activity(this).referer(iframeUrl).callback(this).url(iframeUrl).start();
        });
    }

    @Override
    public void getVideoEmpty() {
        runOnUiThread(() -> {
            Silisili.getInstance().showToastMsg(Utils.getString(R.string.open_web_view));
            VideoUtils.openDefaultWebview(this, dramaUrl);
        });
    }

    @Override
    public void getVideoError() {
        //网络出错
        runOnUiThread(() -> application.showErrorToastMsg(Utils.getString(R.string.error_700)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != videoPresenter)
            videoPresenter.detachView();
    }

    @Override
    public void onSniffingStart(View webView, String url) {
    }

    @Override
    public void onSniffingFinish(View webView, String url) {
        SniffingUtil.get().releaseWebView();
        cancelDialog();
    }

    @Override
    public void onSniffingSuccess(View webView, String url, List<SniffingVideo> videos) {
        List<String> urls = new ArrayList<>();
        for (SniffingVideo video : videos) {
            urls.add(video.getUrl());
        }
        VideoUtils.showMultipleVideoSources(this,
                urls,
                (dialog, index) -> playAnime(urls.get(index)), (dialog, which) -> {
                    cancelDialog();
                    dialog.dismiss();
                }, 1);
    }

    @Override
    public void onSniffingError(View webView, String url, int errorCode) {
        Silisili.getInstance().showToastMsg(Utils.getString(R.string.open_web_view));
        VideoUtils.openDefaultWebview(this, dramaUrl);
    }
}
