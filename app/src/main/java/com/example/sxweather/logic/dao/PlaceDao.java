package com.example.sxweather.logic.dao;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sxweather.SXWeatherApplication;
import com.example.sxweather.logic.model.PlaceResponse;
import com.google.gson.Gson;

//为什么需要存放本地数据源
//因为我们的目前完全没有对选中的城市进行记录。也就是说，每当你退出并重新进入程序之后，就需要再重新搜索并选择一次城市，这显然是不能接受的
//我们接下来要实现记录选中城市的功能
//我们需要运用到持久化技术，因为存储的数据并不属于关系型数据，因此也使用不着数据库存储技术
//直接使用SharedPreferences存储
//JSON数据是我们前面所有数据的总和，我们将他们分类，写成不同的类，但为了存储的方便，在持久化存储时我们一般再把他们整合回去

public class PlaceDao {

    //在PlaceDao中封装几个必要的存储和读取的数据接口
    //savePlace()方法用于将对象存储到SharedPreferences文件中
    public static void savePlace(PlaceResponse.Place place){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        //这里使用了一个技巧，我们先将JSON字符串从SharedPreferences文件中读取出来，然后再通过GSON将JSON字符串解析成Place对象返回
        Gson gson=new Gson();
        editor.putString("place",gson.toJson(place)) ;
        editor.apply();
    }
    //读取是相反的过程，我们先将JSON字符从SharedPreferences中读取出来
    //再通过GSON将JSON字符串解析成Place对象并返回
    public static PlaceResponse.Place getSavedPlace(){
        Gson gson=new Gson();
        PlaceResponse.Place Savedplace=gson.fromJson(sharedPreferences.getString("place",""),PlaceResponse.Place.class);
        return Savedplace;
    }
    public static boolean isPlaceSaved(){
        return sharedPreferences.contains("place");
    }
    private static SharedPreferences sharedPreferences= SXWeatherApplication.getContext().getSharedPreferences("placeData", Context.MODE_PRIVATE);

}
