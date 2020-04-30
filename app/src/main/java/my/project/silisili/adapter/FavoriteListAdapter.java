package my.project.silisili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import my.project.silisili.R;
import my.project.silisili.application.Silisili;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.util.Utils;

/**
 * 收藏列表适配器
 */
public class FavoriteListAdapter extends BaseQuickAdapter<AnimeDescHeaderBean, BaseViewHolder> {
    private Context context;

    public FavoriteListAdapter(Context context, List<AnimeDescHeaderBean> list) {
        super(R.layout.item_favorite, list);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeDescHeaderBean item) {
        String img = item.getImg();
        Utils.setCardDefaultBg(context, helper.getView(R.id.card_view), helper.getView(R.id.title));
        Utils.setDefaultImage(context, img.contains("http") ? img : Silisili.DOMAIN + img, helper.getView(R.id.img));
        Utils.setCardBg(context, img.contains("http") ? img : Silisili.DOMAIN + img, helper.getView(R.id.card_view), helper.getView(R.id.title));
        helper.setText(R.id.title, item.getName());

    }
}
