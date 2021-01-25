package my.project.silisili.main.tag;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import my.project.silisili.R;
import my.project.silisili.application.Silisili;
import my.project.silisili.bean.TagBean;
import my.project.silisili.bean.TagHeaderBean;
import my.project.silisili.net.HttpGet;
import my.project.silisili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TagModel implements TagContract.Model {
    private List<MultiItemEntity> list = new ArrayList<>();
    private final static Pattern ANIME_PATTERN = Pattern.compile("\\/anime\\/[0-9]{4}\\/");

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
                        Elements years = taglist.get(0).select("a");
                        setXFData(years);
                        // 地区列表
                        Elements regions = taglist.get(1).select("dl.tag-list");
                        for (int i = 0; i < regions.size(); i++) {
                            setData("动漫地区", regions.get(i).select("a"));
                        }
                        // 类型列表
                        Elements models = taglist.get(2).select("dl.tag-list");
                        for (int i = 0; i < models.size(); i++) {
                            setData("动漫类型", models.get(i).select("a"));
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

    private void setXFData(Elements els) {
        TagHeaderBean tagHeaderBean = new TagHeaderBean("动漫年代");
        for (int i=0,size=els.size(); i<size; i++) {
            Element a = els.get(i);
            Matcher m = ANIME_PATTERN.matcher(a.attr("href"));
            while (m.find()) {
                tagHeaderBean.addSubItem(
                        new TagBean(els.get(i).text().replaceAll("\\D", ""),
                                Silisili.DOMAIN + els.get(i).attr("href"),
                                tagHeaderBean.getTitle() + " - ")
                );
                break;
            }
        }
        tagHeaderBean.addSubItem(
                new TagBean("09-00",
                        Silisili.DOMAIN + "/anime/2010xq/",
                        tagHeaderBean.getTitle() + " - ")
        );
        tagHeaderBean.addSubItem(
                new TagBean("00以前",
                        Silisili.DOMAIN + "/anime/2000xq/",
                        tagHeaderBean.getTitle() + " - ")
        );
        list.add(tagHeaderBean);
    }

    private void setData(String title, Elements els) {
        TagHeaderBean tagHeaderBean = new TagHeaderBean(title);
        for (int j = 0; j < els.size(); j++) {
            tagHeaderBean.addSubItem(
                    new TagBean(els.get(j).text(),
                    Silisili.DOMAIN + els.get(j).attr("href"),
                    title + " - ")
            );
        }
        list.add(tagHeaderBean);
    }
}
