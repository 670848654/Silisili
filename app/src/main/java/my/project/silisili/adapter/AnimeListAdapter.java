package my.project.silisili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import my.project.silisili.R;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.util.Utils;

/**
 * 番剧列表适配器
 */
public class AnimeListAdapter extends BaseQuickAdapter<AnimeDescHeaderBean, BaseViewHolder> {
    private Context context;

    public AnimeListAdapter(Context context, List list) {
        super(R.layout.item_favorite, list);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeDescHeaderBean item) {
        Utils.setCardDefaultBg(context, helper.getView(R.id.card_view), helper.getView(R.id.title));
        Utils.setDefaultImage(context, item.getImg(), helper.getView(R.id.img), true, helper.getView(R.id.card_view), helper.getView(R.id.title));
//        Utils.setCardBg(context, item.getImg(), helper.getView(R.id.card_view), helper.getView(R.id.title));
        helper.setText(R.id.title, item.getName());
    }
}
