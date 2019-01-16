package com.example.coolweather;
import org.litepal.crud.LitePalSupport;

public class Country extends LitePalSupport {

    private String countryName;
    private String countryCode;
    private int cityId;
    private int weatherId;

    public Country(String countryName, String countryCode) {
        this.countryName = countryName;
        this.countryCode = countryCode;
    }


    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }
}
