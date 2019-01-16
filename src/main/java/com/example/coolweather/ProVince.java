package com.example.coolweather;

import org.litepal.crud.LitePalSupport;

public class ProVince extends LitePalSupport {

    private String provinceName;
    private String provinceCode;

    public ProVince(String provinceName, String provinceCode) {
        this.provinceName = provinceName;
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
