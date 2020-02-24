package my.project.silisili.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import my.project.silisili.config.RecommendType;

public class RecommendHeaderBean extends AbstractExpandableItem<RecommendBean> implements MultiItemEntity {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RecommendHeaderBean(String title){
        this.title = title;
    }

    @Override
    public int getLevel() {
        return RecommendType.TYPE_LEVEL_0;
    }

    @Override
    public int getItemType() {
        return RecommendType.TYPE_LEVEL_0;
    }
}
