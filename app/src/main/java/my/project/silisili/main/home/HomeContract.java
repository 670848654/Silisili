package my.project.silisili.main.home;

import java.util.LinkedHashMap;

import my.project.silisili.main.base.BaseLoadDataCallback;
import my.project.silisili.main.base.BaseView;

public interface HomeContract {
    interface Model{
        void getData(LoadDataCallback callback);
    }

    interface View extends BaseView {
        void showLoadSuccess(LinkedHashMap map);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(LinkedHashMap map);
    }
}
