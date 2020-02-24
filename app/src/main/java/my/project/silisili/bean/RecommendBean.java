package my.project.silisili.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import my.project.silisili.config.RecommendType;

public class RecommendBean implements MultiItemEntity {
    private String title;
    private String img;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RecommendBean(String title, String img, String url){
        this.title = title;
        this.img = img;
        this.url = url;
    }

    @Override
    public int getItemType() {
        return RecommendType.TYPE_LEVEL_1;
    }
}
