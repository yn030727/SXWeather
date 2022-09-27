package com.example.sxweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sxweather.logic.model.DailyResponse;
import com.example.sxweather.logic.model.RealtimeResponse;
import com.example.sxweather.logic.model.Sky;
import com.example.sxweather.logic.model.Weather;
import com.example.sxweather.ui.weather.WeatherViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

//在这里我们请求太难起数据，并将数据展示到界面上
public class WeatherActivity extends AppCompatActivity {
    //创建一个WeatherViewModel实例
    private static WeatherViewModel weatherViewModel;
    public WeatherViewModel getWeatherViewModel(){
        return weatherViewModel;
    }
    //下拉刷新
    private SwipeRefreshLayout swipeRefreshLayout;
    //滑动菜单
    private DrawerLayout drawerLayout;
    public DrawerLayout getDrawerLayout(){return drawerLayout;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //这时滑动菜单的输出部分，通过按钮点击-------------------------------------
        Button navButton =(Button)findViewById(R.id.navBtn);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //我们监听DrawerLayout的状态，当滑动菜单隐藏的时候，同时也要隐藏输入法
        drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(drawerView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });




        //优化部分：我们发现背景图和状态栏没有融合在一起，这样的视觉体验没有达到最佳的效果。------------------------------------------
        //现在学一种更简单的方式将我们的背景图和状态栏结合在一起
            //（1).调用getWindow().getDecorView()方法拿到当前的Activity的DecorView
        View decorView=getWindow().getDecorView();
            //(2).调用setSystemUiVisibility()方法来改变系统UI的展示
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //(3).最后调用setStatusBarColor方法将状态栏设置为透明色
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //后续还要修改下布局，由于系统状态栏已经成为我们布局的一部分，因此会导致天气界面的布局整体向上偏移了一些，这样头部布局就显得有些靠上了
        //可以借助android:firstSystemWindows属性
        //获取ViewModel实例
        weatherViewModel=new ViewModelProvider(this).get(WeatherViewModel.class);
        //获取下拉刷新实例
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        if(weatherViewModel.location_lng.isEmpty()){
            if(getIntent().getStringExtra("location_lng")==null){
                weatherViewModel.location_lng="";
            }else{
                weatherViewModel.location_lng=getIntent().getStringExtra("location_lng");
            }
        }
        if(weatherViewModel.location_lat.isEmpty()){
            if(getIntent().getStringExtra("location_lat")==null){
                weatherViewModel.location_lat="";
            }else{
                weatherViewModel.location_lat=getIntent().getStringExtra("location_lat");
            }
        }
        if(weatherViewModel.place.isEmpty()){
            if(getIntent().getStringExtra("place_name")==null){
                weatherViewModel.place="";
            }else{
                weatherViewModel.place=getIntent().getStringExtra("place_name");
            }
        }
        //观察LiveData的变化
       weatherViewModel.weatherLiveDate.observe(this, new Observer<Weather>() {
           //存储天气数据的仓库层发生了变化
           @Override
           public void onChanged(Weather weather) {
               //如果里面的天气信息不为空
               if(weather!=null){
                   Log.d("WeatherActivity","输出天气信息");
                   //就将这些天气数据显示在UI上
                   showWeatherInfo(weather);
               }else{
                   Toast.makeText(getApplicationContext(),"获取不到天气信息", Toast.LENGTH_SHORT).show();
               }
               swipeRefreshLayout.setRefreshing(false);
           }
       });
        //更新我们的天气数据保存到livedata集合当中

