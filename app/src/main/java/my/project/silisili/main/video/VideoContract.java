package my.project.silisili.main.video;

import my.project.silisili.main.base.BaseLoadDataCallback;

public interface VideoContract {
    interface Model{
        void getData(String title, String url, LoadDataCallback callback);
    }

    interface View {
        void cancelDialog();
        void getVideoSuccess(String url);
        void getIframeUrl(String iframeUrl);
        void getVideoEmpty();
        void getVideoError();
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(String url);
        void sendIframeUrl(String iframeUrl);
        void error();
        void empty();
    }
}
