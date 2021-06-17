package com.zzlecheng.cashbook.bean;

/**
 * @类名: DetailTemporaryBean
 * @描述:
 * @作者: huangchao
 * @时间: 2018/9/20 上午11:44
 * @版本: 1.0.0
 */
public class DetailTemporaryBean {
    public String cpid;
    public String sl;
    public String zj;

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

    public String getZj() {
        return zj;
    }

    public void setZj(String zj) {
        this.zj = zj;
    }

    @Override
    public String toString() {
        return "DetailTemporaryBean{" +
                "cpid='" + cpid + '\'' +
                ", sl='" + sl + '\'' +
                ", zj='" + zj + '\'' +
                '}';
    }
}
