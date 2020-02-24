package my.project.silisili.main.search;

import java.util.List;

import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.main.base.BaseLoadDataCallback;
import my.project.silisili.main.base.BaseView;

public interface SearchContract {
    interface Model{
        void getData(String title, String searchUrl, int page, boolean isMain, LoadDataCallback callback);
    }

    interface View extends BaseView {
        void showSuccessView(boolean isMain, List<AnimeDescHeaderBean> list);
        void showErrorView(boolean isMain, String msg);
        void getPageCount(int pageCount);
        void getSearchID(String searchID);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(boolean isMain, List<AnimeDescHeaderBean> list);
        void error(boolean isMain, String msg);
        void pageCount(int pageCount);
        void searchID(String searchID);
    }
}
