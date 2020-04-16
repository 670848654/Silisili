package my.project.silisili.main.tag;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import my.project.silisili.R;
import my.project.silisili.application.Silisili;
import my.project.silisili.bean.AnimeTagBean;
import my.project.silisili.bean.TagBean;
import my.project.silisili.bean.TagHeaderBean;
import my.project.silisili.net.HttpGet;
import my.project.silisili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TagModel implements TagContract.Model {
    private List<MultiItemEntity> list = new ArrayList<>();

    @Override
    public void getData(TagContract.LoadDataCallback callback) {
        new HttpGet(Silisili.TAG, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Document doc = Jsoup.parse(response.body().string());
                    Elements taglist = doc.select("div.tagbox.m-10");
                    if (taglist.size() > 0) {
                        // 新番列表
                        Elements years = taglist.get(0).select("dl.tag-list");
                        for (int i = 0; i < years.size(); i++) {
                            setXFData(years.get(i).select("a"));
                        }
                        TagHeaderBean homeHeaderBean = new TagHeaderBean("2000年-2009年的动漫");
                        homeHeaderBean.addSubItem(new TagBean("2000-2009",
                                Silisili.DOMAIN + "/anime/2010xq/",
                                ""));
                        list.add(homeHeaderBean);
                        homeHeaderBean = new TagHeaderBean("2000年以前的动漫");
                        homeHeaderBean.addSubItem(new TagBean("2000以前",
                                Silisili.DOMAIN + "/anime/2000xq/",
                                ""));
                        list.add(homeHeaderBean);
                        // 地区列表
                        Elements regions = taglist.get(1).select("dl.tag-list");
                        for (int i = 0; i < regions.size(); i++) {
                            setData("地区", regions.get(i).select("a"));
                        }
                        // 类型列表
                        Elements models = taglist.get(2).select("dl.tag-list");
                        for (int i = 0; i < models.size(); i++) {
                            setData("类型", models.get(i).select("a"));
                        }
                        callback.success(list);
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

    private List<AnimeTagBean> getAnimeTagBeans(Elements els) {
        List<AnimeTagBean> animeTagBeans = new ArrayList<>();
        for (int j = 0; j < els.size(); j++) {
            if (els.get(j).text().contains("新番") || els.get(j).text().contains("动漫")) {
                if (els.get(j).attr("href").contains("xq")) continue;
                animeTagBeans.add(new AnimeTagBean(els.get(j).text().replaceAll("：", ""), els.get(j).attr("href").replaceAll("/","").replaceAll("anime", "")));
            }
        }
        return animeTagBeans;
    }

    private void setXFData(Elements els) {
        List<AnimeTagBean> animeTagBeans = getAnimeTagBeans(els);
        for (AnimeTagBean animeTagBean : animeTagBeans) {
            TagHeaderBean tagHeaderBean = new TagHeaderBean(animeTagBean.getName());
            for (int j = 0; j < els.size(); j++) {
                if (els.get(j).attr("href").contains(animeTagBean.getYear()) && !els.get(j).text().contains(animeTagBean.getName())) {
                    if (els.get(j).attr("href").contains("xq")) continue;
                    TagBean tagBean = new TagBean(els.get(j).text(),
                            Silisili.DOMAIN + els.get(j).attr("href"),
                            animeTagBean.getName() + " - ");
                    tagHeaderBean.addSubItem(tagBean);
                }
            }
            list.add(tagHeaderBean);
        }
    }

    private void setData(String title, Elements els) {
        TagHeaderBean tagHeaderBean = new TagHeaderBean(title);
        for (int j = 0; j < els.size(); j++) {
            TagBean tagBean = new TagBean(els.get(j).text(),
                    Silisili.DOMAIN + els.get(j).attr("href"),
                    title + " - ");
            tagHeaderBean.addSubItem(tagBean);
        }
        list.add(tagHeaderBean);
    }
}
