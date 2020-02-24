package my.project.silisili.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import my.project.silisili.adapter.TagAdapter;

public class TagBean implements MultiItemEntity {
    private String title;
    private String url;
    private String desc;


    public TagBean(String title, String url, String desc) {
        this.title = title;
        this.url = url;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int getItemType() {
        return TagAdapter.TYPE_LEVEL_1;
    }
}
