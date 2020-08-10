package my.project.silisili.bean;

import java.util.List;

public class AnimeDescHeaderBean {
    // 名称
    private String name;
    // 图片
    private String img;
    // 地址
    private String url;
    // 详情
    private String region = "";
    private String year = "";
    private String tag = "";
    private String state = "";
    private String show = "";

    private String desc = "";
    private List<String> tagTitles;
    private List<String> tagUrls;

    public List<String> getTagTitles() {
        return tagTitles;
    }

    public void setTagTitles(List<String> tagTitles) {
        this.tagTitles = tagTitles;
    }

    public List<String> getTagUrls() {
        return tagUrls;
    }

    public void setTagUrls(List<String> tagUrls) {
        this.tagUrls = tagUrls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
