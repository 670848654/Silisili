package my.project.silisili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.util.Utils;
import my.project.silisili.R;

/**
 * 番剧列表适配器
 */
public class AnimeListAdapter extends BaseQuickAdapter<AnimeDescHeaderBean, BaseViewHolder> {
    private Context context;

    public AnimeListAdapter(Context context, List list) {
        super(R.layout.item_anime, list);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeDescHeaderBean item) {
        Utils.setDefaultImage(context, item.getImg(), helper.getView(R.id.img));
        helper.setText(R.id.title, item.getName());
        helper.setText(R.id.region, item.getRegion());
        helper.setText(R.id.year, item.getYear());
        helper.setText(R.id.tag, item.getTag());
        helper.setText(R.id.desc, item.getDesc().isEmpty() || item.getDesc().equals("简介：") ? Utils.getString(R.string.no_show_msg) : item.getDesc());
        helper.setText(R.id.show, item.getShow().isEmpty() || item.getShow().equals(Utils.getString(R.string.eye_msg)) ? Utils.getString(R.string.no_eye_msg) : item.getShow());
        helper.setText(R.id.state, item.getState());
    }
}
