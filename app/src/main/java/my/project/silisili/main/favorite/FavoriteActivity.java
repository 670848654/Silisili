package my.project.silisili.main.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import my.project.silisili.R;
import my.project.silisili.adapter.FavoriteListAdapter;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.custom.CustomLoadMoreView;
import my.project.silisili.database.DatabaseUtil;
import my.project.silisili.main.base.BaseActivity;
import my.project.silisili.main.desc.DescActivity;
import my.project.silisili.util.SwipeBackLayoutUtil;
import my.project.silisili.util.Utils;
import my.project.silisili.util.VideoUtils;

public class FavoriteActivity extends BaseActivity<FavoriteContract.View, FavoritePresenter> implements FavoriteContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private FavoriteListAdapter adapter;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private List<AnimeDescHeaderBean> favoriteList = new ArrayList<>();
    @BindView(R.id.show)
    CoordinatorLayout show;
    private int limit = 100;
    private int videosCount = 0;
    private boolean isMain = true;
    protected boolean isErr = true;

    @Override
    protected FavoritePresenter createPresenter() {
        return new FavoritePresenter(favoriteList.size(), limit, this);
    }

    @Override
    protected void loadData() {
        videosCount = DatabaseUtil.queryFavoriteCount();
        mPresenter.loadData(isMain);
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_anime;
    }

    @Override
    protected void init() {
        Slidr.attach(this,Utils.defaultInit());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) show.getLayoutParams();
        params.setMargins(10, 0, 10, Utils.getNavigationBarHeight(this) - 5);
        initToolbar();
        initSwipe();
        initAdapter();
    }

    @Override
    protected void initBeforeView() {
        SwipeBackLayoutUtil.convertActivityToTranslucent(this);
    }

    public void initToolbar(){
        toolbar.setTitle(Utils.getString(R.string.favorite_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    public void initSwipe(){
        //不启用下拉刷新
        mSwipe.setEnabled(false);
    }

    public void initAdapter(){
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new FavoriteListAdapter(this, favoriteList);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
            AnimeDescHeaderBean bean = (AnimeDescHeaderBean) adapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putString("name", bean.getName());
            String url = VideoUtils.getSiliUrl(bean.getUrl());
            bundle.putString("url", url);
            startActivityForResult(new Intent(FavoriteActivity.this, DescActivity.class).putExtras(bundle),3000);
        });
        adapter.setOnItemLongClickListener((adapter, view, position) -> {
            View v = adapter.getViewByPosition(mRecyclerView, position, R.id.img);
            final PopupMenu popupMenu = new PopupMenu(FavoriteActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.favorite_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.remove_favorite:
                        removeFavorite(position);
                        break;
                }
                return true;
            });
            popupMenu.show();
            return true;
        });
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.setOnLoadMoreListener(() -> mRecyclerView.postDelayed(() -> {
            if (favoriteList.size() >= videosCount) {
                adapter.loadMoreEnd();
            } else {
                if (isErr) {
                    isMain = false;
                    mPresenter = createPresenter();
                    loadData();
                } else {
                    isErr = true;
                    adapter.loadMoreFail();
                }
            }
        }, 500), mRecyclerView);
        if (Utils.checkHasNavigationBar(this)) mRecyclerView.setPadding(0,0,0, Utils.getNavigationBarHeight(this));
        mRecyclerView.setAdapter(adapter);
    }

    public void setLoadState(boolean loadState) {
        isErr = loadState;
        adapter.loadMoreComplete();
    }

    /**
     * 移除收藏
     */
    private void removeFavorite(int position){
        DatabaseUtil.deleteFavorite(favoriteList.get(position).getName());
        adapter.remove(position);
        application.showSnackbarMsg(show, Utils.getString(R.string.join_error));
        if (favoriteList.size() <= 0){
            errorTitle.setText(Utils.getString(R.string.empty_favorite));
            adapter.setEmptyView(errorView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200 && requestCode == 3000) {
            isMain = true;
            favoriteList.clear();
            mPresenter = createPresenter();
            loadData();
        }
    }

    @Override
    public void showLoadingView() {
        adapter.setNewData(favoriteList);
    }

    @Override
    public void showLoadErrorView(String msg) {
        setLoadState(false);
        if (isMain) {
            errorTitle.setText(msg);
            adapter.setEmptyView(errorView);
        }
    }

    @Override
    public void showEmptyVIew() {
        adapter.setEmptyView(emptyView);
    }

    @Override
    public void showSuccessView(List<AnimeDescHeaderBean> list) {
        setLoadState(true);
        if (isMain) {
            favoriteList = list;
            adapter.setNewData(favoriteList);
        } else
            adapter.addData(list);
    }
}
