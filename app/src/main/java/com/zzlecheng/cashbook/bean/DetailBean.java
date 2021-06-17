package com.zzlecheng.cashbook.bean;

/**
 * @类名: DetailBean
 * @描述: 菜品入库详情
 * @作者: huangchao
 * @时间: 2018/9/20 上午11:20
 * @版本: 1.0.0
 */
public class DetailBean {
    //入库详情ID
    public String id;
    //入库日期
    public String rq;
    //菜品ID
    public String cpid;
    //数量
    public String sl;
    //单价
    public String dj;
    //总价
    public String zj;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRq() {
        return rq;
    }

    public void setRq(String rq) {
        this.rq = rq;
    }

    public String getCpid() {
        return cpid;
    }

    public void setCpid(String cpid) {
        this.cpid = cpid;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getDj() {
        return dj;
    }

    public void setDj(String dj) {
        this.dj = dj;
    }

    public String getZj() {
        return zj;
    }

    public void setZj(String zj) {
        this.zj = zj;
    }

    @Override
    public String toString() {
        return "DetailBean{" +
                "id='" + id + '\'' +
                ", rq='" + rq + '\'' +
                ", cpid='" + cpid + '\'' +
                ", sl=" + sl +
                ", dj=" + dj +
                ", zj=" + zj +
                '}';
    }
}
