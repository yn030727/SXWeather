package com.example.sxweather.logic.model;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
//这里三个类对应的是从JSON上的数据
//在Kotlin中用data class就会自动帮我们封装好需要的方法
public  class PlaceResponse {
    private  String status=new String();
    private  List<Place> places=new ArrayList<>();

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public String getStatus() {
        return status;
    }


    //这里是内部的Place
    public class Place{
        private String name;
        @SerializedName("formatted_address")
        private  String address;
        private Location location;

        public void setName(String name) {
            this.name = name;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public Location getLocation() {
            return location;
        }

        public String getAddress() {
            return address;
        }

    }
    public static class Location{
        private String lng;
        private String lat;

        public Location(String lng, String lat) {
            this.lat = lat;
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

    }
}

