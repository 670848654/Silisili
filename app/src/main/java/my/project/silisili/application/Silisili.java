package my.project.silisili.application;

import android.app.Activity;
import android.app.Application;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.tencent.smtt.sdk.QbSdk;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatDelegate;
import es.dmoral.toasty.Toasty;
import my.project.silisili.R;
import my.project.silisili.util.SharedPreferencesUtils;
import my.project.silisili.util.Utils;

public class Silisili extends Application {
    private static Silisili appContext;
    private List<Activity> oList;
    private static Map<String, Activity> destoryMap = new HashMap<>();
    public static String DOMAIN;
    public static String TAG = "%s";
    public String error;
    public JSONObject week = new JSONObject();

    public static Silisili getInstance() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if ((Boolean) SharedPreferencesUtils.getParam(this, "darkTheme", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        oList = new ArrayList<>();
        appContext = this;
        Utils.init(this);
        String web = (String) SharedPreferencesUtils.getParam(this, "domain", Utils.getString(R.string.domain_url));
        DOMAIN = web.toLowerCase().contains("dilidili") ? Utils.getString(R.string.domain) : web;
        setDomain();
        initTBS();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    public static void setDomain() {
        TAG = DOMAIN + "/anime/202001/";
    }

    private void initTBS() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.setDownloadWithoutWifi(true);//非wifi条件下允许下载X5内核
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                if (arg0) SharedPreferencesUtils.setParam(appContext, "X5State", true);
                else SharedPreferencesUtils.setParam(appContext, "X5State", false);
            }
            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    public void showToastMsg(String msg){
        Toasty.warning(getApplicationContext(), msg, Toast.LENGTH_LONG, true).show();
    }

    public void showSuccessToastMsg(String msg){
        Toasty.success(getApplicationContext(), msg, Toast.LENGTH_LONG, true).show();
    }

    public void showErrorToastMsg(String msg){
        Toasty.error(getApplicationContext(), msg, Toast.LENGTH_LONG, true).show();
    }

    public void showCustomToastMsg(String msg, @DrawableRes int iconRes, @ColorRes int color){
        Toasty.custom(this, msg,
                iconRes, color, Toast.LENGTH_LONG, true, true).show();
    }

    public void showSnackbarMsgAction(View view, String msg, String actionMsg, View.OnClickListener listener) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction(actionMsg, listener).show();
    }

    public void showSnackbarMsg(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    public void addActivity(Activity activity) {
        if (!oList.contains(activity)) {
            oList.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        if (oList.contains(activity)) {
            oList.remove(activity);
            activity.finish();
        }
    }

    public void removeALLActivity() {
        for (Activity activity : oList) {
            activity.finish();
        }
    }

    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        if (keySet.size() > 0) {
            for (String key : keySet) {
                if (activityName.equals(key)) {
                    destoryMap.get(key).finish();
                }
            }
        }
    }
}
