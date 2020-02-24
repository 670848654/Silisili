package my.project.silisili.main.animelist;

import java.util.List;

import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.main.base.BaseLoadDataCallback;
import my.project.silisili.main.base.BaseView;

public interface AnimeListContract {
    interface Model{
        void getData( String url, int page, boolean isMain, LoadDataCallback callback);
    }

    interface View extends BaseView {
        void showSuccessView(boolean isMain, List<AnimeDescHeaderBean> list);

        void showErrorView(boolean isMain, String msg);

        void getPageCountSuccessView(int count);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(boolean isMain, List<AnimeDescHeaderBean> list);

        void error(boolean isMain, String msg);

        void pageCount(int count);
    }
}
