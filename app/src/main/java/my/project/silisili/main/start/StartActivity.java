package my.project.silisili.main.start;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import my.project.silisili.R;
import my.project.silisili.api.Api;
import my.project.silisili.main.base.BaseActivity;
import my.project.silisili.main.base.Presenter;
import my.project.silisili.main.home.HomeActivity;
import my.project.silisili.net.HttpGet;
import my.project.silisili.util.SharedPreferencesUtils;
import my.project.silisili.util.StatusBarUtil;
import my.project.silisili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StartActivity extends BaseActivity {
    @BindView(R.id.check_update)
    LinearLayout linearLayout;
    @BindView(R.id.view_need_offset)
    CoordinatorLayout coordinatorLayout;
    private String downUrl;

    @Override
    protected Presenter createPresenter() {
        return null;
    }

    @Override
    protected void loadData() {}

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_start;
    }

    @Override
    protected void init() {
        StatusBarUtil.setTranslucentForCoordinatorLayout(this, 0);
        StatusBarUtil.setTranslucentForImageView(this, 0, coordinatorLayout);
        SharedPreferencesUtils.setParam(this,"initX5","init");
        RelativeLayout.LayoutParams Params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        Params.setMargins(0,0,0, Utils.getNavigationBarHeight(this));
        linearLayout.setLayoutParams(Params);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            linearLayout.setVisibility(View.VISIBLE);
            checkUpdate();
        }, 1000);
    }

    @Override
    protected void initBeforeView() {}

    private void checkUpdate() {
        new HttpGet(Api.CHECK_UPDATE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    application.showErrorToastMsg(Utils.getString(R.string.ck_network_error));
                    openMain();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject obj = new JSONObject(json);
                    String newVersion = obj.getString("tag_name");
                    if (newVersion.equals(Utils.getASVersionName()))
                        runOnUiThread(() -> openMain());
                    else {
                        runOnUiThread(() -> linearLayout.setVisibility(View.GONE));
                        downUrl = obj.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                        String body = obj.getString("body");
                        runOnUiThread(() -> Utils.findNewVersion(StartActivity.this,
                                newVersion,
                                body,
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    Utils.putTextIntoClip(downUrl);
                                    application.showSuccessToastMsg(Utils.getString(R.string.url_copied));
                                    Utils.viewInChrome(StartActivity.this, downUrl);
                                },
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    openMain();
                                })
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        application.showErrorToastMsg(Utils.getString(R.string.ck_error_start));
                        openMain();
                    });
                }
            }
        });
    }

    private void openMain() {
        startActivity(new Intent(StartActivity.this, HomeActivity.class));
        StartActivity.this.finish();
    }
}
