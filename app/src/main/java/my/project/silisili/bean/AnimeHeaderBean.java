package my.project.silisili.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

import my.project.silisili.config.AnimeType;

public class AnimeHeaderBean extends AbstractExpandableItem<AnimeDescBean> implements MultiItemEntity,Serializable {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AnimeHeaderBean(String title){
        this.title = title;
    }

    public AnimeHeaderBean() {}

    @Override
    public int getLevel() {
        return AnimeType.TYPE_LEVEL_0;
    }

    @Override
    public int getItemType() {
        return AnimeType.TYPE_LEVEL_0;
    }
}
