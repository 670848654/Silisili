package my.project.silisili.main.desc;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.blurry.Blurry;
import my.project.silisili.R;
import my.project.silisili.adapter.AnimeDescDetailsAdapter;
import my.project.silisili.adapter.AnimeDescDramaAdapter;
import my.project.silisili.adapter.AnimeDescRecommendAdapter;
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

public class DescActivity extends BaseActivity<DescContract.View, DescPresenter> implements DescContract.View, VideoContract.View, SniffingUICallback {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.details_list)
    RecyclerView detailsRv;
    @BindView(R.id.recommend_list)
    RecyclerView recommendRv;
    @BindView(R.id.anime_img)
    ImageView animeImg;
    @BindView(R.id.title)
    AppCompatTextView title;
    @BindView(R.id.region)
    AppCompatTextView region;
    @BindView(R.id.year)
    AppCompatTextView year;
    @BindView(R.id.tag)
    AppCompatTextView tag;
    @BindView(R.id.desc)
    AppCompatTextView desc;
    @BindView(R.id.state)
    AppCompatTextView state;
    @BindView(R.id.error_bg)
    RelativeLayout errorBg;
    @BindView(R.id.desc_layout)
    LinearLayout descLinearLayout;
    @BindView(R.id.play_layout)
    LinearLayout playLinearLayout;
    @BindView(R.id.recommend_layout)
    LinearLayout recommendLinearLayout;
    @BindView(R.id.open_drama)
    RelativeLayout openDrama;
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
    private BottomSheetDialog mBottomSheetDialog;
    private AnimeDescDramaAdapter animeDescDramaAdapter;
    @BindView(R.id.msg)
    CoordinatorLayout msg;
    @BindView(R.id.error_msg)
    TextView error_msg;
    private ImageView closeDrama;
    @BindView(R.id.operation)
    LinearLayout operation;
    @BindView(R.id.down)
    AppCompatTextView down;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.desc_view)
    LinearLayout desc_view;

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
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) msg.getLayoutParams();
        params.setMargins(0, 0, 0, Utils.getNavigationBarHeight(this) - 5);
        setCollapsingToolbarLayoutHeight();
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

    private void setCollapsingToolbarLayoutHeight() {
        appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, Utils.getActionBarHeight() + Utils.getStatusBarHeight() + Utils.dpToPx(this,180)));
        CollapsingToolbarLayout.LayoutParams params2 = (CollapsingToolbarLayout.LayoutParams) desc_view.getLayoutParams();
        int marginSize = Utils.dpToPx(this, 10);
        params2.setMargins(marginSize, Utils.getActionBarHeight() + Utils.getStatusBarHeight() + marginSize, marginSize, marginSize);
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
        int test = Utils.pxTodp(this, Utils.getStatusBarHeight());
        Log.e("actionBarHeight", Utils.getActionBarHeight()+ "");
        Log.e("pxTodp", test+"");
        toolbar.setTitle(Utils.getString(R.string.desc_toolbar_title));
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
        mSwipe.setOnRefreshListener(() -> mPresenter.loadData(true));
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
        closeDrama = dramaView.findViewById(R.id.close_drama);
        closeDrama.setOnClickListener(v-> mBottomSheetDialog.dismiss());
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
        animeImg.setImageDrawable(getDrawable(R.drawable.loading));
        setTextviewEmpty(title);
        setTextviewEmpty(region);
        setTextviewEmpty(year);
        setTextviewEmpty(tag);
        setTextviewEmpty(desc);
        setTextviewEmpty(state);
        mSwipe.setRefreshing(true);
        imageView.setImageDrawable(null);
        animeDescBeans = new AnimeDescBean();
        operation.setVisibility(View.GONE);
        mPresenter = new DescPresenter(siliUrl, this);
        mPresenter.loadData(true);
    }

    private void setTextviewEmpty(AppCompatTextView appCompatTextView) {
        appCompatTextView.setText("");
    }

    @OnClick({R.id.down, R.id.open})
    public void imgClicked(AppCompatTextView appCompatTextView) {
        switch (appCompatTextView.getId()) {
            case R.id.down:
                showDownDialog();
                break;
            case R.id.open:
                Utils.viewInChrome(this, siliUrl);
                break;
        }
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
        animeDescDramaAdapter.notifyDataSetChanged();
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
            p = Utils.getProDialog(DescActivity.this, R.string.parsing);
            application.showToastMsg(Utils.getString(R.string.should_be_used_web));
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
            } else  application.showSnackbarMsg(msg, Utils.getString(R.string.load_desc_info));
        }
    }

    public void favoriteAnime() {
        setResult(200);
        isFavorite = DatabaseUtil.favorite(animeDescHeaderBean);
        if (isFavorite) {
            Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_white_48dp).into(favorite);
            application.showSnackbarMsg(msg, Utils.getString(R.string.join_ok));
        } else {
            Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_border_white_48dp).into(favorite);
            application.showSnackbarMsg(msg, Utils.getString(R.string.join_error));
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
        Utils.setDefaultImage(this, animeDescHeaderBean.getImg(), animeImg);
        title.setText(animeDescHeaderBean.getName());
        region.setText(animeDescHeaderBean.getRegion().equals("地区：") ? Utils.getString(R.string.no_region_msg) : animeDescHeaderBean.getRegion());
        year.setText(animeDescHeaderBean.getYear().equals("年代：") ? Utils.getString(R.string.no_year_msg) : animeDescHeaderBean.getYear());
        tag.setText(animeDescHeaderBean.getTag().equals("标签：") ? Utils.getString(R.string.no_tag_msg) : animeDescHeaderBean.getTag().replaceAll("\\|", " ").replaceAll(",", " "));
        desc.setText( animeDescHeaderBean.getDesc().replaceAll("简介：",""));
        state.setText(animeDescHeaderBean.getState().equals("状态：") ? Utils.getString(R.string.no_state_msg) : animeDescHeaderBean.getState());
        operation.setVisibility(View.VISIBLE);
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
                descLinearLayout.setVisibility(View.GONE);
                playLinearLayout.setVisibility(View.GONE);
                recommendLinearLayout.setVisibility(View.GONE);
                error_msg.setText(msg);
                errorBg.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showEmptyVIew() {
        mSwipe.setRefreshing(true);
        down.setVisibility(View.GONE);
        descLinearLayout.setVisibility(View.GONE);
        playLinearLayout.setVisibility(View.GONE);
        recommendLinearLayout.setVisibility(View.GONE);
        errorBg.setVisibility(View.GONE);
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
                descLinearLayout.setVisibility(View.VISIBLE);
                playLinearLayout.setVisibility(View.VISIBLE);
                recommendLinearLayout.setVisibility(View.VISIBLE);
                this.animeDescBeans = bean;
                if (bean.getAnimeDescDetailsBeans().size() > 4)
                    openDrama.setVisibility(View.VISIBLE);
                else
                    openDrama.setVisibility(View.GONE);
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
            down.setVisibility(View.VISIBLE);
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
            application.showToastMsg(Utils.getString(R.string.should_be_used_web));
            SniffingUtil.get().activity(this).referer(iframeUrl).callback(this).url(iframeUrl).start();
        });
    }

    @Override
    public void getVideoEmpty() {
        runOnUiThread(() -> {
            cancelDialog();
            application.showToastMsg(Utils.getString(R.string.open_web_view));
            VideoUtils.openDefaultWebview(this, dramaUrl);
        });
    }

    @Override
    public void getVideoError() {
        //网络出错
        runOnUiThread(() -> {
            cancelDialog();
            application.showErrorToastMsg(Utils.getString(R.string.error_700));
        });
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
        application.showToastMsg(Utils.getString(R.string.open_web_view));
        VideoUtils.openDefaultWebview(this, dramaUrl);
    }
}
