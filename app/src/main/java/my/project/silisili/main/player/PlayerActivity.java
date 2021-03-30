package my.project.silisili.main.player;

import android.app.PictureInPictureParams;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fanchen.sniffing.SniffingUICallback;
import com.fanchen.sniffing.SniffingVideo;
import com.fanchen.sniffing.web.SniffingUtil;
import com.google.android.material.button.MaterialButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import my.project.silisili.R;
import my.project.silisili.adapter.AnimeDescDramaAdapter;
import my.project.silisili.application.Silisili;
import my.project.silisili.bean.AnimeDescDetailsBean;
import my.project.silisili.bean.Event;
import my.project.silisili.main.base.BaseActivity;
import my.project.silisili.main.base.Presenter;
import my.project.silisili.main.video.VideoContract;
import my.project.silisili.main.video.VideoPresenter;
import my.project.silisili.util.SharedPreferencesUtils;
import my.project.silisili.util.StatusBarUtil;
import my.project.silisili.util.Utils;
import my.project.silisili.util.VideoUtils;

public class PlayerActivity extends BaseActivity implements VideoContract.View, JZPlayer.CompleteListener, JZPlayer.TouchListener, SniffingUICallback {
    @BindView(R.id.player)
    JZPlayer player;
    private String witchTitle, url, siliUrl;
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    private List<AnimeDescDetailsBean> list = new ArrayList<>();
    private AnimeDescDramaAdapter dramaAdapter;
    private AlertDialog alertDialog;
    private String animeTitle;
    @BindView(R.id.nav_view)
    LinearLayout linearLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.pic_config)
    RelativeLayout picConfig;
    private VideoPresenter presenter;
    private boolean isPip = false;

    @BindView(R.id.nav_config_view)
    LinearLayout navConfigView;
    @BindView(R.id.speed)
    TextView speedTextView;
    private String[] speeds = Utils.getArray(R.array.speed_item);
    private int userSpeed = 2;

    @Override
    protected Presenter createPresenter() {
        return null;
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_play;
    }

    @Override
    protected void init() {
        Silisili.addDestoryActivity(this, "player");
        hideGap();
        Bundle bundle = getIntent().getExtras();
        init(bundle);
        initAdapter();
        initUserConfig();
    }

    @Override
    protected void initBeforeView() {
        StatusBarUtil.setTranslucent(this, 0);
    }

    private void init(Bundle bundle) {
        //播放地址
        url = bundle.getString("url");
        //集数名称
        witchTitle = bundle.getString("title");
        //番剧名称
        animeTitle = bundle.getString("animeTitle");
//        titleView.setText(animeTitle);
        //源地址
        siliUrl = bundle.getString("sili");
        //剧集list
        list = (List<AnimeDescDetailsBean>) bundle.getSerializable("list");
        //禁止冒泡
        linearLayout.setOnClickListener(view -> {
            return;
        });
        navConfigView.setOnClickListener(view -> {
            return;
        });
        linearLayout.getBackground().mutate().setAlpha(150);
        navConfigView.getBackground().mutate().setAlpha(150);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    player.goOnPlayOnPause();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                    player.goOnPlayOnResume();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        player.config.setOnClickListener(v -> {
            if (!Utils.isFastClick()) return;
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else drawerLayout.openDrawer(GravityCompat.START);
        });
        player.setListener(this, this, this);
        player.backButton.setOnClickListener(v -> finish());
        // 加载视频失败，嗅探视频
        player.snifferBtn.setOnClickListener(v -> sniffer(true));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) picConfig.setVisibility(View.GONE);
        else picConfig.setVisibility(View.VISIBLE);
        if (gtSdk23()) player.tvSpeed.setVisibility(View.VISIBLE);
        else player.tvSpeed.setVisibility(View.GONE);
        player.fullscreenButton.setOnClickListener(view -> {
            if (!Utils.isFastClick()) return;
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
            else drawerLayout.openDrawer(GravityCompat.END);
        });
        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        Jzvd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        player.playingShow();
        checkPlayUrl();
    }

    public void startPic() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(this::enterPicInPic, 500);
    }

    public void initAdapter() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        dramaAdapter = new AnimeDescDramaAdapter(this, list);
        recyclerView.setAdapter(dramaAdapter);
        dramaAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
