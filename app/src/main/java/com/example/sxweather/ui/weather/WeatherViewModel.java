package com.example.sxweather.ui.weather;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.sxweather.logic.Repository;
import com.example.sxweather.logic.model.PlaceResponse;
import com.example.sxweather.logic.model.Weather;

public class WeatherViewModel extends ViewModel {
    //在place中我们获得了经纬度并且存放到了livedata当中，我们现在将他获取到
    //并作为参数传进去，作为搜索天气信息的必须条件
    private static MutableLiveData<PlaceResponse.Location> locationMutableLiveData=new MutableLiveData<>();
    public String location_lng="";
    public String location_lat="";
    public String place="";
    final static Repository repository=new Repository();
    //lamda表达式，location是PlaceResponse当中的一个内部类
    //从location中获取经纬度信息，通过刷新天气方法获取到天气信息，并将它放到livedata当中
    public static  final LiveData<Weather> weatherLiveDate= Transformations.switchMap(locationMutableLiveData, location->repository.refreshWeather(location.getLng(),location.getLat()));

    public void refreshWeather(String lng,String lat){
        Log.d("WeatherViewModel","lng is"+lng);
        Log.d("WeatherViewModel","lat is"+lat);
        locationMutableLiveData.postValue(new PlaceResponse.Location(lng,lat));
    }
    public MutableLiveData<PlaceResponse.Location> getLocationMutableLiveData(){
        return locationMutableLiveData;
    }
}
