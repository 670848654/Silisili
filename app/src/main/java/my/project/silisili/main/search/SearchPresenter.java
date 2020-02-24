package my.project.silisili.main.search;

import java.util.List;

import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.main.base.BasePresenter;
import my.project.silisili.main.base.Presenter;

public class SearchPresenter extends Presenter<SearchContract.View> implements BasePresenter,SearchContract.LoadDataCallback {
    private String title;
    private String searchID;
    private int page;
    private SearchContract.View view;
    private SearchModel model;

    public SearchPresenter(String title, String searchID, int page, SearchContract.View view){
        super(view);
        this.title = title;
        this.searchID = searchID;
        this.page = page;
        this.view = view;
        model = new SearchModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
        {
            view.showLoadingView();
            view.showEmptyVIew();
        }
        model.getData(title, searchID, page, isMain, this);
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
    public void pageCount(int pageCount) {
        view.getPageCount(pageCount);
    }

    @Override
    public void searchID(String searchID) {
        view.getSearchID(searchID);
    }

    @Override
    public void error(String msg) {

    }
}
