package my.project.silisili.main.animelist;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import my.project.silisili.R;
import my.project.silisili.application.Silisili;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.net.HttpGet;
import my.project.silisili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AnimeListModel implements AnimeListContract.Model{

    @Override
    public void getData(String url, int page, boolean isMain, AnimeListContract.LoadDataCallback callback) {
        if (page != 0) {
            //如果不是第一页
            if (url.contains("anime"))
                url = url.contains(Silisili.DOMAIN) ? url + page : Silisili.DOMAIN + url + page;
            else if (url.contains("riyu") || url.contains("guoyu") || url.contains("yingyu") || url.contains("yueyu")) {
                page += 1;
                url = url.contains(Silisili.DOMAIN) ? url + "index_" + page + ".html" : Silisili.DOMAIN + url + "index_" + page + ".html";
            }
            else if (url.contains("tags"))
                url = url.contains(Silisili.DOMAIN) ? url + "&page=" + page: Silisili.DOMAIN + url + "&page=" + page;
        }
        new HttpGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try{
                    Document body = Jsoup.parse(response.body().string());
                    Elements animeList = body.getElementsByClass("anime_list").select("dl");
                    if (animeList.size() > 0) {
                        if (isMain) {
                            Elements pages = body.select("div.page > a");
                            String[] hrefArr = pages.get(pages.size()-1).attr("href").split("/");
                            callback.pageCount(Integer.parseInt(hrefArr[hrefArr.length-1].replaceAll("[^0-9]", "")));
                        }

                        List<AnimeDescHeaderBean> list = new ArrayList<>();
                        for (int i = 0; i < animeList.size(); i++) {
                            AnimeDescHeaderBean bean = new AnimeDescHeaderBean();
                            bean.setName(animeList.get(i).select("h3").text());
                            bean.setImg(animeList.get(i).select("dt").select("img").attr("src").contains("http") ? animeList.get(i).select("dt").select("img").attr("src") : Silisili.DOMAIN + animeList.get(i).select("dt").select("img").attr("src"));
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
                        callback.error(isMain, Utils.getString(R.string.no_resources));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    callback.error(isMain, Utils.getString(R.string.parsing_error));
                }
            }
        });
    }
}
