package com.example.coolweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.LifeStyleAdapter;
import model.AirQuailty;
import model.Lifestyleitem;
import model.PerviewListItem;
import model.StringAdapter;
import model.Weather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;
import util.ParseUtil;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Context mContext;

    private  String WEATHERURL = "https://free-api.heweather.net/s6/weather?location=";
    private String AIRQUALITYURL = "https://free-api.heweather.net/s6/air/now?location=";
    private static final String APIKEY = "&key=501876c9524c487db26608cc3c9f6fa9";
    private static final String WEATHERINFO = "weatherInfo";
    private static final String AIRQUALITY = "airquailty";

    private HashMap<String,String> lifeTypeMap = new HashMap<String,String>(){
        {
            put("comf","舒适度指数");
            put("drsg","穿衣指数");
            put("flu","感冒指数");
            put("ptfc","交通指数");
            put("sport","运动指数");
            put("uv","紫外线指数");
            put("spi","防晒指数");
        }
    };

    private SharedPreferences mPreferences;
    private String weatherInfo;
    private String mAirQualityInfo;

    private TextView mTitleName;
    private TextView mTemperatureText;
    private TextView mCondText;
    private ListView mPreviewDayListView;
    private List<PerviewListItem> mPerViewList = new ArrayList<>();
    private TextView mAqiTextView;
    private TextView mAirQuiltyTextView;
    private RecyclerView mRecyclerView;



    private Weather.HeWeather6Bean mWeather;
    private Weather.HeWeather6Bean.NowBean mNow;
    private List<Weather.HeWeather6Bean.DailyForecastBean> dailyForecastBeanList;
    private List<Weather.HeWeather6Bean.LifestyleBean> mLifestyleList;
    private List<Lifestyleitem> mLifeStyleItems = new ArrayList<>();
    private AirQuailty.HeWeather6Bean mAirQuailty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        init();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        weatherInfo = mPreferences.getString(WEATHERINFO, null);
        mAirQualityInfo = mPreferences.getString(AIRQUALITY, null);
        if (weatherInfo != null && mAirQualityInfo != null) {
            showTheData();
            showAirQuiltyData();
        } else {
            queryServer();
        }
    }

    private void queryServer() {
        HttpUtil.sendGetRequest(WEATHERURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                weatherInfo = response.body().string();
                Log.d(TAG, "onResponse: " + weatherInfo);
                mPreferences.edit().putString(WEATHERINFO,weatherInfo);
                showTheData();
            }
        });

        HttpUtil.sendGetRequest(AIRQUALITYURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mAirQualityInfo = response.body().string();
                Log.d(TAG, "mAirQualityInfo: " + mAirQualityInfo);
                mPreferences.edit().putString(AIRQUALITY,mAirQualityInfo);
                showAirQuiltyData();
            }
        });

    }

    private void showAirQuiltyData() {
        mAirQuailty =  ParseUtil.parseAirQuality(mAirQualityInfo);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mAirQuailty.getStatus() == "ok") {
                    mAqiTextView.setText(mAirQuailty.getAir_now_city().getAqi());
                    mAirQuiltyTextView.setText(mAirQuailty.getAir_now_city().getQlty());
                } else {
                    mAqiTextView.setText("该城市暂无数据");
                    mAirQuiltyTextView.setText("该城市暂无数据");
                }
            }
        });
    }

    private void showTheData() {
        mWeather = ParseUtil.parseResponseNow(weatherInfo);
        mNow = mWeather.getNow();
        dailyForecastBeanList = mWeather.getDaily_forecast();
        mLifestyleList = mWeather.getLifestyle();
        for (Weather.HeWeather6Bean.LifestyleBean bean : mLifestyleList) {
            if(lifeTypeMap.get(bean.getType()) != null ) {
                Lifestyleitem item = new Lifestyleitem(bean.getTxt(),
                        lifeTypeMap.get(bean.getType()),
                        bean.getBrf());
                mLifeStyleItems.add(item);
                Log.d(TAG, "showTheData: " + item.toString());
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Weather.HeWeather6Bean.DailyForecastBean bean:dailyForecastBeanList) {
                    mPerViewList.add(
                            new PerviewListItem(bean.getDate(),
                                    bean.getCond_txt_d(),
                                    bean.getTmp_max(),
                                    bean.getTmp_min()));
                }
                StringAdapter adapter = new StringAdapter(mContext,mPerViewList);
                mPreviewDayListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                LifeStyleAdapter styleAdapter = new LifeStyleAdapter(mLifeStyleItems,mContext);
                mRecyclerView.setAdapter(styleAdapter);
                styleAdapter.notifyDataSetChanged();
                mTemperatureText.setText(mNow.getFl()+"℃");
                mCondText.setText(mNow.getCond_txt());
            }
        });
    }

    private void init() {
        mContext = this;
        mTitleName = findViewById(R.id.title_city_name);
        mTemperatureText = findViewById(R.id.temperature);
        mCondText =findViewById(R.id.cond_text);
        mAqiTextView = findViewById(R.id.aqi);
        mAirQuiltyTextView = findViewById(R.id.air_quailty);
        mRecyclerView = findViewById(R.id.liftstyle_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        String cityId = intent.getStringExtra("cityId");
        String cityName = intent.getStringExtra("cityName");
        mTitleName.setText(cityName);
        WEATHERURL = WEATHERURL + cityId + APIKEY;
        AIRQUALITYURL = AIRQUALITYURL + cityId + APIKEY;
        mPreviewDayListView = findViewById(R.id.preview_listview);
    }
}
