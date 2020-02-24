package my.project.silisili.main.home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedHashMap;

import my.project.silisili.R;
import my.project.silisili.net.HttpGet;
import my.project.silisili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeModel implements HomeContract.Model {
    private static final String[] TABS = Utils.getArray(R.array.week_array);

    @Override
    public void getData(final HomeContract.LoadDataCallback callback) {
        new HttpGet(my.project.silisili.application.Silisili.DOMAIN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    LinkedHashMap map = new LinkedHashMap();
                    JSONObject weekObj = new JSONObject();
                    Document body = Jsoup.parse(response.body().string());
                        map.put("url", body.select("ul.nav_lef > li").get(1).select("a").get(0).attr("href"));
                        map.put("title",  body.select("ul.nav_lef > li").get(1).select("a").get(0).text());
                        setDataToJson(TABS[0], body.select("div.xfswiper0 >div.swiper-wrapper >div.swiper-slide >ul.clear > li"), weekObj);
                        setDataToJson(TABS[1], body.select("div.xfswiper1 >div.swiper-wrapper >div.swiper-slide >ul.clear > li"), weekObj);
                        setDataToJson(TABS[2], body.select("div.xfswiper2 >div.swiper-wrapper >div.swiper-slide >ul.clear > li"), weekObj);
                        setDataToJson(TABS[3], body.select("div.xfswiper3 >div.swiper-wrapper >div.swiper-slide >ul.clear > li"), weekObj);
                        setDataToJson(TABS[4], body.select("div.xfswiper4 >div.swiper-wrapper >div.swiper-slide >ul.clear > li"), weekObj);
                        setDataToJson(TABS[5], body.select("div.xfswiper5 >div.swiper-wrapper >div.swiper-slide >ul.clear > li"), weekObj);
                        setDataToJson(TABS[6], body.select("div.xfswiper6 >div.swiper-wrapper >div.swiper-slide >ul.clear > li"), weekObj);
                        map.put("week", weekObj);
                        callback.success(map);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.error(Utils.getString(R.string.parsing_error));
                }
            }
        });
    }

    /**
     * 新番时间表
     *
     * @param title
     * @param els
     * @param jsonObject
     * @throws JSONException
     */
    public static void setDataToJson(String title, Elements els, JSONObject jsonObject) throws JSONException {
        JSONArray arr = new JSONArray();
        for (int i = 0, size = els.size(); i < size; i++) {
            JSONObject object = new JSONObject();
            object.put("title", els.get(i).select("img").attr("alt"));
            object.put("img", els.get(i).select("img").attr("src"));
            object.put("url", els.get(i).select("a").attr("href"));
            object.put("drama", els.get(i).select("i") == null ? "" : els.get(i).select("i").text());
            object.put("new", els.get(i).select("b").text().equals("new"));
            arr.put(object);
        }
        jsonObject.put(title, arr);
    }
}
