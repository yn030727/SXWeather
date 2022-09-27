package com.example.sxweather.ui.place;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.sxweather.logic.Repository;
import com.example.sxweather.logic.model.PlaceResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

//持有和UI元素相关的数据，并保证这些数据在旋转式不会消失
public class PlaceViewModel extends ViewModel {
    //1.获得仓库层实例
    Repository repository=new Repository();
    //2.创建一个LiveDate来存放String
    private static MutableLiveData<String> searchLiveData=new MutableLiveData<String>();
    //3.城市信息集合 @List<Place> 同时我们在这定义了一个存放城市信息的集合，用于对界面上显示的城市数据进行缓存，因为原则上与界面相关的数据都应该放到ViewModel中
    //这样可以保证他们在手机旋转的时候不会丢失
    private  static List<PlaceResponse.Place> placeList=new ArrayList<>();

    //获取LiveData数据
    public LiveData<List<PlaceResponse.Place>> mutableLiveData=Transformations.switchMap(searchLiveData, query -> repository.searchPlaces(query));
    //定义了一个searchPlaces()方法，并没有直接调用仓库层中的searchPlaces方法，而是将传入的搜索参数query赋值给了一个searchLivaData对象
    //并且使用Transformations的switchMap()方法来观察这个对象
    //我们在发送网络请求的同时，进行了外包装，让我们的LiveDate对象转换成了一个可供Activity观察的liveData对象
    public void searchPlaces(String query){
        searchLiveData.postValue(query);
    }


    //-----------------------------------------下面的方法是在开发中后续添加上去的
    //1.getPlaceList
    //2.clearPlaceList
    //3.addPlaceList

    //给外面（Fragment）一个方法用于得到我们保存数据的集合
    public List<PlaceResponse.Place> getPlaceList() {
        return placeList;
    }
    //在搜索城市时，因为是实时的，我们希望在输入框为空时，将整个ViewModel内存放的城市信息给进行清空
    public void clearPlaceList(){
        placeList.clear();
    }
    //在搜索框改变时，并且有值的时候，将我们的城市信息输入到集合当中
    public void addPlaceList(List<PlaceResponse.Place> places){
        placeList.addAll(places);
    }
    //保存地点到本地数据源中
    public static void savePlace(PlaceResponse.Place place){
        Repository.savePlace(place);
    }
    //获取保存的地点
    public static PlaceResponse.Place getSavedPlace(){
        return Repository.getSavedPlace();
    }
    //判断是否有保存的地点
    public static Boolean isPlaceSaved(){
        return Repository.isPlaceSaved();
    }
}
