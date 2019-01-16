package com.example.coolweather;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {

    private String cityName;
    private String cityCode;
    private int provinceId;

    public City(String cityName, String cityCode) {
        this.cityName = cityName;
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
}


