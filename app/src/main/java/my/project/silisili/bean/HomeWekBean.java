package my.project.silisili.bean;

public class HomeWekBean {
    private String title;
    private String img;
    private String url;
    private String drama;
    private boolean hasNew;

    public HomeWekBean(String title, String img, String url, String drama, boolean hasNew){
        this.title = title;
        this.img = img;
        this.url = url;
        this.drama = drama;
        this.hasNew = hasNew;
    }

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

    public String getDrama() {
        return drama;
    }

    public void setDrama(String drama) {
        this.drama = drama;
    }

    public boolean isHasNew() {
        return hasNew;
    }

    public void setHasNew(boolean hasNew) {
        this.hasNew = hasNew;
    }
}
