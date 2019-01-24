package com.example.coolweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

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
import util.ActivityCollrector;
import util.HttpUtil;
import util.ParseUtil;
import view.NestListView;

public class WeatherActivity extends AppCompatActivity implements  View.OnClickListener{

    private static final String TAG = "MainActivity";
    private Context mContext;
    private View mView;
    private DrawerLayout mDrawLayout;
    private String mCityId;
    private String mCityName;
    private String WEATHERURL = "https://free-api.heweather.net/s6/weather?location=";
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
    private SharedPreferences.Editor mEditor;
    private String weatherInfo;
    private String mAirQualityInfo;
    private TextView mTitleName;
    private TextView mTemperatureText;
    private TextView mCondText;
    private NestListView mPreviewDayListView;
    private List<PerviewListItem> mPerViewList = new ArrayList<>();
    private TextView mAqiTextView;
    private TextView mAirQuiltyTextView;
    private RecyclerView mRecyclerView;
    private ImageView mProgress;
    private Weather.HeWeather6Bean mWeather;
    private Weather.HeWeather6Bean.NowBean mNow;
    private List<Weather.HeWeather6Bean.DailyForecastBean> dailyForecastBeanList;
    private List<Weather.HeWeather6Bean.LifestyleBean> mLifestyleList;
    private List<Lifestyleitem> mLifeStyleItems = new ArrayList<>();
    private AirQuailty.HeWeather6Bean mAirQuailty;
    private ImageButton mMenuButton;
    private NavigationView mNavView;
    private ImageView mHeadProtraitImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        ActivityCollrector.addActivity(this);
        mView = LayoutInflater.from(this).inflate(R.layout.activity_weather,null);
        init();
        mEditor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        weatherInfo = mPreferences.getString(WEATHERINFO, null);
        mAirQualityInfo = mPreferences.getString(AIRQUALITY, null);
        if ( weatherInfo == null ||  mAirQualityInfo == null) {
            queryServer();
        } else {
            String cityName = ParseUtil.parseAirQuality(weatherInfo).getBasic().getLocation();
            if (cityName != null && cityName.equals(mCityName)) {
                showTheData();
                showAirQuiltyData();
            } else {
                queryServer();
            }
        }
    }

    private void queryServer() {
        openProgress();
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
                mEditor.putString(WEATHERINFO,weatherInfo).apply();
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
                mEditor.putString(AIRQUALITY,mAirQualityInfo).apply();
                showAirQuiltyData();
                GoneProgress();
            }
        });

    }

    private void showAirQuiltyData() {
        mAirQuailty =  ParseUtil.parseAirQuality(mAirQualityInfo);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mAirQuailty.getStatus().equals("ok")) {
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
        mLifeStyleItems.clear();
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
                mPerViewList.clear();
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
                mTitleName.setText(mWeather.getBasic().getLocation());
            }
        });
    }

    private void init() {
        mContext = this;
        initImmerse();
        mNavView = findViewById(R.id.nav_view);
        mDrawLayout = findViewById(R.id.drawer_layout);
        mMenuButton = findViewById(R.id.menu_button);
        Log.d(TAG, "init: "+ mMenuButton.getBackground());
        mMenuButton.setOnClickListener(this);
        mTitleName = findViewById(R.id.title_city_name);
        mTemperatureText = findViewById(R.id.temperature);
        mCondText =findViewById(R.id.cond_text);
        mAqiTextView = findViewById(R.id.aqi);
        mAirQuiltyTextView = findViewById(R.id.air_quailty);
        mRecyclerView = findViewById(R.id.liftstyle_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        mCityId = intent.getStringExtra("cityId");
        mCityName = intent.getStringExtra("cityName");
        WEATHERURL = WEATHERURL + mCityId + APIKEY;
        AIRQUALITYURL = AIRQUALITYURL + mCityId + APIKEY;
        mPreviewDayListView = findViewById(R.id.preview_listview);
        View headerView = mNavView.getHeaderView(0);
        mHeadProtraitImageView = headerView.findViewById(R.id.headicon_iamgeview);
        mHeadProtraitImageView.setOnClickListener(this);
        mNavView.setItemIconTintList(null);
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.change_city_item :{
                        Intent intent = new Intent(WeatherActivity.this,MainActivity.class);
                        intent.putExtra("command","changeCity");
                        startActivity(intent);
                        break;
                    }
                    case R.id.update :{
                        queryServer();
                        break;
                    }
                }
                mDrawLayout.closeDrawer(GravityCompat.START);
                menuItem.setCheckable(false);
                return true;
            }
        });
        mProgress = findViewById(R.id.progress);
        final AnimationDrawable ad = (AnimationDrawable)mProgress.getDrawable();
        mProgress.postDelayed(new Runnable() {
            @Override
            public void run() {
                ad.start();
            }
        }, 10);
    }

    private void initImmerse() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_button: {
                Log.d(TAG, "onClick: "+"menuButton");
                openMenu();
                break;
            }
            case R.id.headicon_iamgeview :{
                Log.d(TAG, "onClick: "+ "换换头像");
                openTheAlbum();
                break;
            }
            default: break;
        }
    }

    private void openTheAlbum() {
       Toast.makeText(this,"这个没弄呢",Toast.LENGTH_SHORT).show();
    }


    private void openMenu() {
        mDrawLayout.openDrawer(GravityCompat.START);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollrector.deleteAllActivity();
    }
}
