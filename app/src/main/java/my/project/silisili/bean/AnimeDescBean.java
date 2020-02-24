package my.project.silisili.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnimeDescBean implements Serializable {
    // 播放列表集合
    private List<AnimeDescDetailsBean> animeDescDetailsBeans = new ArrayList<>();
    // 番剧推荐集合
    private List<AnimeDescRecommendBean> animeDescRecommendBeans = new ArrayList<>();

    public List<AnimeDescDetailsBean> getAnimeDescDetailsBeans() {
        return animeDescDetailsBeans;
    }

    public void setAnimeDescDetailsBeans(List<AnimeDescDetailsBean> animeDescDetailsBeans) {
        this.animeDescDetailsBeans = animeDescDetailsBeans;
    }

    public List<AnimeDescRecommendBean> getAnimeDescRecommendBeans() {
        return animeDescRecommendBeans;
    }

    public void setAnimeDescRecommendBeans(List<AnimeDescRecommendBean> animeDescRecommendBeans) {
        this.animeDescRecommendBeans = animeDescRecommendBeans;
    }

}