        weatherViewModel.refreshWeather(weatherViewModel.location_lng, weatherViewModel.location_lat);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setRefreshing(true);//可以让下拉刷新进度条显示出来
        //在另外写一个手动下拉刷新信息实现逻辑
        //通过事件当发现我们手动下拉刷新时，就更新我们的天气信息
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                weatherViewModel.refreshWeather(weatherViewModel.location_lng, weatherViewModel.location_lat);
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }
    //封装一下拉刷新天气信息（后面发现封装会稍微方便点调用）
    public void refreshWeather(){
        weatherViewModel.refreshWeather(weatherViewModel.location_lng,weatherViewModel.location_lat);
        swipeRefreshLayout.setRefreshing(true);
    }
    //从weather对象中获取数据，然后将对象中获取到的数据显示到相应的控件上
    //在未来几天的天气预报中，我们添加了for-in（kotlin）循环来处理每天的天气信息，在循环中动态加载forecast_item.xml设置相应的数据
    //生活指数方面虽然服务器会返回很多天的数据，但是界面上只需要当天的数据就可以了，因此我们所有的数据都取了下标为零的那个元素的数据
    //最后记得让ScrollView变成可见状态
    private void showWeatherInfo(Weather weather){
        //展示天气信息在UI界面，我们分成了两部分，未来和实时
        //先将我们的地点信息加载到TextView上面
        TextView placeName=findViewById(R.id.placeName);
        placeName.setText(weatherViewModel.place);
        //1.先获取我们存放未来和实时信息的天气数据
        RealtimeResponse.Realtime realtime=weather.getRealtime();
        DailyResponse.Daily daily=weather.getDaily();
        //Log.d("WeatherActivity","daily"+daily.getTemperatures().get(0).toString());
        //------------------------------------------------------------------>
        //2.先填充实时天气预报，即now.xml控件上的信息
            //（1）.获取实例
        TextView  currentTempText =findViewById(R.id.currentTemp);
        TextView currentASky=findViewById(R.id.currentSky);
        TextView currentAQIText=findViewById(R.id.currentAQI);
        RelativeLayout nowLayout=findViewById(R.id.nowLayout);
            //（2）.修改控件->Skycon是Response内的一个关于天气数据的内部类(实时和未来里面都有这个类，getXXX就是他的获取方法）
        currentTempText.setText((int)realtime.getTemperature().floatValue()+"℃");
        Log.d("WeatherActivity","Temperature is " + realtime.getTemperature());
        currentASky.setText(Sky.getSky(realtime.getSkycon()).getInfo());//获取天气的名字信息
        currentAQIText.setText("空气指数"+(int)realtime.getAirQuality().getAqi().getChn().floatValue());
        nowLayout.setBackgroundResource(Sky.getSky(realtime.getSkycon()).getBg());//获取天气的背景信息
        //--------------------------------------------------------
        //3.填充life_index.xml生活指数布局中的数据
        //(1).获取网络请求到的生活指数信息
        DailyResponse.LifeIndex lifeIndex=daily.getLifeIndex();
        //(2).获取控件
        TextView coldRiskText=findViewById(R.id.coldRiskText);
        TextView dressingText=findViewById(R.id.dressingText);
        TextView ultravioletText=findViewById(R.id.ultravioletText);
        TextView carWashingText =findViewById(R.id.carWashingText);
        ScrollView scrollView=findViewById(R.id.weatherLayout);
        //(3).修改UI
        //PS:生活指数会返回很多天的数据，但是界面上只需要当天的数据就可以了，因此这里我们对所有的生活指数都取了下标为零的那个元素的数据
        //记得前面说的把ScrollView设为可见
        coldRiskText.setText(lifeIndex.getClodRisk().get(0).getDesc());
        dressingText.setText(lifeIndex.getDressing().get(0).getDesc());
        ultravioletText.setText(lifeIndex.getUltraviolet().get(0).getDesc());
        carWashingText.setText(lifeIndex.getCarWashing().get(0).getDesc());
        Log.d("WeatherActivity",lifeIndex.getClodRisk().get(0).getDesc().toString());
        scrollView.setVisibility(View.VISIBLE);

        //-------------------------------------------------------------------->
        //4.接下来填充未来几天天气预报，即forecast.xml控件上的信息
            //(1).因为要循环处理获取到的数据，我们用for循环来解决问题，那么首先我们需要得到天数信息作为循环的终止次数
            //因为我们DailyResponse内有存放天气信息skyCon的一个集合，所以我们可以通过集合的size来获得总共的预测天数
            //-----??第三个大bug，为什么会出现size导致的空指针异常报错
        int day=daily.getSkycon().size();
        Log.d("WeatherActivityDaily",""+daily.getSkycon().size());
        Log.d("WeatherActivity",""+daily.getSkycon().get(0));
            //(2).在forecast中我们设定了一个linearLayout用于后面加载子布局
        LinearLayout forecastLayout=findViewById(R.id.forecastLayout);
        forecastLayout.removeAllViews();//清空所有布局
            //(3).开始通过循环加载布局
        for(int i=0;i<day;i++){//这里是kotlin中的for in循环
            //先获得skyCon数据
            DailyResponse.Skycon skycon=daily.getSkycon().get(i);
            DailyResponse.Temperature temperature=daily.getTemperature().get(i);
            //加载布局
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            //获取控件
            ImageView skyIcon= view.findViewById(R.id.skyIcon);
            TextView skyInfo=view.findViewById(R.id.skyInfo);
            TextView dataInfo=view.findViewById(R.id.dateInfo);//dataInfo里面存放的是skycon内的第二个信息，时间
            TextView temperatureInfo =view.findViewById(R.id.temperatureInfo);
            //获取天气(skycon内部的value属性，放的是天气的类型——多云，晴天...)
            //获取日期（这两个数据都是skycon类里面的，也就是我们json格式数据读取出来的)
            Sky sky=Sky.getSky(skycon.getValue());
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            //修改控件
            skyIcon.setImageResource(sky.getIcon());
            skyInfo.setText(sky.getInfo());
            dataInfo.setText(simpleDateFormat.format(skycon.getDate()));
            temperatureInfo.setText((int)temperature.getMin().floatValue()+"~"+(int)temperature.getMax().floatValue());
            //添加view到我们的父布局当中
            forecastLayout.addView(view);
        }

    }
}