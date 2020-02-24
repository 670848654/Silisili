package my.project.silisili.main.desc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import my.project.silisili.R;
import my.project.silisili.application.Silisili;
import my.project.silisili.bean.AnimeDescBean;
import my.project.silisili.bean.AnimeDescDetailsBean;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.bean.AnimeDescRecommendBean;
import my.project.silisili.bean.DownBean;
import my.project.silisili.database.DatabaseUtil;
import my.project.silisili.net.HttpGet;
import my.project.silisili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DescModel implements DescContract.Model {
    private String fid;
    private String dramaStr = "";
    private AnimeDescBean animeDescBean = new AnimeDescBean();

    @Override
    public void getData(String url, DescContract.LoadDataCallback callback) {
        new HttpGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Document doc = Jsoup.parse(response.body().string());
                    Elements detail = doc.getElementsByClass("detail");
                    //新版解析方案
                    if (detail.size() > 0) {
                        AnimeDescHeaderBean bean = new AnimeDescHeaderBean();
                        String animeName = detail.get(0).select("h1").text();
                        bean.setImg(detail.get(0).select("img").attr("src").contains("http") ? detail.get(0).select("img").attr("src") : Silisili.DOMAIN + detail.get(0).select("img").attr("src"));
                        bean.setName(animeName);
                        //创建index
                        DatabaseUtil.addAnime(animeName);
                        fid = DatabaseUtil.getAnimeID(animeName);
                        dramaStr = DatabaseUtil.queryAllIndex(fid);
                        Elements desc1 = detail.get(0).getElementsByClass("d_label");
                        Elements desc2 = detail.get(0).getElementsByClass("d_label2");
                        bean.setUrl(url);
                        for (Element desc : desc1) {
                            if (desc.text().contains("地区"))
                                bean.setRegion(desc.text());
                            else if (desc.text().contains("年代"))
                                bean.setYear(desc.text());
                            else if (desc.text().contains("标签"))
                                bean.setTag(desc.text());
                            else if (desc.text().contains("状态"))
                                bean.setState(desc.text());

                        }
                        for (Element desc : desc2) {
                            if (desc.text().contains("看点"))
                                bean.setShow(desc.text());
                            if (desc.text().contains("简介"))
                                bean.setDesc(desc.text());
                        }
                        callback.successDesc(bean);

                        Elements playDesc = doc.getElementsByClass("stitle").get(0).select("span >a");
                        Elements play = doc.getElementsByClass("time_pic");
                        if (play.size() > 0) {
                            //分集
                            Elements play_list = doc.getElementsByClass("time_pic").get(0).getElementsByClass("swiper-slide").select("ul.clear >li");
                            //下载
                            Elements down = doc.getElementsByClass("time_pic").get(0).getElementsByClass("xfswiper3").select("ul.clear >li >a");
                            //推荐
                            Elements recommend = doc.getElementsByClass("swiper3").select("ul > li");
                            for (int i = 0; i < playDesc.size(); i++) {
                                String str = playDesc.get(i).text();
                                if (str.equals("在线")) {
                                    setPlayData(play_list);
                                } else if (str.equals("下载")) {
                                    if (down.size() > 0) {
                                        List<DownBean> downList = new ArrayList<>();
                                        for (int j = 0; j < down.size(); j++) {
                                            if (!down.get(j).text().isEmpty()) {
                                                downList.add(
                                                        new DownBean(
                                                                down.get(j).text(),
                                                                down.get(j).attr("href")
                                                        )
                                                );
                                            }
                                        }
                                        callback.hasDown(downList);
                                    }
                                }
                            }
                            if (recommend.size() > 0) {
                                setRecommendData(recommend);
                            }
                            callback.isFavorite(DatabaseUtil.checkFavorite(animeName));
                            callback.successMain(animeDescBean);
                        } else {
                            callback.error(Utils.getString(R.string.no_playlist_error));
                        }
                    } else {
                        //解析失败
                        callback.error(Utils.getString(R.string.parsing_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.error(e.getMessage());
                }
            }
        });
    }

    /**
     * 番剧列表
     * @param els
     */
    public void setPlayData(Elements els) {
        List<AnimeDescDetailsBean> animeDescDetailsBeans = new ArrayList<>();
        int k = 0;
        boolean select;
        for (int i = 0; i < els.size(); i++) {
            String name = els.get(i).select("a>em>span").text();
            String watchUrl = els.get(i).select("a").attr("href");
            if (!watchUrl.isEmpty()) {
                k++;
                if (dramaStr.contains(watchUrl.replaceAll(Silisili.DOMAIN, "")))
                    select = true;
                else
                    select = false;
                animeDescDetailsBeans.add(new AnimeDescDetailsBean(name, els.get(i).select("a").attr("href"), select));
            }
        }
        if (k == 0)
            animeDescDetailsBeans.add(new AnimeDescDetailsBean(Utils.getString(R.string.no_resources), "", false));
        animeDescBean.setAnimeDescDetailsBeans(animeDescDetailsBeans);
    }

    public void setRecommendData(Elements els) {
        List<AnimeDescRecommendBean> animeDescRecommendBeans = new ArrayList<>();
        for (int i = 0; i < els.size(); i++) {
            String str = els.get(i).text();
            if (!str.equals(""))
                animeDescRecommendBeans.add(new AnimeDescRecommendBean(els.get(i).select("p").text(),
                        els.get(i).select("img").attr("src").contains("http") ? els.get(i).select("img").attr("src") : Silisili.DOMAIN + els.get(i).select("img").attr("src"),
                        Silisili.DOMAIN + els.get(i).select("a").attr("href"))
                );
        }
        animeDescBean.setAnimeDescRecommendBeans(animeDescRecommendBeans);
    }

}
