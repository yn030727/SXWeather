package com.example.sxweather.ui.place;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sxweather.MainActivity;
import com.example.sxweather.R;
import com.example.sxweather.SXWeatherApplication;
import com.example.sxweather.WeatherActivity;
import com.example.sxweather.logic.model.PlaceResponse;

import java.util.List;

public class PlaceFragment extends Fragment {
    CharSequence sequence;
    //定义一个ViewModel实例
    private  static PlaceViewModel placeViewModel;
    public PlaceViewModel getViewModel(){
        return placeViewModel;
    }
    @Nullable
    @Override
    //这里是Fragment的标准用法,记载碎片布局
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //获取了存储数据的ViewModel
        placeViewModel=new ViewModelProvider(this).get(PlaceViewModel.class);
        View v=inflater.inflate(R.layout.fragment_place,container,false);
        return v;
    }
    //1.先给RecyclerView设置LayoutManager和适配器
    //2.使用PlaceViewModel中的placeList集合作为数据源
    //3.重点！！！！！！！！！！！！！：调用EditText的addTextChangedListener()方法来监听搜索框内容的变化情况(每当搜索框发生了变化，我们就获取新的内容，然后传递给PlaceViewModel中的searchPlaces()方法）
    //这样就可以发起搜索城市数据的网络请求了
    //4.当输入框内容为空时，我们就将RecyclerView隐藏起来，只显示背景图
    //5.借助LiveData来获取到服务器响应的数据
    //PS:接下来的数字序列就代表这五个过程
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //0.（我们再PlaceAdapter构造器获取到Place的信息后，我们保存了它）完成存储功能后，我们要对存储的状态进行判断和读取
        //这里对PlaceFragment进行了判断，如果当前已有存储的城市数据，那么就获取已存储的城市数据并解析成place对象
        //然后直接使用这个place的经纬度坐标和城市名，并直接跳转到WeatherActivity,这样就不用重新选择了
        //PS：前半段判断条件是之后加上去的，原因：我们之前是如果有选中的城市，保存在sharedPreferences文件中，就直接跳转到WeatherActivity
        //但是在PlaceFragment嵌入WeatherActivity之后，如果继续只有后面一个判断，就会造成无限循环跳转的情况
        if(getActivity().getClass().equals(MainActivity.class)&&placeViewModel.isPlaceSaved()){
            //只有当PlaceFragment被嵌入MainActivity中，并且之前存在选中的城市，此时会直接跳转到WeatherActivity
            PlaceResponse.Place place=placeViewModel.getSavedPlace();
            Intent intent=new Intent(getContext(), WeatherActivity.class);
            intent.putExtra("location_lng",place.getLocation().getLng());
            intent.putExtra("location_lat",place.getLocation().getLat());
            intent.putExtra("place_name",place.getName());
            startActivity(intent);
            getActivity().finish();
            return;
        }
        //补充：读取数据，并显示在我们的cardView上面
        RecyclerView recyclerView=getActivity().findViewById(R.id.recyclerView);
        //1,2 加载RecyclerView的相关设置，并从placeViewModel中获得我们的集合
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());//SXWeatherApplication.getContext()
        recyclerView.setLayoutManager(layoutManager);
        PlaceAdapter placeAdapter=new PlaceAdapter(this,placeViewModel.getPlaceList());
        recyclerView.setAdapter(placeAdapter);
        //3.获取控件实例，并写EditText的监听事件，用于发送搜索城市数据的网络请求
        ImageView imageViewBG=(ImageView) getActivity().findViewById(R.id.bgImageView);
        EditText PlaceEdit =getActivity().findViewById(R.id.searchPlaceEdit);
        Button button=(Button)getActivity().findViewById(R.id.searchButton);
        //我们在搜索框内部嵌套一个按钮，通过按钮进行搜索，这样我们可以避免输入上产生问题，导致内容变化错误
        PlaceEdit.addTextChangedListener(new TextWatcher() {//搜索框内容变化监听
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //改变时候
                //参数分析：CharSequence在这里就是上面的数据
                String nowText = s.toString();
                //我们用日志打印一下我们输入的城市信息
                Log.d("PlaceFragment","query is"+nowText);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //4.当输入框为空的
                        if(nowText.isEmpty()){
                            Toast.makeText(getActivity(),"你不能搜索空的内容",Toast.LENGTH_SHORT).show();
                            //我们将RecyclerView隐藏，并将我们的背景图显示出来
                            recyclerView.setVisibility(View.GONE);
                            imageViewBG.setVisibility(View.VISIBLE);
                            //同时我们要清空我们的placeViewModel里面集合存放的城市信息，为了下一个搜索城市进行准备
                            //如何清空集合内的城市信息？
                            //我们可以在我们的PlaceViewModel类里面添加方法，来清空城市信息（之后是否需要其他方法？）
                            placeViewModel.clearPlaceList();
                            //提醒我们的构建器我们的数据已经发生了修改
                            placeAdapter.notifyDataSetChanged();
                        }else{
                            //有数据的时候，开始进行搜索操作
                            placeViewModel.searchPlaces(nowText);
                            Log.d("PlaceFragment",""+placeViewModel.mutableLiveData.getValue());
                        }
                    }
                });
                /* //4.当输入框为空的
                if(nowText.isEmpty()){
                    //我们将RecyclerView隐藏，并将我们的背景图显示出来
                    recyclerView.setVisibility(View.GONE);
                    imageViewBG.setVisibility(View.VISIBLE);
                    //同时我们要清空我们的placeViewModel里面集合存放的城市信息，为了下一个搜索城市进行准备
                    //如何清空集合内的城市信息？
                    //我们可以在我们的PlaceViewModel类里面添加方法，来清空城市信息（之后是否需要其他方法？）
                    placeViewModel.clearPlaceList();
                    //提醒我们的构建器我们的数据已经发生了修改
                    placeAdapter.notifyDataSetChanged();
                }else{
                    //有数据的时候，开始进行搜索操作
                    placeViewModel.searchPlaces(nowText);
                    Log.d("PlaceFragment",""+placeViewModel.mutableLiveData.getValue());
                }*/
            }
            @Override
            public void afterTextChanged(Editable s) {
                //改变之后
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PlaceEdit.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),"你不能搜索空的内容",Toast.LENGTH_SHORT).show();
                }
            }
        });


        //5.获取服务器响应的数据通过LivaData
        //我们对PlaceViewModel中的PlaceLivaData对象进行观察，当有任何数据变化时，就会回调到传入的Observer接口中
        //如果数据不为空，我们就将数据添加到PlaceViewModel的placeList集合中
        //并通知PlaceAdapter刷新界面
        //数据为空表示发生异常，弹出一个提示
        placeViewModel.mutableLiveData.observe(getViewLifecycleOwner(), new Observer<List<PlaceResponse.Place>>() {
            @Override
            public void onChanged(List<PlaceResponse.Place> placeList) {
                //这里我们获得了placeList集合
                //placeList集合不为空
                if(placeList!=null){
                    //把我们的RecyclerView显示出来
                    //把我们的背景图隐藏起来
                    Log.d("PlaceFragment","数据已变化");
                    recyclerView.setVisibility(View.VISIBLE);
                    imageViewBG.setVisibility(View.GONE);
                    placeViewModel.clearPlaceList();
                    //将数据添加到placeList集合中
                    placeViewModel.addPlaceList(placeList);
                    placeAdapter.notifyDataSetChanged();
                }else{
                    Log.d("PlaceViewModel","earth???????????");
                    Toast.makeText(getActivity(),"地球上找不到你要找的地方",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
