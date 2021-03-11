package my.project.silisili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import my.project.silisili.R;
import my.project.silisili.application.Silisili;
import my.project.silisili.bean.HomeWekBean;
import my.project.silisili.util.Utils;

public class FragmentAdapter extends BaseQuickAdapter<HomeWekBean,BaseViewHolder> {
    private Context context;

    public FragmentAdapter(Context context, List<HomeWekBean> data) {
        super(R.layout.item_week, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeWekBean item) {
        Utils.setDefaultImage(context, item.getImg().startsWith("http") ? item.getImg() : Silisili.DOMAIN + item.getImg(),helper.getView(R.id.img), true, helper.getView(R.id.card_view), helper.getView(R.id.title));
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.drama, item.getDrama());
//        Utils.setCardBg(context, item.getImg().startsWith("http") ? item.getImg() : Silisili.DOMAIN + item.getImg(), helper.getView(R.id.card_view), helper.getView(R.id.title));
        if (item.isHasNew()) helper.setVisible(R.id.new_view, true);
        else helper.setVisible(R.id.new_view, false);
    }
}
