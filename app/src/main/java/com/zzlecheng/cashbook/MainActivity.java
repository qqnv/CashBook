package com.zzlecheng.cashbook;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.zzlecheng.cashbook.adapter.CategoryAdapter;
import com.zzlecheng.cashbook.bean.CategoryBean;
import com.zzlecheng.cashbook.bean.TotalPriceBean;
import com.zzlecheng.cashbook.bean.VarietyBean;
import com.zzlecheng.cashbook.util.IdUtils;
import com.zzlecheng.cashbook.util.LogUtils;
import com.zzlecheng.cashbook.util.PermissionUtils;
import com.zzlecheng.cashbook.util.RecyclerViewDivider;
import com.zzlecheng.cashbook.util.SQLiteDbHelper;
import com.zzlecheng.cashbook.util.SharedPreferenceUtils;
import com.zzlecheng.cashbook.util.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_tj)
    TextView tvTj;
    @BindView(R.id.tv_rq)
    TextView tvRq;
    @BindView(R.id.edt_zj)
    EditText edtZj;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_save)
    Button btnSave;
    private Unbinder unbinder;
    //类别弹框
    private EditText edt_content;
    private EditText edt_px;
    private String lbmc = "";
    private int px = 0;
    //菜品弹框
    private TextView tv_title;
    private EditText edt_content_cp;
    private EditText edt_dw_cp;
    private EditText edt_px_cp;
    private String cpmc;
    private String dw;
    private int px_cp;
    private String lbid;
    //是添加菜品弹框还是编辑类别弹框
    private boolean isAdd;
    //需要修改的类别ID
    private String editId;

    //类别数据
    private List<CategoryBean> categoryBeanList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    //数据库
    private SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase db;

    private List<CategoryBean> list = new ArrayList<>();

    private TimePickerView pvCustomTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        iniView();
        initAdapter();
    }

    private void iniView() {

        //检测读写权限
        PermissionUtils.verifyStoragePermissions(this);
        //设置默认日期为今天
        String time = SharedPreferenceUtils.getInstance().getString("time");
        if (time.equals("")) {
            tvRq.setText(IdUtils.getCurrentTime());
        } else {
            tvRq.setText(time);
        }
        sqLiteOpenHelper = new SQLiteDbHelper(this);
        //创建一个可写入的数据库
        db = sqLiteOpenHelper.getWritableDatabase();
        //查询所有类别
        queryCategory();
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new RecyclerViewDivider(this,
                LinearLayoutManager.HORIZONTAL, 1, ContextCompat.getColor(this,
                R.color.main_back)));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(this, categoryBeanList);
        recyclerView.setAdapter(categoryAdapter);

        categoryAdapter.buttonSetOnclick(new CategoryAdapter.ButtonInterface() {
            @Override
            public void onclick(View view, int position, String editLbid) {
                isAdd = false;
                CustomPopupWindow popupWindow = new CustomPopupWindow(MainActivity.this);
                popupWindow.showAtLocation(findViewById(R.id.tv_rq),
                        Gravity.CENTER, 0, 0);
                editId = editLbid;
            }

            @Override
            public void onDeleteClick(View view, int position) {
                //真删除
//                db.delete(SQLiteDbHelper.TABLE_CATEGORY,
//                        "id = ?", new String[]{list.get(position).getId()});
                //假删除,将是否有效改为0，即为无效
                db.execSQL("update category set sfyx = ? where id = ? ",
                        new Object[]{"0", list.get(position).getId()});
                refresh();
                ToastUtils.showShortToast(MainActivity.this, "删除成功");
            }

            @Override
            public void onAddClick(View view, int position) {
                lbid = list.get(position).getId();
                CustomPopupWindowCp popupWindowCp = new CustomPopupWindowCp(MainActivity.this);
                popupWindowCp.showAtLocation(findViewById(R.id.tv_rq),
                        Gravity.CENTER, 0, 0);
                LogUtils.e(lbid);
            }
        });

        tvTj.setOnClickListener(this);
        tvRq.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    /**
     * 查询所有类别
     */
    private void queryCategory() {
        // 相当于 select * from students 语句
        Cursor cursor = db.query(SQLiteDbHelper.TABLE_CATEGORY, null,
                " sfyx = 1 ", null, null, null,
                " px asc");
        // 不断移动光标获取值
        while (cursor.moveToNext()) {
            CategoryBean categoryBean = new CategoryBean();
            categoryBean.setId(cursor.getString(cursor.getColumnIndex("id")));
            categoryBean.setLbmc(cursor.getString(cursor.getColumnIndex("lbmc")));
            categoryBean.setSfyx(cursor.getString(cursor.getColumnIndex("sfyx")));
            categoryBean.setPx(Integer.parseInt(cursor.getString(cursor.getColumnIndex("px"))));
            list.add(categoryBean);
        }
        // 关闭光标
        cursor.close();
    }

    /**
     * 初始化是适配器数据
     */
    private void initAdapter() {
        edtZj.setText("");
        edtZj.setHint("请输入");
        //查询每日入库总价
        Cursor cursorZj = db.query(SQLiteDbHelper.TABLE_TOTALPRICE, null,
                " rq = ? ", new String[]{tvRq.getText().toString()},
                null, null, null);
        while (cursorZj.moveToNext()) {
            edtZj.setText(cursorZj.getString(cursorZj.getColumnIndex("zj")));
        }

        //查询recycle的数据
        for (int i = 0; i < list.size(); i++) {
            CategoryBean categoryBean = new CategoryBean();
            categoryBean.setId(list.get(i).getId());
            categoryBean.setLbmc(list.get(i).getLbmc());
            categoryBean.setPx(list.get(i).getPx());
            categoryBean.setSfyx(list.get(i).getSfyx());

            //根据当前类别，数据库查询属于当前类别的菜品
            Cursor cursor = db.rawQuery("select v.*,d.sl,d.zj from variety v left join " +
                            "details d on v.id = d.cpid and d.rq = ? " +
                            " where v.lbid = ?" +
                            " order by v.px asc",
                    new String[]{tvRq.getText().toString(), list.get(i).getId()});

//            Cursor cursor = db.query(SQLiteDbHelper.TABLE_VARIETY, null,
//                    " lbid = ? ", new String[]{list.get(i).getId()}, null, null,
//                    " px asc");
            List<VarietyBean> listVariety = new ArrayList<>();
            // 不断移动光标获取值
            while (cursor.moveToNext()) {
                VarietyBean varietyBean1 = new VarietyBean();
                varietyBean1.setId(cursor.getString(cursor.getColumnIndex("id")));
                varietyBean1.setLbid(cursor.getString(cursor.getColumnIndex("lbid")));
                varietyBean1.setSl(cursor.getString(cursor.getColumnIndex("sl")));
                varietyBean1.setZj(cursor.getString(cursor.getColumnIndex("zj")));
                varietyBean1.setCpmc(cursor.getString(cursor.getColumnIndex("cpmc")));
                varietyBean1.setDw(cursor.getString(cursor.getColumnIndex("dw")));
                varietyBean1.setSfyx(cursor.getString(cursor.getColumnIndex("sfyx")));
                varietyBean1.setPx(Integer.parseInt(cursor.getString(cursor.getColumnIndex("px"))));
                listVariety.add(varietyBean1);
            }
            // 关闭光标
            cursor.close();
            categoryBean.setMenuList(listVariety);
            categoryBeanList.add(categoryBean);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tj://添加类别
                isAdd = true;
                CustomPopupWindow popupWindow = new CustomPopupWindow(this);
                popupWindow.showAtLocation(findViewById(R.id.tv_rq),
                        Gravity.CENTER, 0, 0);
                break;
            case R.id.tv_rq://时间选择器
                initCustomTimePicker();
                pvCustomTime.show(v);
                break;
            case R.id.btn_save://保存菜品详情和每日入库总价
                saveDetail();
                break;

        }
    }

    /**
     * 添加类别弹框
     */
    class CustomPopupWindow extends PopupWindow implements View.OnClickListener {

        Context mContext;
        private LayoutInflater mInflater;
        private View mContentView;

        public CustomPopupWindow(Context context) {
            super(context);
            this.mContext = context;
            //打气筒
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //打气
            mContentView = mInflater.inflate(R.layout.layout_popup_bb, null);
            mContentView.findViewById(R.id.tv_cancel).setOnClickListener(this);
            mContentView.findViewById(R.id.tv_sure).setOnClickListener(this);
            edt_content = mContentView.findViewById(R.id.edt_content);
            edt_px = mContentView.findViewById(R.id.edt_px);
            //设置hint字体颜色
//            edt_content.setHintTextColor(getResources().getColor(R.color.white));
//            edt_px.setHintTextColor(getResources().getColor(R.color.white));
            //设置弹框的title
            tv_title = mContentView.findViewById(R.id.tv_title);
            if (isAdd) {
                tv_title.setText("添加类别");
            } else {
                tv_title.setText("编辑类别");
            }
            //设置View
            setContentView(mContentView);
            //设置宽与高
            setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            //设置进出动画
            setAnimationStyle(R.style.PopupWindowStyle);
            //设置背景只有设置了这个才可以点击外边和BACK消失
            setBackgroundDrawable(new ColorDrawable());
            //设置可以获取集点
            setFocusable(true);
            //设置点击外边可以消失
            setOutsideTouchable(true);
            //设置可以触摸
            setTouchable(true);
            //设置点击外部可以消失
            setTouchInterceptor((v, event) -> {
                //判断是不是点击了外部
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                backgroundAlpha(1.0f);
                //不是点击外部
                return false;
            });
            backgroundAlpha(0.4f);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_cancel:
                    edt_content.setText("");
                    backgroundAlpha(1.0f);
                    dismiss();
                    break;
                case R.id.tv_sure:
                    lbmc = edt_content.getText().toString();
                    px = Integer.parseInt(edt_px.getText().toString());
                    if (isAdd) {
                        addRealm(0);
                    } else {
                        editorRealm();
                    }
                    dismiss();
                    break;
            }
        }
    }

    /**
     * 添加菜品弹框
     */
    class CustomPopupWindowCp extends PopupWindow implements View.OnClickListener {

        Context mContext;
        private LayoutInflater mInflater;
        private View mContentView;

        public CustomPopupWindowCp(Context context) {
            super(context);
            this.mContext = context;
            //打气筒
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //打气
            mContentView = mInflater.inflate(R.layout.layout_popup_cp, null);
            mContentView.findViewById(R.id.tv_cancel_cp).setOnClickListener(this);
            mContentView.findViewById(R.id.tv_sure_cp).setOnClickListener(this);
            edt_content_cp = mContentView.findViewById(R.id.edt_content_cp);
            edt_dw_cp = mContentView.findViewById(R.id.edt_dw_cp);
            edt_px_cp = mContentView.findViewById(R.id.edt_px_cp);
//            edt_content_cp.setHintTextColor(getResources().getColor(R.color.white));
//            edt_dw_cp.setHintTextColor(getResources().getColor(R.color.white));
//            edt_px_cp.setHintTextColor(getResources().getColor(R.color.white));
            //设置View
            setContentView(mContentView);
            //设置宽与高
            setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            //设置进出动画
            setAnimationStyle(R.style.PopupWindowStyle);
            //设置背景只有设置了这个才可以点击外边和BACK消失
            setBackgroundDrawable(new ColorDrawable());
            //设置可以获取集点
            setFocusable(true);
            //设置点击外边可以消失
            setOutsideTouchable(true);
            //设置可以触摸
            setTouchable(true);
            //设置点击外部可以消失
            setTouchInterceptor((v, event) -> {
                //判断是不是点击了外部
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                backgroundAlpha(1.0f);
                //不是点击外部
                return false;
            });
            backgroundAlpha(0.4f);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_cancel_cp:
                    backgroundAlpha(1.0f);
                    dismiss();
                    break;
                case R.id.tv_sure_cp:
                    cpmc = edt_content_cp.getText().toString();
                    dw = edt_dw_cp.getText().toString();
                    px_cp = Integer.parseInt(edt_px_cp.getText().toString());
                    addRealm(1);
                    dismiss();
                    break;
            }
        }
    }

    /**
     * 设置遮罩层
     *
     * @param f
     */
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }


    /**
     * 执行向数据库存储数据操作
     *
     * @param i i=0是类别插入，i=1则是菜品插入
     */
    private void addRealm(int i) {
        if (i == 0) {//类别插入
            ContentValues values = categoryToContentValues(mockCategory(IdUtils.getId()));
            db.insert(SQLiteDbHelper.TABLE_CATEGORY, null, values);
            refresh();
        } else {//菜品插入
            ContentValues values = varietyToContentValues(mockVariety(IdUtils.getId()));
            db.insert(SQLiteDbHelper.TABLE_VARIETY, null, values);
            refresh();
        }

    }

    /**
     * 构建 category 对象
     *
     * @param id
     * @return
     */
    private CategoryBean mockCategory(String id) {
        CategoryBean categoryBean = new CategoryBean();
        categoryBean.id = id;
        categoryBean.lbmc = lbmc;
        categoryBean.px = px;
        categoryBean.sfyx = "1";
        return categoryBean;
    }

    /**
     * 将 category 对象的值存储到 ContentValues 中
     *
     * @param categoryBean
     * @return
     */
    private ContentValues categoryToContentValues(CategoryBean categoryBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", categoryBean.id);
        contentValues.put("lbmc", categoryBean.lbmc);
        contentValues.put("px", categoryBean.px);
        contentValues.put("sfyx", categoryBean.sfyx);
        return contentValues;
    }

    /**
     * 构建 variety 对象
     *
     * @param id
     * @return
     */
    private VarietyBean mockVariety(String id) {
        VarietyBean varietyBean = new VarietyBean();
        varietyBean.id = id;
        varietyBean.lbid = lbid;
        varietyBean.cpmc = cpmc;
        varietyBean.dw = dw;
        varietyBean.px = px_cp;
        varietyBean.sfyx = "1";
        return varietyBean;
    }

    /**
     * 将 variety 对象的值存储到 ContentValues 中
     *
     * @param varietyBean
     * @return
     */
    private ContentValues varietyToContentValues(VarietyBean varietyBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", varietyBean.id);
        contentValues.put("lbid", varietyBean.lbid);
        contentValues.put("cpmc", varietyBean.cpmc);
        contentValues.put("dw", varietyBean.dw);
        contentValues.put("px", varietyBean.px);
        contentValues.put("sfyx", varietyBean.sfyx);
        return contentValues;
    }

    /**
     * 刷新数据
     */
    private void refresh() {
        list.clear();
        categoryBeanList.clear();
        iniView();
        initAdapter();
    }

    /**
     * 点击弹出时间选择框
     */
    private void initCustomTimePicker() {

        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(2014, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2027, 2, 28);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerBuilder(this, (date, v) -> {//选中事件回调
            tvRq.setText(getTime(date));
            SharedPreferenceUtils.getInstance().putString("time", getTime(date));
            refresh();
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentTextSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
                .setDividerColor(Color.WHITE)//设置分割线的颜色
                .setTextColorCenter(Color.LTGRAY)//设置选中项的颜色
                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.BLACK)//滚轮背景颜色 Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)
                .animGravity(Gravity.RIGHT)// default is center*/
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.layout_pickerview_custom, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = v.findViewById(R.id.tv_finish);
                        TextView tvCancel = v.findViewById(R.id.tv_cancel);
                        tvSubmit.setOnClickListener(v1 -> {
                            pvCustomTime.returnData();
                            pvCustomTime.dismiss();
                        });
                        tvCancel.setOnClickListener(v12 -> pvCustomTime.dismiss());
                    }
                })
                .setContentTextSize(18)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 40, 0, -40)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .build();

    }

    /**
     * 可根据需要自行截取数据显示
     *
     * @param date
     * @return
     */
    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 保存单个菜品详情和每日入库总价
     */
    private void saveDetail() {
        //保存当前日期
        SharedPreferenceUtils.getInstance().putString("time", tvRq.getText().toString());
        //删除当前时间入库详情
        db.delete(SQLiteDbHelper.TABLE_DETAILS,
                "rq = ?", new String[]{
                        SharedPreferenceUtils.getInstance().getString("time")});
        //删除所选日期的入库总价
        db.delete(SQLiteDbHelper.TABLE_TOTALPRICE,
                "rq = ?", new String[]{
                        SharedPreferenceUtils.getInstance().getString("time")});
        ContentValues values = priceToContentValues(mockPrice(IdUtils.getId(),
                IdUtils.getCurrentTime(), edtZj.getText().toString()));
        db.insert(SQLiteDbHelper.TABLE_TOTALPRICE, null, values);

        categoryAdapter.notifyDataSetChanged();
        refreshActivity();
    }

    /**
     * 刷新
     */
    private void refreshActivity() {
        finish();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * 构建 variety 对象
     *
     * @param id
     * @return
     */
    private TotalPriceBean mockPrice(String id, String lrsj, String zj) {
        TotalPriceBean totalPriceBean = new TotalPriceBean();
        totalPriceBean.id = id;
        totalPriceBean.rq = SharedPreferenceUtils.getInstance().getString("time");
        totalPriceBean.lrsj = lrsj;
        totalPriceBean.zj = zj;
        return totalPriceBean;
    }

    /**
     * 将 variety 对象的值存储到 ContentValues 中
     *
     * @param totalPriceBean
     * @return
     */
    private ContentValues priceToContentValues(TotalPriceBean totalPriceBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", totalPriceBean.id);
        contentValues.put("rq", totalPriceBean.rq);
        contentValues.put("lrsj", totalPriceBean.lrsj);
        contentValues.put("zj", totalPriceBean.zj);
        return contentValues;
    }

    /**
     * 编辑类别，修改数据库
     */
    private void editorRealm() {
        db.execSQL("update category set lbmc=?,px=? where id=?",
                new Object[]{edt_content.getText().toString(), edt_px.getText().toString(), editId});
        refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
