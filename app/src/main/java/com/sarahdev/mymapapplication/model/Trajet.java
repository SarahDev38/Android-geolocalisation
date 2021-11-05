package com.sarahdev.mymapapplication.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trajet {
    private static double distance; // cumulative distance from start location
    public static List<Position> positions;

    public Trajet() {
        positions = new ArrayList<>() ;
        distance = 0;
    }

    public Trajet(double startLatitude, double startLongitude, double startAltitude, long startTime) {
        positions = new ArrayList<>();
        positions.add(new Position(startLatitude, startLongitude, startAltitude, startTime));
        distance = 0;
    }

    public void resetPositions() {
        positions = new ArrayList<>() ;
        distance = 0;
    }

    public void addNewLocation(Location currentLocation, long currentTime) {
        distance = Calculs.stackDistance(currentLocation, distance);
        positions.add(new Position(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude(), currentTime ));
    }

    public double getStartAltitude() {
        return positions.get(0).getAltitude();
    }

    public double getCurrentAltitude() {
        if (positions != null && positions.size() > 0)
            return positions.get(positions.size()-1).getAltitude();
        return 0;
    }

    private static void setDistance(List<Position> pos) {
        distance = 0;
        if (pos != null && pos.size() > 1) {
            for (int i=1 ; i < pos.size() ; i++) {
                distance += Calculs.distance(pos.get(i-1).getLatitude(), pos.get(i-1).getLongitude(), pos.get(i).getLatitude(), pos.get(i).getLongitude());
            }
        }
    }

    public double getStackDistance() {
        return distance;
    }

    public double getCurrentSpeed() {
        return Calculs.averageSpeed();
    }

    public LatLng getLastPoint(){
        if (positions != null && positions.size() > 0)
            return positions.get(positions.size()-1).getLatLng();
        return new LatLng(45.63756259158247,5.142865302041173) ;
    }
    public LatLng getFirstPoint(){
        if (positions != null && positions.size() > 0)
            return positions.get(0).getLatLng();
        return new LatLng(45.63756259158247,5.142865302041173) ;
    }

    public static void setPositions(List<Position> pos) {
        positions = pos;
        setDistance(positions);
    }

    public static double averageSpeed() {
        double averageSpeed = 0;
        if (positions != null && positions.size() > 0) {
            for (Position position : positions)
                averageSpeed += position.getSpeed();
            averageSpeed /= positions.size();
        }
        return averageSpeed;
    }
}
