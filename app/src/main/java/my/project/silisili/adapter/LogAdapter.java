package my.project.silisili.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import my.project.silisili.bean.LogBean;
import my.project.silisili.R;

public class LogAdapter extends BaseQuickAdapter<LogBean, BaseViewHolder> {
    public LogAdapter(List list) {
        super(R.layout.item_log, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, LogBean item) {
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.dateTime, item.getDateTime());
        helper.setText(R.id.desc, item.getDesc());
    }
}
