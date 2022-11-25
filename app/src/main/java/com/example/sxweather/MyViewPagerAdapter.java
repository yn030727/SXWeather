package com.example.sxweather;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class MyViewPagerAdapter extends PagerAdapter {
    //viewArrayList来存放的是存储城市的布局
    //stringArrayList存放的是城市的信息
    private ArrayList<View> viewArrayList;
    private ArrayList<String> stringArrayList;
    public MyViewPagerAdapter(){}
    public MyViewPagerAdapter(ArrayList<View> viewArrayList,ArrayList<String> stringArrayList){
        this.viewArrayList=viewArrayList;
        this.stringArrayList=stringArrayList;
    }
    @Override
    public int getCount() {
        return viewArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(viewArrayList.get(position));
        return viewArrayList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(viewArrayList.get(position));
    }
    //显示我们当前查看的城市
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
