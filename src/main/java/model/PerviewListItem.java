package model;

import org.litepal.crud.LitePalSupport;

public class PerviewListItem extends LitePalSupport {

    private String day;
    private String info;
    private String maxTmp;
    private String minTmp;


    public PerviewListItem(String day, String info, String maxTmp, String minTmp) {
        this.day = day;
        this.info = info;
        this.maxTmp = maxTmp;
        this.minTmp = minTmp;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMaxTmp() {
        return maxTmp;
    }

    public void setMaxTmp(String maxTmp) {
        this.maxTmp = maxTmp;
    }

    public String getMinTmp() {
        return minTmp;
    }

    public void setMinTmp(String minTmp) {
        this.minTmp = minTmp;
    }
}
