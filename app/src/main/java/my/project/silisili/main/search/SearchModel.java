package my.project.silisili.main.search;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import my.project.silisili.api.Api;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.net.HttpGet;
import my.project.silisili.net.HttpPost;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchModel implements SearchContract.Model {
    private final static Pattern SEARCHID_PATTERN = Pattern.compile("searchid=(.*)");
    @Override
    public void getData(String title, String seaechID, int page, boolean isMain, SearchContract.LoadDataCallback callback) {
        Log.e("page", page +"");
        if (page != 0) {
            // 如果不是第一页 则使用GET方式
            Log.e("分页GET", String.format(Api.SEARCH_GET_API, seaechID, page));
            new HttpGet(String.format(Api.SEARCH_GET_API, seaechID, page), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.error(isMain, e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    getSearchData(response, isMain, callback);
                }
            });
        }else {
            // 第一次使用POST获取searchID
            Log.e("第一页POST", "GO" + title);
            new HttpPost(Api.SEARCH_API,"show=title&tbname=movie&tempid=1&keyboard=" + title , new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.error(isMain, e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    getSearchData(response, isMain, callback);
                }
            });
        }

    }

    public void getSearchData(Response response, boolean isMain, SearchContract.LoadDataCallback callback) {
        try{
            Document body = Jsoup.parse(response.body().string());
            Elements animeList = body.getElementsByClass("anime_list").select("dl");
            if (animeList.size() > 0) {
                if (isMain) {
                    Elements pages = body.select("div.page > a");
                    if (pages.size() > 0) {
                        String page = pages.get(0).select("a").text().replaceAll(" ", "").replaceAll("下一页尾页", "");
                        page = page.substring(page.length() - 1);
                        Matcher m = SEARCHID_PATTERN.matcher(pages.get(0).select("a").attr("href"));
                        String searchID = "";
                        while (m.find()) {
                            searchID = m.group().replaceAll("searchid=","");
                            break;
                        }
                        callback.searchID(searchID);
                        callback.pageCount(Integer.parseInt(page));
                    }
                }
                List<AnimeDescHeaderBean> list = new ArrayList<>();
                for (int i = 0; i < animeList.size(); i++) {
                    AnimeDescHeaderBean bean = new AnimeDescHeaderBean();
                    bean.setName(animeList.get(i).select("h3").text());
                    bean.setImg(animeList.get(i).select("dt").select("img").attr("src").contains("http") ? animeList.get(i).select("dt").select("img").attr("src") : my.project.silisili.application.Silisili.DOMAIN + animeList.get(i).select("dt").select("img").attr("src"));
                    bean.setUrl(animeList.get(i).select("h3").select("a").attr("href"));
                    Elements label = animeList.get(i).getElementsByClass("d_label");
                    for (int k = 0;k < label.size(); k++){
                        String str = label.get(k).text();
                        if (str.contains("地区"))
                            bean.setRegion(str);
                        else if (str.contains("年代"))
                            bean.setYear(str);
                        else if (str.contains("标签"))
                            bean.setTag(str);
                    }
                    Elements p = animeList.get(i).select("p");
                    for (int j = 0;j < p.size(); j++){
                        String str = p.get(j).text();
                        if (str.contains("看点"))
                            bean.setShow(str);
                        else if (str.contains("简介"))
                            bean.setDesc(str);
                        else if (str.contains("状态"))
                            bean.setState(str);
                    }
                    list.add(bean);
                }
                callback.success(isMain, list);
            } else {
                callback.error(isMain, "没有搜索到相关信息");
            }
        }catch (Exception e){
            e.printStackTrace();
            callback.error(e.getMessage());
        }
    }

}
