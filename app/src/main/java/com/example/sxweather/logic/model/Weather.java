package com.example.sxweather.logic.model;
//将获取到的实时和未来天气都封装成了一个类
public class Weather {
    //Response都是Json数据格式通过Retrofit转换过来的
    private RealtimeResponse.Realtime realtime;
    private DailyResponse.Daily daily;
    public Weather(RealtimeResponse.Realtime realtime, DailyResponse.Daily daily){
        this.realtime=realtime;
        this.daily=daily;
    }

    public void setDaily(DailyResponse.Daily daily) {
        this.daily = daily;
    }

    public DailyResponse.Daily getDaily() {
        return daily;
    }

    public void setRealtime(RealtimeResponse.Realtime realtime) {
        this.realtime = realtime;
    }

    public RealtimeResponse.Realtime getRealtime() {
        return realtime;
    }
}

