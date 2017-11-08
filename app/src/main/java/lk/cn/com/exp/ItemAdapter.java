package lk.cn.com.exp;

import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class ItemAdapter extends BaseQuickAdapter<ActivityItem, BaseViewHolder> {

        public ItemAdapter() {
            super(R.layout.item_main);
        }

        @Override
        protected void convert(BaseViewHolder helper, final ActivityItem item) {
            if (item != null) {
                helper.setText(R.id.btn, item.name);
                helper.getView(R.id.btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, item.cls);
                        mContext.startActivity(i);
                    }
                });
            }
        }
    }