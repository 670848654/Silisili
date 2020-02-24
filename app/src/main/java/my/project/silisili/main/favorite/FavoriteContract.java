package my.project.silisili.main.favorite;

import java.util.List;

import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.main.base.BaseLoadDataCallback;
import my.project.silisili.main.base.BaseView;

public interface FavoriteContract {
    interface Model{
        void getData(LoadDataCallback callback);
    }

    interface View extends BaseView {
        void showSuccessView(List<AnimeDescHeaderBean> list);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(List<AnimeDescHeaderBean> list);
    }

}
