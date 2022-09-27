package com.example.sxweather.logic.network;

import com.example.sxweather.logic.model.PlaceResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//2.创建一个Retrofit构建器
public class ServiceCreator {
    private static Retrofit retrofit=new Retrofit.Builder()
            .baseUrl("https://api.caiyunapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static PlaceService PlaceCreate(Class<PlaceService> placeServiceClass){
        PlaceService placeService=retrofit.create(placeServiceClass);
        return placeService;
    }
    //private PlaceService placeService=retrofit.create(PlaceService.class);
    //Call<PlaceResponse> responseBody=placeService.searchPlaces("");
    //创建天气接口
    public static WeatherService WeatherCreate(Class<WeatherService> weatherServiceClass){
        WeatherService weatherService=retrofit.create(weatherServiceClass);
        return weatherService;
    }
}