//            setResult(0x20);
            drawerLayout.closeDrawer(GravityCompat.END);
            AnimeDescDetailsBean bean = (AnimeDescDetailsBean) adapter.getItem(position);
            Jzvd.releaseAllVideos();
            alertDialog = Utils.getProDialog(PlayerActivity.this, R.string.parsing);
            MaterialButton materialButton = (MaterialButton) adapter.getViewByPosition(recyclerView, position, R.id.tag_group);
            materialButton.setTextColor(getResources().getColor(R.color.tabSelectedTextColor));
            bean.setSelected(true);
            EventBus.getDefault().post(new Event(position));
            siliUrl = VideoUtils.getSiliUrl(bean.getUrl());
            witchTitle = animeTitle + " - " + bean.getTitle();
            presenter = new VideoPresenter(animeTitle, siliUrl, PlayerActivity.this);
            presenter.loadData(true);
        });
    }

    /**
     * 播放视频
     * @param animeUrl
     */
    private void playAnime(String animeUrl) {
        cancelDialog();
        url = animeUrl;
        /*switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
            case 0:
                //调用播放器
                Jzvd.releaseAllVideos();
                player.currentSpeedIndex = 1;
                player.setUp(url, witchTitle, Jzvd.SCREEN_FULLSCREEN, JZExoPlayer.class);
                player.startVideo();
                break;
            case 1:
                Jzvd.releaseAllVideos();
                Utils.selectVideoPlayer(PlayerActivity.this, url);
                break;
        }*/
        Jzvd.releaseAllVideos();
        player.currentSpeedIndex = 1;
        player.setUp(url, witchTitle, Jzvd.SCREEN_FULLSCREEN, JZExoPlayer.class);
        player.startVideo();
    }

    private void checkPlayUrl() {
        if (url.contains("mp4") || url.contains("m3u8"))
            play(url);
        else
            sniffer(false);
    }

    /**
     * 播放视频
     * @param playUrl
     */
    private void play(String playUrl) {
        Jzvd.releaseAllVideos();
        player.currentSpeedIndex = 1;
        player.setUp(playUrl, witchTitle, Jzvd.SCREEN_FULLSCREEN, JZExoPlayer.class);
        player.startVideo();
    }


    /**
     * 嗅探视频真实连接
     */
    private void sniffer(boolean showDialog) {
        if (showDialog)
            alertDialog = Utils.getProDialog(PlayerActivity.this, R.string.should_be_used_web);
        SniffingUtil.get().activity(this).referer(url).callback(this).url(url).start();
    }

    private void initUserConfig() {
        switch ((Integer) SharedPreferencesUtils.getParam(this, "user_speed", 15)) {
            case 5:
                setUserSpeedConfig(speeds[0], 0);
                break;
            case 10:
                setUserSpeedConfig(speeds[1], 1);
                break;
            case 15:
                setUserSpeedConfig(speeds[2], 2);
                break;
            case 30:
                setUserSpeedConfig(speeds[3], 3);
                break;
        }
    }

    private void setUserSpeedConfig(String text, int speed) {
        speedTextView.setText(text);
        userSpeed = speed;
    }

    private void setDefaultSpeed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Utils.getString(R.string.set_user_speed));
        builder.setSingleChoiceItems(speeds, userSpeed, (dialog, which) -> {
            switch (which) {
                case 0:
                    SharedPreferencesUtils.setParam(getApplicationContext(), "user_speed", 5);
                    setUserSpeedConfig(speeds[0], which);
                    break;
                case 1:
                    SharedPreferencesUtils.setParam(getApplicationContext(), "user_speed", 10);
                    setUserSpeedConfig(speeds[1], which);
                    break;
                case 2:
                    SharedPreferencesUtils.setParam(getApplicationContext(), "user_speed", 15);
                    setUserSpeedConfig(speeds[2], which);
                    break;
                case 3:
                    SharedPreferencesUtils.setParam(getApplicationContext(), "user_speed", 30);
                    setUserSpeedConfig(speeds[3], which);
                    break;
            }
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @OnClick({R.id.speed_config, R.id.pic_config, R.id.player_config, R.id.browser_config})
    public void configBtnClick(RelativeLayout relativeLayout) {
        switch (relativeLayout.getId()) {
            case R.id.speed_config:
                setDefaultSpeed();
                break;
            case R.id.pic_config:
                if (gtSdk26()) startPic();
                break;
            case R.id.player_config:
                Utils.selectVideoPlayer(this, url);
                break;
            case R.id.browser_config:
                Utils.viewInChrome(this, siliUrl);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.closeDrawer(GravityCompat.END);
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!inMultiWindow()) player.goOnPlayOnPause();
        else player.goOnPlayOnResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavBar();
        if (!inMultiWindow()) player.goOnPlayOnResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPip) finish();
    }

    /**
     * 是否为分屏模式
     *
     * @return
     */
    public boolean inMultiWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return this.isInMultiWindowMode();
        else return false;
    }

    /**
     * Android 8.0 画中画
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterPicInPic() {
//        PictureInPictureParams.Builder builder = new PictureInPictureParams.Builder();
        // 设置宽高比例值，第一个参数表示分子，第二个参数表示分母
        // 下面的10/5=2，表示画中画窗口的宽度是高度的两倍
//        Rational aspectRatio = new Rational(10,5);
        // 设置画中画窗口的宽高比例
//        builder.setAspectRatio(aspectRatio);
        // 进入画中画模式，注意enterPictureInPictureMode是Android8.0之后新增的方法
//        enterPictureInPictureMode(builder.build());
        PictureInPictureParams builder = new PictureInPictureParams.Builder().build();
        enterPictureInPictureMode(builder);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            player.startPIP();
            isPip = true;
            player.goOnPlayOnResume();
        } else isPip = false;
    }

    @Override
    public void cancelDialog() {
        Utils.cancelDialog(alertDialog);
    }

    @Override
    public void getVideoSuccess(String url) {
        runOnUiThread(() -> {
            hideNavBar();
            this.url = url;
            checkPlayUrl();
        });
    }

    @Override
    public void getIframeUrl(String iframeUrl) {
        runOnUiThread(() -> {
//            application.showToastMsg(Utils.getString(R.string.should_be_used_web));
//            SniffingUtil.get().activity(this).referer(iframeUrl).callback(this).url(iframeUrl).start();
            url = iframeUrl;
            checkPlayUrl();
        });
    }

    @Override
    public void getVideoEmpty() {
        runOnUiThread(() -> {
            application.showToastMsg(Utils.getString(R.string.open_web_view));
            VideoUtils.openDefaultWebview(this, siliUrl);
            finish();
        });
    }

    @Override
    public void getVideoError() {
        //网络出错
        runOnUiThread(() -> {
            hideNavBar();
            application.showErrorToastMsg(Utils.getString(R.string.error_700));
        });
    }

    @Override
    protected void onDestroy() {
        if (null != presenter) presenter.detachView();
        JzvdStd.releaseAllVideos();
        super.onDestroy();
    }

    @Override
    public void complete() {
        application.showSuccessToastMsg("播放完毕");
        if (!drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public void touch() {
        hideNavBar();
    }

    @Override
    public void onSniffingStart(View webView, String url) {

    }

    @Override
    public void onSniffingFinish(View webView, String url) {
        SniffingUtil.get().releaseWebView();
        cancelDialog();
        hideNavBar();
    }

    @Override
    public void onSniffingSuccess(View webView, String url, List<SniffingVideo> videos) {
        List<String> urls = Utils.ridRepeat(videos);
        if (urls.size() > 1)
            VideoUtils.showMultipleVideoSources(this,
                    urls,
                    (dialog, index) -> playAnime(urls.get(index)),
                    (dialog, which) -> dialog.dismiss(), 1);
        else playAnime(urls.get(0));
    }

    @Override
    public void onSniffingError(View webView, String url, int werrorCode) {
        application.showToastMsg(Utils.getString(R.string.open_web_view));
        VideoUtils.openDefaultWebview(this, siliUrl);
        finish();
    }

    @Override
    public void finish() {
        if (null != presenter) presenter.detachView();
        JzvdStd.releaseAllVideos();
        super.finish();
    }
}
