package my.project.silisili.main.video;

import my.project.silisili.main.base.BasePresenter;
import my.project.silisili.main.base.Presenter;

public class VideoPresenter extends Presenter<VideoContract.View> implements BasePresenter,VideoContract.LoadDataCallback {
    private VideoContract.View view;
    private VideoModel playModel;
    private String title;
    private String url;

    public VideoPresenter(String title, String url, VideoContract.View view){
        super(view);
        this.title = title;
        this.url = url;
        this.view = view;
        playModel = new VideoModel();
    }

    @Override
    public void loadData(boolean isMain) {
        playModel.getData(title, url, this);
    }

    @Override
    public void success(String url) {
        view.getVideoSuccess(url);
    }

    @Override
    public void sendIframeUrl(String iframeUrl) {
        view.getIframeUrl(iframeUrl);
    }

    @Override
    public void error() {
        view.cancelDialog();
        view.getVideoError();
    }

    @Override
    public void empty() {
//        view.cancelDialog();
        view.getVideoEmpty();
    }

    @Override
    public void error(String msg) {

    }
}
