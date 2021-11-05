package com.sarahdev.mymapapplication.model;

import com.google.android.gms.maps.model.LatLng;
import com.sarahdev.mymapapplication.model.Calculs;

import java.io.Serializable;

public class Position implements Serializable {
    private Double latitude;
    private Double longitude;
    private Double altitude ;
    private Long time;
    private Double speed;

    public Position(double latitude, double longitude, double altitude, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.time=time;
        this.speed = Calculs.speed(latitude, longitude, time);
    }

    public LatLng getLatLng() {
        return new LatLng(latitude,longitude);
    }

    protected double getAltitude() {
        return altitude;
    }

    protected double getLatitude() {
        return latitude;
    }

    protected double getLongitude() {
        return longitude;
    }

    protected long getTime() {
        return time;
    }

    protected double getSpeed(){
        return speed;
    }

}
