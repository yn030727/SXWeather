package com.example.sxweather.logic.model;

import com.google.gson.annotations.SerializedName;

//这是返回的实时天气预报信息的数据类型（获取实时天气信息接口所返回的JSON数据格式）
/*
 * {
 *   "status": "ok",
 *   "result": {
 *       "realtime": {
 *           "temperature": 23.16,
 *           "skycon": "WIND",
 *           "air_quality": {
 *               "aqi": { "chn": 17.0 }
 *            }
 *       }
 *   }
 * }
 * */
//data class RealtimeResponse(val status:String , val result :Result){}
public class RealtimeResponse {
    private String status;
    private Result result;
    /*
    * 可以定义一个变量，来为之后的报错进行调试（Error?）
    * */
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
    //----------------------------------------------------
    //内部类
    public class Result{
        private Realtime realtime;

        public Realtime getRealtime() {
            return realtime;
        }

        public void setRealtime(Realtime realtime) {
            this.realtime = realtime;
        }
    }
    public class Realtime{
        private String skycon;
        private Float temperature;
        @SerializedName("air_quality")
        private AirQuality airQuality;

        public AirQuality getAirQuality() {
            return airQuality;
        }

        public Float getTemperature() {
            return temperature;
        }

        public String getSkycon() {
            return skycon;
        }

        public void setAirQuality(AirQuality airQuality) {
            this.airQuality = airQuality;
        }

        public void setSkycon(String skycon) {
            this.skycon = skycon;
        }

        public void setTemperature(Float temperature) {
            this.temperature = temperature;
        }
    }

    public class AQI{
        private Float chn;

        public Float getChn() {
            return chn;
        }

        public void setChn(Float chn) {
            this.chn = chn;
        }
    }
    public class AirQuality{
        private AQI aqi;

        public AQI getAqi() {
            return aqi;
        }

        public void setAqi(AQI aqi) {
            this.aqi = aqi;
        }
    }
}
