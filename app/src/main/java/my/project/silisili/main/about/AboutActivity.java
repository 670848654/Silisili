package my.project.silisili.main.about;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import my.project.silisili.R;
import my.project.silisili.adapter.LogAdapter;
import my.project.silisili.api.Api;
import my.project.silisili.application.Silisili;
import my.project.silisili.bean.LogBean;
import my.project.silisili.main.base.BaseActivity;
import my.project.silisili.main.base.Presenter;
import my.project.silisili.net.DownloadUtil;
import my.project.silisili.net.HttpGet;
import my.project.silisili.util.SharedPreferencesUtils;
import my.project.silisili.util.SwipeBackLayoutUtil;
import my.project.silisili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AboutActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cache)
    TextView cache;
    @BindView(R.id.version)
    TextView version;
    private ProgressDialog p;
    private  String downloadUrl;
    private Call downCall;
    @BindView(R.id.footer)
    LinearLayout footer;
    @BindView(R.id.show)
    CoordinatorLayout show;

    @Override
    protected Presenter createPresenter() {
        return null;
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_about;
    }

    @Override
    protected void init() {
        Slidr.attach(this, Utils.defaultInit());
        initToolbar();
        initViews();
    }

    @Override
    protected void initBeforeView() {
        SwipeBackLayoutUtil.convertActivityToTranslucent(this);
    }

    public void initToolbar(){
        toolbar.setTitle(Utils.getString(R.string.about));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void initViews(){
        LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.getNavigationBarHeight(this));
        footer.setLayoutParams(Params);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) show.getLayoutParams();
        params.setMargins(0, 0, 0, Utils.getNavigationBarHeight(this) - 15);
        version.setText(Utils.getASVersionName());
        cache.setText(Environment.getExternalStorageDirectory() + Utils.getString(R.string.cache_text));
    }

    @OnClick({R.id.silisili,R.id.github,R.id.check_update})
    public void openBrowser(RelativeLayout relativeLayout) {
        switch (relativeLayout.getId()) {
            case R.id.silisili:
                if (Utils.isFastClick()) Utils.viewInChrome(this, Silisili.DOMAIN);
                break;
            case R.id.github:
                if (Utils.isFastClick()) Utils.viewInChrome(this, Utils.getString(R.string.github_url));
                break;
             case R.id.check_update:
                 if (Utils.isFastClick()) checkUpdate();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        MenuItem updateLogItem = menu.findItem(R.id.update_log);
        MenuItem openSourceItem = menu.findItem(R.id.open_source);
        if (!(Boolean) SharedPreferencesUtils.getParam(this, "darkTheme", false)) {
            updateLogItem.setIcon(R.drawable.baseline_insert_chart_outlined_black_48dp);
            openSourceItem.setIcon(R.drawable.baseline_all_inclusive_black_48dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_log:
                if (Utils.isFastClick()) showUpdateLogs();
                break;
            case R.id.open_source:
                if (Utils.isFastClick()) startActivity(new Intent(AboutActivity.this,OpenSourceActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showUpdateLogs() {
        AlertDialog alertDialog;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_log, null);
        RecyclerView logs = view.findViewById(R.id.rv_list);
        logs.setLayoutManager(new LinearLayoutManager(this));
        LogAdapter logAdapter = new LogAdapter(createUpdateLogList());
        logs.setAdapter(logAdapter);
        builder.setPositiveButton(Utils.getString(R.string.page_positive), null);
        TextView title = new TextView(this);
        title.setText(Utils.getString(R.string.update_log));
        title.setPadding(30,30,30,30);
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        title.setGravity(Gravity.LEFT);
        title.setTextSize(18);
        title.setTextColor(getResources().getColor(R.color.text_color_primary));
        builder.setCustomTitle(title);
        alertDialog = builder.setView(view).create();
        alertDialog.show();
    }

    public List createUpdateLogList() {
        List logsList = new ArrayList();
        logsList.add(new LogBean("1.0-beta6", "2020年5月30日", "修复一些Bug\n优化番剧详情界面\n内置播放器新增屏幕锁定、快进、后退操作"));
        logsList.add(new LogBean("1.0-beta5", "2020年5月27日", "修复解析时弹窗不关闭的问题"));
        logsList.add(new LogBean("1.0-beta4", "2020年5月18日", "修复已知问题"));
        logsList.add(new LogBean("1.0-beta3", "2020年4月28日", "修复番剧详情中的显示Bug"));
        logsList.add(new LogBean("1.0-beta2", "2020年4月16日", "修复一些Bug\n部分界面UI改动"));
        logsList.add(new LogBean("1.0-beta1", "2020年2月18日", "第一个beta版本"));
        return logsList;
    }

    public void checkUpdate() {
        p = Utils.getProDialog(this, R.string.check_update_text);
        new Handler().postDelayed(() -> new HttpGet(Api.CHECK_UPDATE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Utils.cancelProDialog(p);
                    application.showSnackbarMsgAction(show, Utils.getString(R.string.network_error), Utils.getString(R.string.try_again), v -> checkUpdate());
                });
            }



            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject obj = new JSONObject(json);
                    String newVersion = obj.getString("tag_name");
                    if (newVersion.equals(Utils.getASVersionName()))
                        runOnUiThread(() -> {
                            Utils.cancelProDialog(p);
                            application.showSnackbarMsg(show, Utils.getString(R.string.no_new_version));
                        });
                    else {
                        downloadUrl = obj.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                        String body = obj.getString("body");
                        runOnUiThread(() -> {
                            Utils.cancelProDialog(p);
                           Utils.findNewVersion(AboutActivity.this,
                                   newVersion,
                                   body,
                                   (dialog, which) -> download(),
                                   (dialog, which) -> dialog.dismiss()
                                   );
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }), 1000);
    }

    public void download() {
        p = Utils.showProgressDialog(AboutActivity.this);
        p.setButton(ProgressDialog.BUTTON_NEGATIVE, Utils.getString(R.string.page_negative), (dialog1, which1) -> {
            if (null != downCall)
                downCall.cancel();
            dialog1.dismiss();
        });
        p.show();
        downNewVersion(downloadUrl);
    }

    /**
     * 下载apk
     * @param url 下载地址
     */
    private void downNewVersion(String url) {
        downCall = DownloadUtil.get().downloadApk(url, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(final String fileName) {
                runOnUiThread(() -> {
                    Utils.cancelProDialog(p);
                    Utils.startInstall(AboutActivity.this);
                });
            }
            @Override
            public void onDownloading(final int progress) {
                runOnUiThread(() -> p.setProgress(progress));
            }
            @Override
            public void onDownloadFailed() {
                runOnUiThread(() -> {
                    Utils.cancelProDialog(p);
                    application.showSnackbarMsgAction(show, Utils.getString(R.string.download_error), Utils.getString(R.string.try_again), v -> download());
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            Utils.startInstall(AboutActivity.this);
        }
    }
}
