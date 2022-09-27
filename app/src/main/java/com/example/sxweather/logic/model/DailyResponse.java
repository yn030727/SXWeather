package com.example.sxweather.logic.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//从网络接口获取到我们的未来几天的信息
/*
* {
*   "status": "ok",
*   "result": {
*       "daily": {
*           "temperature": [ {"max": 25.7 , "min": 20.3},......]
*           "skycon": [ {"value": "CLOUDY" , "date":"2019-10-20T00:00+08:00"},...]
*           "life_index":{
*               "coldRisk": [{"desc" , "易发"}, ... ]
*               "carWashing":[{"desc", "适宜"}, ... ]
*               "ultraviolet":[{"desc", "无"}, ...]
*               "dressing": [{"desc", "舒适"} , ...]
*           }
*       }
*   }
* }
* */
public class DailyResponse {
    private String status;
    private Result result;

    public Result getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }


    public class Result{
        Daily daily=new Daily();

        public Daily getDaily() {
            return daily;
        }

        public void setDaily(Daily daily) {
            this.daily = daily;
        }
    }
    public class Daily{
        private List<Temperature> temperature=new ArrayList<>();
        private List<Skycon> skycon=new ArrayList<>();
        @SerializedName("life_index")
        private LifeIndex life_index=new LifeIndex();

        public LifeIndex getLifeIndex() {
            return life_index;
        }

        public List<Skycon> getSkycon() {
            return skycon;
        }

        public List<Temperature> getTemperature() {
            return temperature;
        }
    }
    public class LifeIndex{
        //！！！！！！！！！！！！！！！！！！！！！！！第三个重大bug未出现在这
        //单词cold写成了clod导致出现json格式数据翻译上的错误，造成了空指针异常报错
        List<Life_index_num> coldRisk=new ArrayList<>();
        List<Life_index_num> carWashing =new ArrayList<>();
        List<Life_index_num> ultraviolet =new ArrayList<>();
        List<Life_index_num> dressing=new ArrayList<>();

        public List<Life_index_num> getCarWashing() {
            return carWashing;
        }

        public List<Life_index_num> getClodRisk() {
            return coldRisk;
        }

        public List<Life_index_num> getDressing() {
            return dressing;
        }

        public List<Life_index_num> getUltraviolet() {
            return ultraviolet;
        }
    }
    public class Temperature{
        Float max;
        Float min;

        public Float getMax() {
            return max;
        }

        public Float getMin() {
            return min;
        }
    }
    public class Skycon{
        String value=new String();
        Date date =new Date();

        public Date getDate() {
            return date;
        }

        public String getValue() {
            return value;
        }
    }
    public class Life_index_num{
        String desc=new String();

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

}
