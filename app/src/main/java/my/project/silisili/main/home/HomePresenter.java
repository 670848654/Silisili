package my.project.silisili.main.home;

import java.util.LinkedHashMap;

import my.project.silisili.main.base.BasePresenter;
import my.project.silisili.main.base.Presenter;

public class HomePresenter extends Presenter<HomeContract.View> implements BasePresenter,HomeContract.LoadDataCallback {
    private HomeContract.View view;
    private HomeModel model;

    public HomePresenter(HomeContract.View view){
        super(view);
        this.view = view;
        model = new HomeModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
            view.showLoadingView();
        model.getData(this);
    }

    @Override
    public void success(LinkedHashMap map) {
        view.showLoadSuccess(map);
    }

    @Override
    public void error(String msg) {
        view.showLoadErrorView(msg);
    }
}

