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
    //????????????
    private EditText edt_content;
    private EditText edt_px;
    private String lbmc = "";
    private int px = 0;
    //????????????
    private TextView tv_title;
    private EditText edt_content_cp;
    private EditText edt_dw_cp;
    private EditText edt_px_cp;
    private String cpmc;
    private String dw;
    private int px_cp;
    private String lbid;
    //?????????????????????????????????????????????
    private boolean isAdd;
    //?????????????????????ID
    private String editId;

    //????????????
    private List<CategoryBean> categoryBeanList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    //?????????
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

        //??????????????????
        PermissionUtils.verifyStoragePermissions(this);
        //???????????????????????????
        String time = SharedPreferenceUtils.getInstance().getString("time");
        if (time.equals("")) {
            tvRq.setText(IdUtils.getCurrentTime());
        } else {
            tvRq.setText(time);
        }
        sqLiteOpenHelper = new SQLiteDbHelper(this);
        //?????????????????????????????????
        db = sqLiteOpenHelper.getWritableDatabase();
        //??????????????????
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
                //?????????
//                db.delete(SQLiteDbHelper.TABLE_CATEGORY,
//                        "id = ?", new String[]{list.get(position).getId()});
                //?????????,?????????????????????0???????????????
                db.execSQL("update category set sfyx = ? where id = ? ",
                        new Object[]{"0", list.get(position).getId()});
                refresh();
                ToastUtils.showShortToast(MainActivity.this, "????????????");
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
     * ??????????????????
     */
    private void queryCategory() {
        // ????????? select * from students ??????
        Cursor cursor = db.query(SQLiteDbHelper.TABLE_CATEGORY, null,
                " sfyx = 1 ", null, null, null,
                " px asc");
        // ???????????????????????????
        while (cursor.moveToNext()) {
            CategoryBean categoryBean = new CategoryBean();
            categoryBean.setId(cursor.getString(cursor.getColumnIndex("id")));
            categoryBean.setLbmc(cursor.getString(cursor.getColumnIndex("lbmc")));
            categoryBean.setSfyx(cursor.getString(cursor.getColumnIndex("sfyx")));
            categoryBean.setPx(Integer.parseInt(cursor.getString(cursor.getColumnIndex("px"))));
            list.add(categoryBean);
        }
        // ????????????
        cursor.close();
    }

    /**
     * ???????????????????????????
     */
    private void initAdapter() {
        edtZj.setText("");
        edtZj.setHint("?????????");
        //????????????????????????
        Cursor cursorZj = db.query(SQLiteDbHelper.TABLE_TOTALPRICE, null,
                " rq = ? ", new String[]{tvRq.getText().toString()},
                null, null, null);
        while (cursorZj.moveToNext()) {
            edtZj.setText(cursorZj.getString(cursorZj.getColumnIndex("zj")));
        }

        //??????recycle?????????
        for (int i = 0; i < list.size(); i++) {
            CategoryBean categoryBean = new CategoryBean();
            categoryBean.setId(list.get(i).getId());
            categoryBean.setLbmc(list.get(i).getLbmc());
            categoryBean.setPx(list.get(i).getPx());
            categoryBean.setSfyx(list.get(i).getSfyx());

            //???????????????????????????????????????????????????????????????
            Cursor cursor = db.rawQuery("select v.*,d.sl,d.zj from variety v left join " +
                            "details d on v.id = d.cpid and d.rq = ? " +
                            " where v.lbid = ?" +
                            " order by v.px asc",
                    new String[]{tvRq.getText().toString(), list.get(i).getId()});

//            Cursor cursor = db.query(SQLiteDbHelper.TABLE_VARIETY, null,
//                    " lbid = ? ", new String[]{list.get(i).getId()}, null, null,
//                    " px asc");
            List<VarietyBean> listVariety = new ArrayList<>();
            // ???????????????????????????
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
            // ????????????
            cursor.close();
            categoryBean.setMenuList(listVariety);
            categoryBeanList.add(categoryBean);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tj://????????????
                isAdd = true;
                CustomPopupWindow popupWindow = new CustomPopupWindow(this);
                popupWindow.showAtLocation(findViewById(R.id.tv_rq),
                        Gravity.CENTER, 0, 0);
                break;
            case R.id.tv_rq://???????????????
                initCustomTimePicker();
                pvCustomTime.show(v);
                break;
            case R.id.btn_save://???????????????????????????????????????
                saveDetail();
                break;

        }
    }

    /**
     * ??????????????????
     */
    class CustomPopupWindow extends PopupWindow implements View.OnClickListener {

        Context mContext;
        private LayoutInflater mInflater;
        private View mContentView;

        public CustomPopupWindow(Context context) {
            super(context);
            this.mContext = context;
            //?????????
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //??????
            mContentView = mInflater.inflate(R.layout.layout_popup_bb, null);
            mContentView.findViewById(R.id.tv_cancel).setOnClickListener(this);
            mContentView.findViewById(R.id.tv_sure).setOnClickListener(this);
            edt_content = mContentView.findViewById(R.id.edt_content);
            edt_px = mContentView.findViewById(R.id.edt_px);
            //??????hint????????????
//            edt_content.setHintTextColor(getResources().getColor(R.color.white));
//            edt_px.setHintTextColor(getResources().getColor(R.color.white));
            //???????????????title
            tv_title = mContentView.findViewById(R.id.tv_title);
            if (isAdd) {
                tv_title.setText("????????????");
            } else {
                tv_title.setText("????????????");
            }
            //??????View
            setContentView(mContentView);
            //???????????????
            setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            //??????????????????
            setAnimationStyle(R.style.PopupWindowStyle);
            //?????????????????????????????????????????????????????????BACK??????
            setBackgroundDrawable(new ColorDrawable());
            //????????????????????????
            setFocusable(true);
            //??????????????????????????????
            setOutsideTouchable(true);
            //??????????????????
            setTouchable(true);
            //??????????????????????????????
            setTouchInterceptor((v, event) -> {
                //??????????????????????????????
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                backgroundAlpha(1.0f);
                //??????????????????
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
     * ??????????????????
     */
    class CustomPopupWindowCp extends PopupWindow implements View.OnClickListener {

        Context mContext;
        private LayoutInflater mInflater;
        private View mContentView;

        public CustomPopupWindowCp(Context context) {
            super(context);
            this.mContext = context;
            //?????????
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //??????
            mContentView = mInflater.inflate(R.layout.layout_popup_cp, null);
            mContentView.findViewById(R.id.tv_cancel_cp).setOnClickListener(this);
            mContentView.findViewById(R.id.tv_sure_cp).setOnClickListener(this);
            edt_content_cp = mContentView.findViewById(R.id.edt_content_cp);
            edt_dw_cp = mContentView.findViewById(R.id.edt_dw_cp);
            edt_px_cp = mContentView.findViewById(R.id.edt_px_cp);
//            edt_content_cp.setHintTextColor(getResources().getColor(R.color.white));
//            edt_dw_cp.setHintTextColor(getResources().getColor(R.color.white));
//            edt_px_cp.setHintTextColor(getResources().getColor(R.color.white));
            //??????View
            setContentView(mContentView);
            //???????????????
            setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            //??????????????????
            setAnimationStyle(R.style.PopupWindowStyle);
            //?????????????????????????????????????????????????????????BACK??????
            setBackgroundDrawable(new ColorDrawable());
            //????????????????????????
            setFocusable(true);
            //??????????????????????????????
            setOutsideTouchable(true);
            //??????????????????
            setTouchable(true);
            //??????????????????????????????
            setTouchInterceptor((v, event) -> {
                //??????????????????????????????
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                backgroundAlpha(1.0f);
                //??????????????????
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
     * ???????????????
     *
     * @param f
     */
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }


    /**
     * ????????????????????????????????????
     *
     * @param i i=0??????????????????i=1??????????????????
     */
    private void addRealm(int i) {
        if (i == 0) {//????????????
            ContentValues values = categoryToContentValues(mockCategory(IdUtils.getId()));
            db.insert(SQLiteDbHelper.TABLE_CATEGORY, null, values);
            refresh();
        } else {//????????????
            ContentValues values = varietyToContentValues(mockVariety(IdUtils.getId()));
            db.insert(SQLiteDbHelper.TABLE_VARIETY, null, values);
            refresh();
        }

    }

    /**
     * ?????? category ??????
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
     * ??? category ????????????????????? ContentValues ???
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
     * ?????? variety ??????
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
     * ??? variety ????????????????????? ContentValues ???
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
     * ????????????
     */
    private void refresh() {
        list.clear();
        categoryBeanList.clear();
        iniView();
        initAdapter();
    }

    /**
     * ???????????????????????????
     */
    private void initCustomTimePicker() {

        /**
         * @description
         *
         * ???????????????
         * 1.?????????????????????id??? optionspicker ?????? timepicker ???????????????????????????????????????????????????????????????.
         * ???????????????demo ????????????????????????layout?????????
         * 2.????????????Calendar???????????????0-11???,?????????????????????Calendar???set?????????????????????,???????????????????????????0-11
         * setRangDate??????????????????????????????(?????????????????????????????????????????????1900-2100???????????????????????????)
         */
        Calendar selectedDate = Calendar.getInstance();//??????????????????
        Calendar startDate = Calendar.getInstance();
        startDate.set(2014, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2027, 2, 28);
        //??????????????? ??????????????????
        pvCustomTime = new TimePickerBuilder(this, (date, v) -> {//??????????????????
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
                .setDividerColor(Color.WHITE)//????????????????????????
                .setTextColorCenter(Color.LTGRAY)//????????????????????????
                .setLineSpacingMultiplier(1.6f)//????????????????????????????????????
                .setTitleBgColor(Color.DKGRAY)//?????????????????? Night mode
                .setBgColor(Color.BLACK)//?????????????????? Night mode
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
                .setLabel("???", "???", "???", "???", "???", "???")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 40, 0, -40)
                .isCenterLabel(false) //?????????????????????????????????label?????????false?????????item???????????????label???
                .setDividerColor(0xFF24AD9D)
                .build();

    }

    /**
     * ???????????????????????????????????????
     *
     * @param date
     * @return
     */
    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * ?????????????????????????????????????????????
     */
    private void saveDetail() {
        //??????????????????
        SharedPreferenceUtils.getInstance().putString("time", tvRq.getText().toString());
        //??????????????????????????????
        db.delete(SQLiteDbHelper.TABLE_DETAILS,
                "rq = ?", new String[]{
                        SharedPreferenceUtils.getInstance().getString("time")});
        //?????????????????????????????????
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
     * ??????
     */
    private void refreshActivity() {
        finish();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * ?????? variety ??????
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
     * ??? variety ????????????????????? ContentValues ???
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
     * ??????????????????????????????
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
