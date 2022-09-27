package com.example.sxweather.ui.place;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sxweather.R;
import com.example.sxweather.SXWeatherApplication;
import com.example.sxweather.WeatherActivity;
import com.example.sxweather.logic.model.PlaceResponse;

import org.w3c.dom.Text;

import java.util.List;
//很简单的RecyclerView构建器创造
public class PlaceAdapter extends RecyclerView.Adapter<PlaceViewHolder>{
    List<PlaceResponse.Place> placeList;
    private static PlaceFragment placeFragment;
    //1.将我们的数据集合传入到RecyclerView当中
    public PlaceAdapter(PlaceFragment placeFragmentList,List<PlaceResponse.Place> placeList){
        this.placeFragment=placeFragmentList;
        this.placeList=placeList;

    }
    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item,parent,false);
        //View view=View.inflate(SXWeatherApplication.getContext(),R.layout.place_item,null);
        PlaceViewHolder placeViewHolder=new PlaceViewHolder(view);
        //点击我们的小卡片布局，要做到的操作就是跳转到天气输出界面
        placeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            //通过日志我们可以知道搜索后，出现的内容会被我们封装到livedata当中，然后送到构建器中
            @Override
            public void onClick(View v) {
                //val position =holder.adapterPosition  点击的序列号
                int position=placeViewHolder.getAdapterPosition();
                //val place = placeList(position)  点击的当前地方
                PlaceResponse.Place place=placeList.get(position);
                //获得Activity
                Activity activity=placeFragment.getActivity();

                if(activity.getClass().equals(WeatherActivity.class)){
                    //如果实在WeatherActivity中，那么关闭滑动菜单，给WeatherViewModel赋值新的经纬度坐标和地区名称
                    SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
                    DrawerLayout drawerLayout = (DrawerLayout) view.findViewById(R.id.drawerLayout);

                    ((WeatherActivity) activity).getDrawerLayout().closeDrawers();
                    ((WeatherActivity) activity).getWeatherViewModel().location_lng= place.getLocation().getLng();
                    ((WeatherActivity) activity).getWeatherViewModel().location_lat = place.getLocation().getLat();
                    ((WeatherActivity) activity).getWeatherViewModel().place = place.getName();
                    ((WeatherActivity) activity).refreshWeather();
                }else {
                    //将数据上传
                    Intent intent = new Intent(parent.getContext(), WeatherActivity.class);
                    intent.putExtra("location_lng", place.getLocation().getLng());
                    intent.putExtra("location_lat", place.getLocation().getLat());
                    intent.putExtra("place_name", place.getName());
                    //通过intent进行通信
                    //调用Fragment的方法启动我们的WeatherActivity
                    placeFragment.startActivity(intent);
                    activity.finish();
                }
                placeFragment.getViewModel().savePlace(place);
            }
        });
        return placeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        PlaceResponse.Place place=placeList.get(position);
        holder.placeName.setText(place.getName());
        holder.placeAddress.setText(place.getAddress());
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

}
class PlaceViewHolder extends RecyclerView.ViewHolder{
    TextView placeName;
    TextView placeAddress;
    View v;
    public PlaceViewHolder(@NonNull View itemView) {
        super(itemView);
        this.v=itemView;
        this.placeName=(TextView)itemView.findViewById(R.id.placeName);
        this.placeAddress=(TextView) itemView.findViewById(R.id.placeAddress);
    }
}
