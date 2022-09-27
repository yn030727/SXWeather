package com.example.sxweather;

import android.app.Application;
import android.content.Context;

public class SXWeatherApplication extends Application {
    private static Context context;
    private static final String TOKEN="OTI46zZoNo984B5Q";
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
