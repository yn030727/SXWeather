package com.example.sxweather.logic;

import android.media.Session2Command;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sxweather.SXWeatherApplication;
import com.example.sxweather.logic.dao.PlaceDao;
import com.example.sxweather.logic.model.DailyResponse;
import com.example.sxweather.logic.model.PlaceResponse;
import com.example.sxweather.logic.model.RealtimeResponse;
import com.example.sxweather.logic.model.Weather;
import com.example.sxweather.logic.network.SXWeatherNetwork;

import java.util.List;

import kotlinx.coroutines.Dispatchers;
import okhttp3.Dispatcher;
//仓库类，主要工作就是判断调用方请求的数据应该是从本地数据源中获取还是从网络数据源中获取，并将获得的数据返回给调用方
//仓库类像是一个数据获取与缓存的中间层
//这里用一个类来作为仓库层的统一封装入口
public class Repository {
    //一个集合，用来存放读取下来的城市信息，具体可以查看PlaceResponse的内部类Place
    private static List<PlaceResponse.Place> places;
    //我们使用LiveData将异步获取的数据以响应式编程的方式通知给上一层
    private static MutableLiveData<List<PlaceResponse.Place>> PlacesData = new MutableLiveData<>();
    //创建一个网络请求API封装的实例
    static SXWeatherNetwork sxWeatherNetwork = new SXWeatherNetwork();
    //------------------------------------------------------------------------------
    //调用SXWeatherNetwork的searchPlaces（）函数来搜索城市数据，如果服务器响应ok，那么就获取包含地区信息的集合
    //LiveData整合对象并返回
    public MutableLiveData<List<PlaceResponse.Place>> searchPlaces(String query) {
        //重要概念：Android不允许在主线程中进行网络请求
        //诸如读写数据库之类的本地数据操作也是不建议在主线程中进行，因此非常有必要在仓库层进行一次线程转换
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //将我们的query具体城市信息作为参数从外界读取进来
                    //会返回一个带有城市信息的PlaceResponse,也就是最底层的body
                    PlaceResponse placeResponse = sxWeatherNetwork.searchPlaces(query);
                    if (placeResponse.getStatus().equals("ok")) {
                        //包含地区信息的集合
                        places = placeResponse.getPlaces();
                        //我们再将集合放入到LiveData当中
                        PlacesData.postValue(places);
                    } else {
                        Log.d("Repository", "status is not ok");
                        Log.d("Repository", "status is " + placeResponse.getStatus());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Log.d("Repository", "success");
                }
            }
        }).start();
        return PlacesData;
    }
    //---------------------------------------------------------------------
    //
    //----------------------------------------------------------------------
    //存储我们读取到的天气信息
    //在仓库层我们并没有提供两个分别用于获取实时天气信息和未来天气信息的方法，而是提供了一个refreshWeather()方法用来刷新天气信息
    //对于调用方而言，需要调用两次请求才能获得其想要的所有天气数据明显是比较烦琐的行为，因此最好的做法就是在仓库层在进行一次统一的封装
    //以livedate的形式保存数据
    public MutableLiveData<Weather> refreshWeather(String lng,String lat){
        MutableLiveData<Weather> weatherMutableLiveData=new MutableLiveData<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
               try{
                   RealtimeResponse realtimeResponse;
                   DailyResponse dailyResponse;
                   realtimeResponse=sxWeatherNetwork.getRealtimeWeather(lng,lat);
                   dailyResponse=sxWeatherNetwork.getDailyResponse(lng,lat);
                   //从网络获取到的数据可以使用
                   if(realtimeResponse.getStatus().equals("ok") && dailyResponse.getStatus().equals("ok")){
                       //将两个获取到的数据整合成一个我们之前封装的Weather对象
                       Weather weather=new Weather(realtimeResponse.getResult().getRealtime(), dailyResponse.getResult().getDaily());
                       weatherMutableLiveData.postValue(weather);
                   }else{
                       Log.d("Repository","Realtime response error! it is "+dailyResponse.getStatus());
                       Log.d("Repository","Daily response error! it is"+dailyResponse.getStatus());
                   }
               }catch (Exception e){
                   e.printStackTrace();
                   Log.d("Repository","WeatherNetwork Error");
               }finally {
                   Log.d("Repository","success");
               }
            }
        }).start();
        return weatherMutableLiveData;
    }
    //这里两个天气的获取采用了并发执行，不过在同时得到他们的响应之后，才会进行下一步只想程序
    //逻辑分析：如果他们的相应状态都是ok的，那么就将Realtime和Daily对象取出并封装到一个Weather对象中
    //（翻阅第一行代码）优化：关于使用Kotlin代码的优化可能性
    //我们使用了协程来简化网络回调的写法，导致NetWork中的每一个网络请求都可能会抛出异常，所以我们对仓库层的每一个网络请求都进行了try catch处理
    //这增加了代码的复杂度，我们可以使用某个统一的入口函数进行封装，使得只要进行一次try catch处理就行
    /*
    *private fun <T> fire(context: CoroutineContext, block: suspend() -> Result<T>)=liveData<Result<T>>(context) {
    *    val result =try{
    *        block()
    *    }catch(e: Exception){
    *       Result.failure<T>(e)
    *    }
    *    emit(result)
    *    }
    * }
    * */
    //在仓库层对我们的代码进行实现
    //仓库层是我们本机数据源的上面一层，里面存储的LiveDate对外提供了数据
    //在这里仓库层只是做了一层接口封装，这里的实现方式并不标准，因为即使是对SharedPreferences文件进行读写操作，也不太建议在主线程中进行
    //最佳的实现方式还是开启一个线程来执行这些比较耗时的任务，然后通过LiveData对象进行数据返回
    public static PlaceResponse.Place getSavedPlace(){
        return PlaceDao.getSavedPlace();
    }
    public static void savePlace(PlaceResponse.Place place){
        PlaceDao.savePlace(place);
    }
    public static boolean isPlaceSaved(){
        return PlaceDao.isPlaceSaved();
    }
}
