package com.example.sxweather.logic.network;

import com.example.sxweather.logic.model.DailyResponse;
import com.example.sxweather.logic.model.RealtimeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WeatherService {
    //仍然使用get声明来访问API接口
    //使用@Path注解来向请求接口中动态传入经纬度的坐标
    @GET("v2.5/OTI46zZoNo984B5Q/{lng},{lat}/realtime.json")
    Call<RealtimeResponse> getRealtimeWeather(@Path("lng")String lng,@Path("lat")String lat);

    @GET("v2.5/OTI46zZoNo984B5Q/{lng},{lat}/daily.json")
    Call<DailyResponse> getDailyWeather(@Path("lng")String lng,@Path("lat")String lat);
}
