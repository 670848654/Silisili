package my.project.silisili.bean;

public class LogBean {
    private String title;
    private String dateTime;
    private String desc;

    public LogBean(){}

    public LogBean(String title, String dateTime, String desc){
        this.title = title;
        this.dateTime = dateTime;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
