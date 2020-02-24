package my.project.silisili.main.animelist;

import java.util.List;

import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.main.base.BasePresenter;
import my.project.silisili.main.base.Presenter;

public class AnimeListPresenter extends Presenter<AnimeListContract.View> implements BasePresenter,AnimeListContract.LoadDataCallback {
    private String url;
    private int page;
    private AnimeListContract.View view;
    private AnimeListModel model;

    public AnimeListPresenter(String url, int page, AnimeListContract.View view){
        super(view);
        this.url = url;
        this.view = view;
        this.page = page;
        model = new AnimeListModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
            view.showLoadingView();
        view.showEmptyVIew();
        model.getData(url, page, isMain,this);
    }

    @Override
    public void success(boolean isMain, List<AnimeDescHeaderBean> list) {
        view.showSuccessView(isMain, list);
    }

    @Override
    public void error(boolean isMain, String msg) {
        view.showErrorView(isMain, msg);
    }

    @Override
    public void pageCount(int count) {
        view.getPageCountSuccessView(count);
    }

    @Override
    public void error(String msg) {
        view.showLoadErrorView(msg);
    }
}
