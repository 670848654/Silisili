package my.project.silisili.main.desc;

import java.util.List;

import my.project.silisili.bean.AnimeDescBean;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.bean.DownBean;
import my.project.silisili.main.base.BasePresenter;
import my.project.silisili.main.base.Presenter;

public class DescPresenter extends Presenter<DescContract.View> implements BasePresenter,DescContract.LoadDataCallback {
    private String url;
    private DescContract.View view;
    private DescModel model;

    public DescPresenter(String url,DescContract.View view){
        super(view);
        this.url = url;
        this.view = view;
        model = new DescModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
            view.showLoadingView();
        model.getData(url, this);
    }

    @Override
    public void successMain(AnimeDescBean bean) {
        view.showSuccessMainView(bean);
    }

    @Override
    public void successDesc(AnimeDescHeaderBean bean) {
        view.showSuccessDescView(bean);
    }

    @Override
    public void isFavorite(boolean favorite) {
        view.showSuccessFavorite(favorite);
    }

    @Override
    public void hasDown(List<DownBean> list) {
        view.showDownView(list);
    }

    @Override
    public void error(String msg) {
        view.showLoadErrorView(msg);
    }
}
