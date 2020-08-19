package my.project.silisili.main.desc;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanchen.sniffing.SniffingUICallback;
import com.fanchen.sniffing.SniffingVideo;
import com.fanchen.sniffing.web.SniffingUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.r0adkll.slidr.Slidr;
import com.zhouwei.blurlibrary.EasyBlur;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import my.project.silisili.R;
import my.project.silisili.adapter.AnimeDescDetailsAdapter;
import my.project.silisili.adapter.AnimeDescDramaAdapter;
import my.project.silisili.adapter.AnimeDescRecommendAdapter;
import my.project.silisili.bean.AnimeDescBean;
import my.project.silisili.bean.AnimeDescDetailsBean;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.bean.AnimeDescRecommendBean;
import my.project.silisili.bean.DownBean;
import my.project.silisili.custom.InsideScrollView;
import my.project.silisili.database.DatabaseUtil;
import my.project.silisili.main.animelist.AnimeListActivity;
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
    @BindView(R.id.desc)
    AppCompatTextView desc;
    @BindView(R.id.error_bg)
    RelativeLayout errorBg;
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
    private String siliUrl, dramaUrl;
    private String animeTitle;
    private String witchTitle;
    private ProgressDialog p;
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
    @BindView(R.id.desc_view)
    LinearLayout desc_view;
    @BindView(R.id.bg)
    ImageView bg;
    private MenuItem downView, favorite;
    @BindView(R.id.tag_view)
    TagContainerLayout tagContainerLayout;
    @BindView(R.id.inside_view)
    InsideScrollView scrollView;

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
        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, toolbar);
        if ((Boolean) SharedPreferencesUtils.getParam(this, "darkTheme", false)) bg.setVisibility(View.GONE);
        Slidr.attach(this, Utils.defaultInit());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) msg.getLayoutParams();
        params.setMargins(10, 0, 10, 0);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> mSwipe.setEnabled(scrollView.getScrollY() == 0));
        getBundle();
        initToolbar();
        initSwipe();
        initAdapter();
        initTagClick();
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
        if (Utils.checkHasNavigationBar(this)) recommendRv.setPadding(0,0,0, Utils.getNavigationBarHeight(this));
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

    private void initTagClick() {
        tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                if (animeDescHeaderBean.getTagUrls().get(position).isEmpty())
                    application.showToastMsg(Utils.getString(R.string.no_tag_url));
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", animeDescHeaderBean.getTagTitles().get(position));
                    bundle.putString("url", VideoUtils.getSiliUrl(animeDescHeaderBean.getTagUrls().get(position)));
                    startActivity(new Intent(DescActivity.this, AnimeListActivity.class).putExtras(bundle));
                }
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
    }

    private LinearLayoutManager getLinearLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        return linearLayoutManager;
    }

    @OnClick(R.id.open_drama)
    public void dramaClick() {
        if (!mBottomSheetDialog.isShowing()) {
            mBottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            mBottomSheetDialog.show();
        }
    }


    @SuppressLint("RestrictedApi")
    public void openAnimeDesc() {
        animeImg.setImageDrawable(getDrawable(R.drawable.loading));
        toolbar.setTitle(Utils.getString(R.string.loading));
        tagContainerLayout.setVisibility(View.GONE);
        tagContainerLayout.setTags("");
        setTextviewEmpty(desc);
        mSwipe.setRefreshing(true);
        animeDescBeans = new AnimeDescBean();
        favorite.setVisible(false);
        bg.setImageDrawable(getResources().getDrawable(R.drawable.default_bg));
        mPresenter = new DescPresenter(siliUrl, this);
        mPresenter.loadData(true);
    }

    private void setTextviewEmpty(AppCompatTextView appCompatTextView) {
        appCompatTextView.setText("");
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
        cancelDialog();
        if (animeUrl.contains(".mp4") || animeUrl.contains(".m3u8")) {
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
            favorite.setIcon(R.drawable.baseline_star_white_48dp);
            favorite.setTitle(Utils.getString(R.string.remove_favorite));
            application.showSnackbarMsg(msg, Utils.getString(R.string.join_ok));
        } else {
            favorite.setIcon(R.drawable.baseline_star_border_white_48dp);
            favorite.setTitle(Utils.getString(R.string.favorite));
            application.showSnackbarMsg(msg, Utils.getString(R.string.join_error));
        }
    }

    public void setCollapsingToolbar() {
        Glide.with(DescActivity.this).asBitmap().load(animeDescHeaderBean.getImg()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (resource != null) {
                    DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.default_bg)
                            .error(R.drawable.default_bg)
                            .format(DecodeFormat.PREFER_RGB_565);
                    Glide.with(DescActivity.this)
                            .load(new BitmapDrawable(DescActivity.this.getResources(), EasyBlur.with(DescActivity.this)
                                    .bitmap(resource) //要模糊的图片
                                    .radius(10)//模糊半径
                                    .scale(4)//指定模糊前缩小的倍数
                                    .policy(EasyBlur.BlurPolicy.FAST_BLUR)//使用fastBlur
                                    .blur()))
                            .transition(DrawableTransitionOptions.withCrossFade(drawableCrossFadeFactory))
                            .apply(options)
                            .into(bg);
                }
            }
        });
        Utils.setDefaultImage(this, animeDescHeaderBean.getImg(), animeImg);
        toolbar.setTitle(animeDescHeaderBean.getName());
        if (animeDescHeaderBean.getTagTitles() != null) {
            tagContainerLayout.setTags(animeDescHeaderBean.getTagTitles());
            tagContainerLayout.setVisibility(View.VISIBLE);
        }else
            tagContainerLayout.setVisibility(View.GONE);
        tagContainerLayout.setVisibility(View.VISIBLE);
        desc.setText(animeDescHeaderBean.getDesc());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.down:
                if (Utils.isFastClick()) showDownDialog();
                break;
            case R.id.open_in_browser:
                if (Utils.isFastClick()) Utils.viewInChrome(this, siliUrl);
                break;
            case R.id.favorite:
                if (Utils.isFastClick()) favoriteAnime();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.desc_menu, menu);
        downView = menu.findItem(R.id.down);
        favorite = menu.findItem(R.id.favorite);
        return true;
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
        if (downView != null && downView.isVisible())
            downView.setVisible(false);
        if (favorite != null && favorite.isVisible())
            favorite.setVisible(false);
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
                if (!favorite.isVisible()) {
                    if (isFavorite) {
                        favorite.setIcon(R.drawable.baseline_star_white_48dp);
                        favorite.setTitle(Utils.getString(R.string.remove_favorite));
                    } else {
                        favorite.setIcon(R.drawable.baseline_star_border_white_48dp);
                        favorite.setTitle(Utils.getString(R.string.favorite));
                    }
                    favorite.setVisible(true);
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
                    dialog.dismiss();
                }, 1);
    }

    @Override
    public void onSniffingError(View webView, String url, int errorCode) {
        application.showToastMsg(Utils.getString(R.string.open_web_view));
        VideoUtils.openDefaultWebview(this, dramaUrl);
    }
}
