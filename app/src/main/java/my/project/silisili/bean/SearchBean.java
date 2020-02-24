package my.project.silisili.bean;

public class SearchBean {
    private String title;
    private String url;
    private String img;
    private String tags;
    private String desc;
    private String state;

    public SearchBean(){

    }

    public SearchBean(String title,String url,String img,String desc){
        this.title = title;
        this.url = url;
        this.img = img;
        this.desc = desc;
    }

    public SearchBean(String title, String url, String desc){
        this.title = title;
        this.url = url;
        this.desc = desc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
}
