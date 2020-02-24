package my.project.silisili.net;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpPost {
    private final static int connectTimeout = 10;
    private final static int readTimeout = 20;

    public HttpPost(String url, FormBody body, Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(connectTimeout, TimeUnit.SECONDS).readTimeout(readTimeout, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public HttpPost(String url, String paramsStr, Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(connectTimeout, TimeUnit.SECONDS).readTimeout(readTimeout, TimeUnit.SECONDS).build();
        MediaType MEDIA_TYPE_NORAML_FORM = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_NORAML_FORM, paramsStr);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
