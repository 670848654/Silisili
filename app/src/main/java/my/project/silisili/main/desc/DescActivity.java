package my.project.silisili.main.desc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.r0adkll.slidr.Slidr;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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
import my.project.silisili.bean.Event;
import my.project.silisili.bean.Refresh;
import my.project.silisili.custom.MyTextView;
import my.project.silisili.database.DatabaseUtil;
import my.project.silisili.main.animelist.AnimeListActivity;
import my.project.silisili.main.base.BaseActivity;
import my.project.silisili.main.video.VideoContract;
import my.project.silisili.main.video.VideoPresenter;
import my.project.silisili.util.StatusBarUtil;
import my.project.silisili.util.SwipeBackLayoutUtil;
import my.project.silisili.util.Utils;
import my.project.silisili.util.VideoUtils;

public class DescActivity extends BaseActivity<DescContract.View, DescPresenter> implements DescContract.View, VideoContract.View {
    @BindView(R.id.details_list)
    RecyclerView detailsRv;
    @BindView(R.id.recommend_list)
    RecyclerView recommendRv;
    @BindView(R.id.anime_img)
    ImageView animeImg;
    @BindView(R.id.desc)
    ExpandableTextView desc;
    @BindView(R.id.title)
    MyTextView title;
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
    private AlertDialog alertDialog;
    private boolean isFavorite;
    private VideoPresenter videoPresenter;
    private AnimeDescHeaderBean animeDescHeaderBean = new AnimeDescHeaderBean();
    private List<String> animeUrlList = new ArrayList();
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
    @BindView(R.id.favorite)
    MaterialButton favorite;
    @BindView(R.id.down)
    MaterialButton down;
    @BindView(R.id.tag_view)
    TagContainerLayout tagContainerLayout;
    @BindView(R.id.scrollview)
    NestedScrollView scrollView;
    private int clickIndex; // 当前点击剧集

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
        EventBus.getDefault().register(this);
        StatusBarUtil.setColorForSwipeBack(this, getResources().getColor(R.color.colorPrimaryDark), 0);
        if (isDarkTheme) bg.setVisibility(View.GONE);
        Slidr.attach(this, Utils.defaultInit());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) msg.getLayoutParams();
        params.setMargins(10, 0, 10, 0);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> mSwipe.setEnabled(scrollView.getScrollY() == 0));
        desc.setNeedExpend(true);
        getBundle();
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

    public void initSwipe() {
        mSwipe.setColorSchemeResources(R.color.pink500, R.color.blue500, R.color.purple500);
        mSwipe.setProgressViewOffset(true, -0, 150);
        mSwipe.setOnRefreshListener(() -> mPresenter.loadData(true));
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
        lineRecyclerView.setLayoutManager(new GridLayoutManager(this, Utils.isPad() ? 8 : 4));
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

    @OnClick({R.id.browser})
    public void openBrowser() {
        Utils.viewInChrome(this, siliUrl);
    }


    @SuppressLint("RestrictedApi")
    public void openAnimeDesc() {
        animeImg.setImageDrawable(getDrawable(isDarkTheme ? R.drawable.loading_night : R.drawable.loading_light));
        tagContainerLayout.setVisibility(View.GONE);
        tagContainerLayout.setTags("");
        setTextviewEmpty(desc);
        animeDescBeans = new AnimeDescBean();
        favorite.setVisibility(View.GONE);
        down.setVisibility(View.GONE);
        bg.setImageDrawable(getResources().getDrawable(R.drawable.default_bg));
        mPresenter = new DescPresenter(siliUrl, this);
        mPresenter.loadData(true);
    }

    private void setTextviewEmpty(AppCompatTextView appCompatTextView) {
        appCompatTextView.setText("");
    }

    public void playVideo(BaseQuickAdapter adapter, int position, AnimeDescDetailsBean bean, RecyclerView recyclerView) {
        alertDialog = Utils.getProDialog(DescActivity.this, R.string.parsing);
        MaterialButton materialButton = (MaterialButton) adapter.getViewByPosition(recyclerView, position, R.id.tag_group);
        materialButton.setTextColor(getResources().getColor(R.color.tabSelectedTextColor));
        bean.setSelected(true);
        clickIndex = position;
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
/*        switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
            case 0:
                //调用播放器
                VideoUtils.openPlayer(true, this, witchTitle, animeUrl, animeTitle, siliUrl, animeDescBeans.getAnimeDescDetailsBeans());
                break;
            case 1:
                Utils.selectVideoPlayer(this, animeUrl);
                break;
        }*/
        VideoUtils.openPlayer(true, this, witchTitle, animeUrl, animeTitle, dramaUrl, animeDescBeans.getAnimeDescDetailsBeans());
    }

/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x10 && resultCode == 0x20) {
            mSwipe.setRefreshing(true);
            mPresenter.loadData(true);
        }
    }*/

    @Override
    public void onBackPressed() {
        if (animeUrlList.size() == 1) super.onBackPressed();
        else {
            if (!mSwipe.isRefreshing()) {
                animeUrlList.remove(animeUrlList.size() - 1);
                siliUrl = animeUrlList.get(animeUrlList.size() - 1);
                openAnimeDesc();
            } else  application.showSnackbarMsg(msg, Utils.getString(R.string.load_desc_info));
        }
    }

    public void favoriteAnime() {
        setResult(200);
        isFavorite = DatabaseUtil.favorite(animeDescHeaderBean);
        favorite.setIcon(ContextCompat.getDrawable(this, isFavorite ? R.drawable.baseline_favorite_white_48dp : R.drawable.baseline_favorite_border_white_48dp));
        favorite.setText(isFavorite ? Utils.getString(R.string.has_favorite) : Utils.getString(R.string.favorite));
        application.showSnackbarMsg(msg, isFavorite ? Utils.getString(R.string.join_ok) : Utils.getString(R.string.join_error));
        EventBus.getDefault().post(new Refresh(1));
    }

    public void setCollapsingToolbar() {
        DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
/*        Glide.with(this).load(animeDescHeaderBean.getImg())
                .apply(RequestOptions.bitmapTransform( new BlurTransformation(25, 3)))
                .transition(DrawableTransitionOptions.withCrossFade(drawableCrossFadeFactory))
                .into(bg);*/
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(isDarkTheme ? R.drawable.loading_night : R.drawable.loading_light)
                .error(R.drawable.error);
        Glide.with(this)
                .load(animeDescHeaderBean.getImg())
                .transition(DrawableTransitionOptions.withCrossFade(drawableCrossFadeFactory))
                .apply(options)
                .into(bg);
        Utils.setDefaultImage(this, animeDescHeaderBean.getImg(), animeImg, false, null, null);
        title.setText(animeDescHeaderBean.getName());
        if (animeDescHeaderBean.getTagTitles() != null) {
            tagContainerLayout.setTags(animeDescHeaderBean.getTagTitles());
            tagContainerLayout.setVisibility(View.VISIBLE);
        }else
            tagContainerLayout.setVisibility(View.GONE);
        tagContainerLayout.setVisibility(View.VISIBLE);
        if (animeDescHeaderBean.getDesc().isEmpty())
            desc.setVisibility(View.GONE);
        else {
            desc.setContent(animeDescHeaderBean.getDesc());
            desc.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.favorite, R.id.down})
    public void setFavorite(View view) {
        switch (view.getId()) {
            case R.id.favorite:
                favoriteAnime();
                break;
            case R.id.down:
                showDownDialog();
                break;
        }
    }

    @OnClick(R.id.exit)
    public void exit() {
        finish();
    }

    @Override
    public void showLoadingView() {
        showEmptyVIew();
        detailsRv.scrollToPosition(0);
        recommendRv.scrollToPosition(0);
    }

    @Override
    public void showLoadErrorView(String msg) {
        runOnUiThread(() -> {
            if (!mActivityFinish) {
                mSwipe.setRefreshing(false);
                desc_view.setVisibility(View.GONE);
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
        if ( favorite.isShown())
            favorite.setVisibility(View.GONE);
        if (down.isShown())
            down.setVisibility(View.GONE);
        desc_view.setVisibility(View.GONE);
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
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.DialogStyle);
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
                setCollapsingToolbar();
                mSwipe.setRefreshing(false);
                if (isFavorite) DatabaseUtil.updateFavorite(animeDescHeaderBean);
                desc_view.setVisibility(View.VISIBLE);
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
                    favorite.setIcon(ContextCompat.getDrawable(this, isFavorite ? R.drawable.baseline_favorite_white_48dp : R.drawable.baseline_favorite_border_white_48dp));
                    favorite.setText(isFavorite ? Utils.getString(R.string.has_favorite) : Utils.getString(R.string.favorite));
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
        Utils.cancelDialog(alertDialog);
    }

    @Override
    public void getVideoSuccess(String url) {
        runOnUiThread(() -> playAnime(url));
    }

    @Override
    public void getIframeUrl(String iframeUrl) {
        runOnUiThread(() -> {
            Utils.cancelDialog(alertDialog);
//            application.showToastMsg(Utils.getString(R.string.should_be_used_web));
            VideoUtils.openPlayer(true, this, witchTitle, iframeUrl, animeTitle, dramaUrl, animeDescBeans.getAnimeDescDetailsBeans());
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
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        clickIndex = event.getClickIndex();
        animeDescBeans.getAnimeDescDetailsBeans().get(clickIndex).setSelected(true);
        animeDescDetailsAdapter.notifyDataSetChanged();
        animeDescDramaAdapter.notifyDataSetChanged();
    }
}
