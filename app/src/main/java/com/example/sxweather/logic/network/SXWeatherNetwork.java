package com.example.sxweather.logic.network;

import android.util.Log;

import com.example.sxweather.logic.model.DailyResponse;
import com.example.sxweather.logic.model.PlaceResponse;
import com.example.sxweather.logic.model.RealtimeResponse;
import com.example.sxweather.logic.model.Weather;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//网络工作，这个类的工作对所有网络请求API进行封装
//包括请求城市数据和请求天气数据
public class SXWeatherNetwork {
    private static PlaceService placeService = ServiceCreator.PlaceCreate(PlaceService.class);
    //最后读取回来的信息通过PlaceResult返回
    private static PlaceResponse PlaceResult = new PlaceResponse();

    //获取城市信息
    public PlaceResponse searchPlaces(String query) {
        try {
            Response<PlaceResponse> response = placeService.searchPlaces(query).execute();
            //body是我们读取到的信息
            PlaceResponse body = response.body();
            if (body != null) {
                PlaceResult = body;
            } else {
                //kotlin中的奇怪异常在java中可以用日志输出
                Log.d("SXWeatherNetwork", "response body is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("SXWeather", "resumeWithException");
        } finally {
            return PlaceResult;
        }
    }


    /*public static PlaceResponse  sendNetwork() {
        final PlaceResponse[] body = new PlaceResponse[1];
        responseBody.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                body[0] =response.body();//body是我们读取到的信息
                if(body[0] !=null){
                    onResponse(call,response);
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {

            }

        });
        return body[0];
    }
   */
    //对于天气的接口请求
    private final WeatherService weatherService = ServiceCreator.WeatherCreate(WeatherService.class);
    private static RealtimeResponse realtimeResponse = new RealtimeResponse();
    private static DailyResponse dailyResponse = new DailyResponse();
    //对于实时天气的获取
    //在这遇到了bug，找不到我们的lng的值？什么原因？没有传进来？lng为null
    public RealtimeResponse getRealtimeWeather(String lng,String lat){
        Log.d("SXWeatherNetWork","查看传入的lng和lat" + lng + " " + lat);
        try{
            Response<RealtimeResponse> response=weatherService.getRealtimeWeather(lng,lat).execute();
            if(response.body()!=null){
                realtimeResponse=response.body();
                Log.d("SXWeatherNetWorkWeather","status is "+realtimeResponse.getStatus());
                Log.d("SXWeatherNetWokrWeather","lng:"+lng+" "+"lat:"+lat);
            }else{
                Log.d("SXWeatherNetWorkWeather","Realtime Response is null");
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            return realtimeResponse;
        }
    }
    //对于未来天气的获取
    public DailyResponse getDailyResponse(String lng,String lat){
        Log.d("SXWeatherNetWork Daily","查看传入的lng和lat" + lng + " " + lat);
        try{
            Response<DailyResponse> response= weatherService.getDailyWeather(lng,lat).execute();
            if(response.body()!=null){
                dailyResponse=response.body();
                Log.d("SXWeatherNetWorkWeather","Daily result"+dailyResponse.getResult());
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            return dailyResponse;
        }
    }
}

