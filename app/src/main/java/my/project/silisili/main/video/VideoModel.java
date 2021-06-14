package my.project.silisili.main.video;

import android.util.Log;
import android.webkit.URLUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import my.project.silisili.application.Silisili;
import my.project.silisili.database.DatabaseUtil;
import my.project.silisili.net.HttpGet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VideoModel implements VideoContract.Model {
    private final static Pattern PLAY_URL_PATTERN = Pattern.compile("(https?|ftp|file):\\/\\/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");

    @Override
    public void getData(String title, String HTML_url, VideoContract.LoadDataCallback callback) {
        Log.e("playHtml",HTML_url);
        new HttpGet(HTML_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Document doc = Jsoup.parse(response.body().string());
                    String fid = DatabaseUtil.getAnimeID(title);
                    DatabaseUtil.addIndex(fid, HTML_url.replaceAll(Silisili.DOMAIN, ""));
                    String iframeUrl = doc.select("iframe").attr("src");
                    Log.e("iframeUrl",iframeUrl);
                    if (iframeUrl.isEmpty() ||  !URLUtil.isValidUrl(iframeUrl)) callback.empty();
                    else {
                        // 解析
                        String host = iframeUrl;
                        java.net.URL urlHost;
                        try {
                            urlHost = new java.net.URL(iframeUrl);
                            host = urlHost.getHost();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        new HttpGet(iframeUrl, host, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                callback.error();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Document doc = Jsoup.parse(response.body().string());
                                String source = doc.select("source").attr("src");
                                if (source.isEmpty()) {
                                    Elements scripts = doc.select("script");
                                    for (Element element : scripts) {
                                        if (element.html().contains("var url")) {
                                            Matcher m = PLAY_URL_PATTERN.matcher(element.html());
                                            if (m.find()) {
                                                source = m.group();
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (source.isEmpty()) callback.sendIframeUrl(iframeUrl);
                                else callback.success(source);
                            }
                        });
                    }
                } catch (SocketTimeoutException e) {
                    callback.error();
                }
            }
        });
    }
}
