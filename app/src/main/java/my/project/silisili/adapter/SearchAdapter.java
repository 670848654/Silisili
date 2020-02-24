package my.project.silisili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import my.project.silisili.bean.SearchBean;
import my.project.silisili.util.Utils;
import my.project.silisili.R;

public class SearchAdapter extends BaseQuickAdapter<SearchBean, BaseViewHolder> {
    private Context context;
    public SearchAdapter(Context context, List<SearchBean> list) {
        super(R.layout.item_favorite, list);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchBean item) {
        Utils.setCardDefaultBg(context, helper.getView(R.id.card_view), helper.getView(R.id.title));
        Utils.setDefaultImage(context, item.getImg(), helper.getView(R.id.img));
        Utils.setCardBg(context, item.getImg(), helper.getView(R.id.card_view), helper.getView(R.id.title));
        helper.setText(R.id.title, item.getTitle());
    }
}
