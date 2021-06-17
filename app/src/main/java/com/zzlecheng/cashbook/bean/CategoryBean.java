package com.zzlecheng.cashbook.bean;

import java.util.List;

/**
 * @类名: CategoryBean
 * @描述: 类别相关
 * @作者: huangchao
 * @时间: 2018/9/18 上午11:30
 * @版本: 1.0.0
 */
public class CategoryBean {

    public String id;
    //类别明称
    public String lbmc;
    //排序
    public int px;
    //是否有效
    public String sfyx;

    public List<VarietyBean> menuList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLbmc() {
        return lbmc;
    }

    public void setLbmc(String lbmc) {
        this.lbmc = lbmc;
    }

    public int getPx() {
        return px;
    }

    public void setPx(int px) {
        this.px = px;
    }

    public String getSfyx() {
        return sfyx;
    }

    public void setSfyx(String sfyx) {
        this.sfyx = sfyx;
    }

    public List<VarietyBean> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<VarietyBean> menuList) {
        this.menuList = menuList;
    }

    @Override
    public String toString() {
        return "CategoryBean{" +
                "id='" + id + '\'' +
                ", lbmc='" + lbmc + '\'' +
                ", px='" + px + '\'' +
                ", sfyx='" + sfyx + '\'' +
                ", menuList=" + menuList +
                '}';
    }
}
