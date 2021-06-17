package com.zzlecheng.cashbook.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zzlecheng.cashbook.bean.CategoryBean;
import com.zzlecheng.cashbook.R;

import java.util.List;

/**
 * @类名: CategoryAdapter
 * @描述:
 * @作者: huangchao
 * @时间: 2018/9/18 上午11:33
 * @版本: 1.0.0
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private ButtonInterface buttonInterface;
    private List<CategoryBean> mList;//父层列表 （里面是 text + 子List（子list是image+text））

    public CategoryAdapter(Context context, List<CategoryBean> list) {
        this.context = context;
        this.mList = list;
    }

    /**
     * 按钮点击事件需要的方法
     */
    public void buttonSetOnclick(ButtonInterface buttonInterface) {
        this.buttonInterface = buttonInterface;
    }

    /**
     * 按钮点击事件对应的接口
     */
    public interface ButtonInterface {
        void onclick(View view, int position, String editLbid);

        void onDeleteClick(View view, int position);

        void onAddClick(View view, int position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent,
                false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CategoryBean bean = mList.get(position);
//        LogUtils.e(bean.toString());
        holder.mTitle.setText(bean.getLbmc());
        holder.tv_bj.setOnClickListener(v -> {
            //接口实例化后的而对象，调用重写后的方法
            buttonInterface.onclick(v, position, bean.getId());
        });
        holder.tv_delete.setOnClickListener(v -> {
            //接口实例化后的而对象，调用重写后的方法
            buttonInterface.onDeleteClick(v, position);
        });
        holder.tv_add.setOnClickListener(v -> {
            //接口实例化后的而对象，调用重写后的方法
            buttonInterface.onAddClick(v, position);
        });
        //把内层的RecyclerView 绑定在外层的onBindViewHolder
        // 先判断一下是不是已经设置了Adapter
        if (holder.mRecyclerView.getAdapter() == null) {
            holder.mRecyclerView.setAdapter(new VarietyAdapter(context,
                    mList.get(position).getMenuList()));
        } else {
            holder.mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * static ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;//标题
        TextView tv_bj;//编辑
        TextView tv_delete;//删除
        TextView tv_add;//添加
        RecyclerView mRecyclerView; // 父层的 RecyclerView

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.menu_title);
            tv_bj = itemView.findViewById(R.id.tv_bj);
            tv_delete = itemView.findViewById(R.id.tv_delete);
            tv_add = itemView.findViewById(R.id.tv_add);
            mRecyclerView = itemView.findViewById(R.id.variety_recyclerView);
            RecyclerView.LayoutManager manager = new GridLayoutManager(itemView.getContext(),
                    1);
            // 需要注意的是GridLayoutManager要设置setAutoMeasureEnabled(true)成自适应高度
            manager.setAutoMeasureEnabled(true);
            mRecyclerView.setLayoutManager(manager);
        }
    }
}
