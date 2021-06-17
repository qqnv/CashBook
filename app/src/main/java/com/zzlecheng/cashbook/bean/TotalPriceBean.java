package com.zzlecheng.cashbook.bean;

/**
 * @类名: TotalPriceBean
 * @描述: 每日入库总价
 * @作者: huangchao
 * @时间: 2018/9/20 上午11:24
 * @版本: 1.0.0
 */
public class TotalPriceBean {
    //每日入库总价ID
    public String id;
    //日期
    public String rq;
    //录入时间
    public String lrsj;
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

    public String getLrsj() {
        return lrsj;
    }

    public void setLrsj(String lrsj) {
        this.lrsj = lrsj;
    }

    public String getZj() {
        return zj;
    }

    public void setZj(String zj) {
        this.zj = zj;
    }

    @Override
    public String toString() {
        return "TotalPriceBean{" +
                "id='" + id + '\'' +
                ", rq='" + rq + '\'' +
                ", lrsj='" + lrsj + '\'' +
                ", zj='" + zj + '\'' +
                '}';
    }
}
