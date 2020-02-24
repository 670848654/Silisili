package my.project.silisili.main.favorite;

import java.util.List;

import my.project.silisili.R;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.database.DatabaseUtil;
import my.project.silisili.util.Utils;

public class FavoriteModel implements FavoriteContract.Model{

    @Override
    public void getData(FavoriteContract.LoadDataCallback callback) {
        List<AnimeDescHeaderBean> list = DatabaseUtil.queryAllFavorite();
        if (list.size() > 0)
            callback.success(list);
        else
            callback.error(Utils.getString(R.string.empty_favorite));
    }
}
