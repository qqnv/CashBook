package com.zzlecheng.cashbook.bean;

/**
 * @类名: VarietyBean
 * @描述: 菜品相关
 * @作者: huangchao
 * @时间: 2018/9/18 上午11:31
 * @版本: 1.0.0
 */
public class VarietyBean {
    //菜品ID
    public String id;
    //类别ID
    public String lbid;
    //菜品名称
    public String cpmc;
    //单位
    public String dw;
    //排序
    public int px;
    //是否有效
    public String sfyx;
    //增加数据后从详情表查询出的数据
    //数量
    public String sl;
    //总价
    public String zj;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLbid() {
        return lbid;
    }

    public void setLbid(String lbid) {
        this.lbid = lbid;
    }

    public String getCpmc() {
        return cpmc;
    }

    public void setCpmc(String cpmc) {
        this.cpmc = cpmc;
    }

    public String getDw() {
        return dw;
    }

    public void setDw(String dw) {
        this.dw = dw;
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

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getZj() {
        return zj;
    }

    public void setZj(String zj) {
        this.zj = zj;
    }

    @Override
    public String toString() {
        return "VarietyBean{" +
                "id='" + id + '\'' +
                ", lbid='" + lbid + '\'' +
                ", cpmc='" + cpmc + '\'' +
                ", dw='" + dw + '\'' +
                ", px='" + px + '\'' +
                ", sfyx='" + sfyx + '\'' +
                ", sl='" + sl + '\'' +
                ", zj='" + zj + '\'' +
                '}';
    }
}
