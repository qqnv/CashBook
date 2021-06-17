package com.zzlecheng.cashbook.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.zzlecheng.cashbook.bean.DetailBean;
import com.zzlecheng.cashbook.bean.VarietyBean;
import com.zzlecheng.cashbook.R;
import com.zzlecheng.cashbook.util.IdUtils;
import com.zzlecheng.cashbook.util.SQLiteDbHelper;
import com.zzlecheng.cashbook.util.SharedPreferenceUtils;

import java.util.List;

/**
 * @类名: VarietyAdapter
 * @描述:
 * @作者: huangchao
 * @时间: 2018/9/18 上午11:33
 * @版本: 1.0.0
 */
public class VarietyAdapter extends RecyclerView.Adapter<VarietyAdapter.ViewHolder> {

    private Context context;
    private List<VarietyBean> mList; // List 集合（里面是image+text）
    private SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase db;

    /**
     * 构造函数
     *
     * @param context
     * @param list
     */
    public VarietyAdapter(Context context, List<VarietyBean> list) {
        this.context = context;
        this.mList = list;
        sqLiteOpenHelper = new SQLiteDbHelper(context);
        db = sqLiteOpenHelper.getWritableDatabase();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_variety, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VarietyBean bean = mList.get(position);
        if (!(holder.edtDwCP.getText().toString().equals("") && holder.edtPrice.getText().toString().equals(""))) {
            ContentValues values = detailToContentValues(mockDetail(bean.getId(),
                    holder.edtDwCP.getText().toString(),
                    holder.edtPrice.getText().toString()));
            db.insert(SQLiteDbHelper.TABLE_DETAILS, null, values);
        }
        holder.tvCp.setText(bean.getCpmc());
        holder.tvDwCP.setText(bean.getDw());
        holder.edtDwCP.setText(bean.getSl());
        holder.edtPrice.setText(bean.getZj());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * static ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCp;
        EditText edtDwCP;
        TextView tvDwCP;
        EditText edtPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCp = itemView.findViewById(R.id.tv_cp);
            edtDwCP = itemView.findViewById(R.id.edt_dw_cp);
            tvDwCP = itemView.findViewById(R.id.tv_dw_cp);
            edtPrice = itemView.findViewById(R.id.edt_price);
        }
    }

    /**
     * 构建 details 对象
     *
     * @param
     * @return
     */
    private DetailBean mockDetail(String cpid, String sl, String zj) {
        String id = IdUtils.getItemID(10);
//        int sls = 0;
//        if ((!sl.equals(""))&&IdUtils.isNumeric(sl)){
//            sls = Integer.parseInt(sl);
//        }
//        int zjs = 0;
//        if ((!zj.equals(""))&&IdUtils.isNumeric(zj)){
//            zjs = Integer.parseInt(zj);
//        }
        DetailBean detailBean = new DetailBean();
        detailBean.id = id;
        detailBean.rq = SharedPreferenceUtils.getInstance().getString("time");
        detailBean.cpid = cpid;
        detailBean.sl = sl;
        detailBean.dj = "0";
        detailBean.zj = zj;
        return detailBean;
    }

    /**
     * 将 details 对象的值存储到 ContentValues 中
     *
     * @param detailBean
     * @return
     */
    private ContentValues detailToContentValues(DetailBean detailBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", detailBean.id);
        contentValues.put("rq", detailBean.rq);
        contentValues.put("cpid", detailBean.cpid);
        contentValues.put("sl", detailBean.sl);
        contentValues.put("dj", detailBean.dj);
        contentValues.put("zj", detailBean.zj);
        return contentValues;
    }


}

