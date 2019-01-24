package util;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import model.AirQuailty;
import model.Weather;

public class ParseUtil {

    private static final String TAG =  ParseUtil.class.getSimpleName();


    public static Weather.HeWeather6Bean parseResponseNow(String response) {
        if (response == null || response.equals("")) {
            Log.e(TAG, "parseResponse:服务器返回数据异常");
            return null;
        }
        Gson gson = new Gson();
        Weather weather =  gson.fromJson(response, Weather.class);
        //Weather weather = (Weather)JSONObject.parse(response);
        return weather.getHeWeather6().get(0);
    }

    public static AirQuailty.HeWeather6Bean parseAirQuality(String response) {
        if (response == null || response.equals("")) {
            Log.e(TAG, "parseResponse:服务器返回数据异常");
            return null;
        }
        Gson gson = new Gson();
        AirQuailty airQuailty =  gson.fromJson(response, AirQuailty.class);
        return airQuailty.getHeWeather6().get(0);
    }


}
