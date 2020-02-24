package my.project.silisili.main.desc;

import java.util.List;

import my.project.silisili.bean.AnimeDescBean;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.bean.DownBean;
import my.project.silisili.main.base.BaseLoadDataCallback;
import my.project.silisili.main.base.BaseView;

public interface DescContract {
    interface Model {
        void getData(String url, LoadDataCallback callback);
    }

    interface View extends BaseView {
        void showSuccessMainView(AnimeDescBean bean);
        void showSuccessDescView(AnimeDescHeaderBean bean);
        void showSuccessFavorite(boolean favorite);
        void showDownView(List<DownBean> list);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void successMain(AnimeDescBean bean);
        void successDesc(AnimeDescHeaderBean bean);
        void isFavorite(boolean favorite);
        void hasDown(List<DownBean> list);
    }
}
