package my.project.silisili.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import java.io.Serializable;
import java.util.List;

import my.project.silisili.R;
import my.project.silisili.application.Silisili;
import my.project.silisili.bean.AnimeDescDetailsBean;
import my.project.silisili.main.player.PlayerActivity;
import my.project.silisili.main.webview.normal.DefaultNormalWebActivity;
import my.project.silisili.main.webview.x5.DefaultX5WebActivity;

public class VideoUtils {
    private static AlertDialog alertDialog;

    /**
     * 发现多个播放地址时弹窗
     *
     * @param context
     * @param list
     * @param listener
     * @param type 0 old 1 new
     */
    public static void showMultipleVideoSources(Context context,
                                                List<String> list,
                                                DialogInterface.OnClickListener listener, DialogInterface.OnClickListener listener2, int type) {
        String[] items = new String[list.size()];
        for (int i = 0, size = list.size(); i < size; i++) {
            if (type == 0) items[i] = getSiliUrl(list.get(i));
            else items[i] = list.get(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
        builder.setTitle(Utils.getString(R.string.select_video_source));
        builder.setCancelable(false);
        builder.setItems(items, listener);
        builder.setNegativeButton(Utils.getString(R.string.play_not_found_negative), listener2);
        alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 打开播放器
     *
     * @param isDescActivity
     * @param activity
     * @param witchTitle
     * @param url
     * @param animeTitle
     * @param siliUrl
     * @param list
     */
    public static void openPlayer(boolean isDescActivity, Activity activity, String witchTitle, String url, String animeTitle, String siliUrl, List<AnimeDescDetailsBean> list) {
        Bundle bundle = new Bundle();
        bundle.putString("title", witchTitle);
        bundle.putString("url", url);
        bundle.putString("animeTitle", animeTitle);
        bundle.putString("sili", siliUrl);
        bundle.putSerializable("list", (Serializable) list);
        Silisili.destoryActivity("player");
        if (isDescActivity)
            activity.startActivityForResult(new Intent(activity, PlayerActivity.class).putExtras(bundle), 0x10);
        else {
            activity.startActivity(new Intent(activity, PlayerActivity.class).putExtras(bundle));
            activity.finish();
        }
    }

    /**
     * 获取链接
     *
     * @param url
     * @return
     */
    public static String getSiliUrl(String url) {
        return url.startsWith("http") ? url : Silisili.DOMAIN + url;
    }

    /**
     * 打开常规webview
     *
     * @param activity
     * @param url
     */
    public static void openDefaultWebview(Activity activity, String url) {
        if (Utils.loadX5())
            activity.startActivity(new Intent(activity, DefaultX5WebActivity.class).putExtra("url",url));
        else
            activity.startActivity(new Intent(activity, DefaultNormalWebActivity.class).putExtra("url",url));
    }
}
