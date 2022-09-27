package com.example.sxweather.logic.network;

import com.example.sxweather.logic.model.PlaceResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
//1.定义一个用于访问API的Retrofit接口
//用get来传参一般注解就用query，用post一般就用FieId
//我们在searchPlaces方法上声明了一个@GET注解，调用方法时会自动发起一条GET请求
//搜索城市数据的API只有query这个参数需要动态指定，所以另外两个参数直接放在注解当中就行了
public interface PlaceService {
    //OTI46zZoNo984B5Q
    @GET("v2/place?token=OTI46zZoNo984B5Q&lang=zh_CN")
    Call<PlaceResponse> searchPlaces(@Query("query")String query);
}
