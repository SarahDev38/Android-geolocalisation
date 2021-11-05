package com.sarahdev.mymapapplication.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Calculs {

    private static double toRadians(double deg) {
        return (deg * Math.PI / 180.0);
    }

    protected static double distance(double lat1, double long1, double lat2, double long2){
        int R = 6371010; // m
        double dLat = toRadians(lat2-lat1);
        double dLon = toRadians(long2-long1);
        lat1 = toRadians(lat1);
        lat2 = toRadians(lat2);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    protected static double stackDistance(Location current, double stackDistance) {
        if (Trajet.positions != null && Trajet.positions.size()>0) {
            LatLng last = Trajet.positions.get(Trajet.positions.size() - 1).getLatLng();
            stackDistance += distance(last.latitude, last.longitude, current.getLatitude(), current.getLongitude());
            return getOneDecimalFloat(stackDistance);
        }
        return 0;
    }

    protected static double totalStackDistance(List<Position> positions) {
        double stackDistance = 0;
        if (positions != null && positions.size()>0) {

            for (int i = 1 ; i < positions.size() ; i++) {

                stackDistance += distance(positions.get(i-1).getLatitude(), positions.get(i-1).getLongitude(), positions.get(i).getLatitude(), positions.get(i).getLongitude());
            }
        }
        return stackDistance;
    }

    protected static double startDistance(Location current) {
        if (Trajet.positions != null && Trajet.positions.size()>0) {
            LatLng start = Trajet.positions.get(0).getLatLng();
            return getOneDecimalFloat(distance(start.latitude, start.longitude, current.getLatitude(), current.getLongitude()));
        }
        return 0;
    }

    protected static double speed(double currentLatitude, double currentLongitude, long currentTime ) {
        double speed = 0;
        if (Trajet.positions != null && Trajet.positions.size()>0) {
            Position last = Trajet.positions.get(Trajet.positions.size()-1);
            speed = 3600 * distance(last.getLatLng().latitude, last.getLatLng().longitude, currentLatitude, currentLongitude) / ((currentTime- last.getTime())); // en km/h
        }
        return speed;
    }

    protected static double averageSpeed() {
        double speed = 0;
        if (Trajet.positions != null && Trajet.positions.size() > 0) {
            List<Position> lastPositions = new ArrayList<Position>();
            int i = 0;
            while (i < Trajet.positions.size() && i < 3) {
                int index = Trajet.positions.size() - i - 1;
                lastPositions.add(Trajet.positions.get(index));
                i++;
            }
            for (Position p : lastPositions) {
                speed += p.getSpeed();
            }
            speed /= i;
        }
        return getOneDecimalFloat(speed);
    }

    public static double getOneDecimalFloat(double number) {
        BigDecimal bigD = new BigDecimal(Double.toString(number));
        return bigD.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
