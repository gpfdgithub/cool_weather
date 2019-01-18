package com.example.coolweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.ServiceModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;
import util.ParseUtil;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CITYINFOURL = "http://guolin.tech/api/china";
    private static final String WEATHERINFO = "weatherInfo";
    private static final String AIRQUALITY = "airquailty";
    private SharedPreferences mPreferences;
    private String weatherInfo;
    private String mAirQualityInfo;

    private static final int COUNTRY_TYPE = 1;
    private static final int CITY_TYPE = 2;
    private static final int PROVINCE_TYPE = 3;

    private int mlevel = PROVINCE_TYPE;

    private ListView mListView;
    private String  mResponse;
    private  ArrayAdapter<String> mAdapter;
    private ImageView mProgress;

    private List<ProVince> mProVinceList = new ArrayList<>();
    private List<City> mCityList = new ArrayList<>();
    private List<Country> mCountryList = new ArrayList<>();
    private List<String> dataList = new ArrayList<>();

    private ProVince mProvinceSelected;
    private City mCitySelected;

    private ImageButton mBackButton;
    private TextView mTitle;
    private AnimationDrawable mProgressDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (mlevel) {
                    case PROVINCE_TYPE: {
                        mProvinceSelected = mProVinceList.get(position);
                        queryCity();
                        break;
                    }
                    case CITY_TYPE: {
                        mCitySelected = mCityList.get(position);
                        queryCountry();
                        break;
                    }
                    case COUNTRY_TYPE: {
                        Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
                        intent.putExtra("cityId",mCountryList.get(position).getWeatherId());
                        Log.d(TAG, "onItemClick: " + mCountryList.get(position).getWeatherId());
                        intent.putExtra("cityName",mCountryList.get(position).getCountryName());
                        startActivity(intent);
                        break;
                    }
                }
            }
        });
    }

    private void init() {
        LitePal.getDatabase();
        mProgress = findViewById(R.id.progress);
        mListView = findViewById(R.id.listview);
        mBackButton = findViewById(R.id.back_button);
        mBackButton.setOnClickListener(this);
        mTitle = findViewById(R.id.city_title);
        mAdapter =
                new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,dataList);
        mListView.setAdapter(mAdapter);
        mProgressDrawable = (AnimationDrawable)mProgress.getDrawable();
        mProgress.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressDrawable.start();
            }
        }, 100);
        String command = getIntent().getStringExtra("command");
        if (command != null && command.equals("changeCity")) {
            queryProvince();
        } else {
            isHaveCache();
        }

    }

    private void isHaveCache() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mPreferences != null) {
            weatherInfo = mPreferences.getString(WEATHERINFO, null);
            mAirQualityInfo = mPreferences.getString(AIRQUALITY, null);
            if (weatherInfo != null && mAirQualityInfo != null) {
                Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
                intent.putExtra("cityId",ParseUtil.parseResponseNow(weatherInfo).getBasic().getCid());
                startActivity(intent);
            } else {
                queryProvince();
            }
        }
    }


    private void queryProvince() {
        mProVinceList = LitePal.findAll(ProVince.class);
        mlevel = PROVINCE_TYPE;
        if (mProVinceList.size() <= 0) {
            //请求网络
            queryForServer();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBackButton.setVisibility(View.GONE);
                    mTitle.setText("中国");
                    dataList.clear();
                    for (int i = 0;i < mProVinceList.size();i++) {
                        dataList.add(mProVinceList.get(i).getProvinceName());
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void queryCity(){
        mCityList =  LitePal.where("provinceid = ?",mProvinceSelected.getProvinceCode()).find(City.class);
        mlevel = CITY_TYPE;
        if (mCityList.size() <= 0) {
            queryForServer();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBackButton.setVisibility(View.VISIBLE);
                    mTitle.setText(mProvinceSelected.getProvinceName());
                    dataList.clear();
                    for (int i = 0;i < mCityList.size();i++) {
                        dataList.add(mCityList.get(i).getCityName());
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void queryCountry(){
        mCountryList =  LitePal.where("cityid = ?",mCitySelected.getCityCode()).find(Country.class);
        mlevel = COUNTRY_TYPE;
        if (mCountryList.size() <= 0) {
            queryForServer();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBackButton.setVisibility(View.VISIBLE);
                    mTitle.setText(mCitySelected.getCityName());
                    dataList.clear();
                    for (int i = 0;i < mCountryList.size();i++) {
                        dataList.add(mCountryList.get(i).getCountryName());
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void queryForServer() {
        openProgress();
        switch (mlevel) {
            case PROVINCE_TYPE: {
                String url = CITYINFOURL;
                Log.d(TAG, "queryForServer: url" +url );
                HttpUtil.sendGetRequest(url,callback);
                break;
            }
            case CITY_TYPE: {
                String url = CITYINFOURL + "/" + mProvinceSelected.getProvinceCode();
                Log.d(TAG, "queryForServer: url" +url );
                HttpUtil.sendGetRequest(url,callback);
                break;
            }
            case COUNTRY_TYPE: {
                String url = CITYINFOURL + "/" + mProvinceSelected.getProvinceCode()
                        + "/" + mCitySelected.getCityCode();
                Log.d(TAG, "queryForServer: url" +url );
                HttpUtil.sendGetRequest(url,callback);
                break;
            }
        }
    }


    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            GoneProgress();
            Toast.makeText(MainActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            mResponse = response.body().string();
            Log.d(TAG, mResponse);
            List<ServiceModel> Models = JSONObject.parseArray(mResponse, ServiceModel.class);
            if (mlevel == PROVINCE_TYPE){
                for (int i=0;i < Models.size();i++) {
                    ProVince proVince = new ProVince(Models.get(i).getName(),
                            Models.get(i).getId()+"");
                    proVince.save();
                }
                queryProvince();
            } else if (mlevel == CITY_TYPE) {
                for (int i=0;i < Models.size();i++) {
                    City city = new City(Models.get(i).getName(),
                            Models.get(i).getId()+"");
                    city.setProvinceId(Integer.valueOf(mProvinceSelected.getProvinceCode()));
                    city.save();
                }
                queryCity();
            } else {
                for (int i=0;i < Models.size();i++) {
                    Country country = new Country(Models.get(i).getName(),
                            Models.get(i).getId()+"");
                    country.setWeatherId(Models.get(i).getWeather_id());
                    country.setCityId(Integer.valueOf(mCitySelected.getCityCode()));
                    country.save();
                }
                queryCountry();
            }
            GoneProgress();
        }
    };

    @Override
    public void onClick(View v) {
        if(mlevel == CITY_TYPE) {
            queryProvince();
        } else {
            queryCity();
        }
    }

    private void openProgress(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void GoneProgress(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.GONE);
            }
        });
    }
}
