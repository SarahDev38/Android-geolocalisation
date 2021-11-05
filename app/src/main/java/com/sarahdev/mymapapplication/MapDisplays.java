package com.sarahdev.mymapapplication;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.sarahdev.mymapapplication.model.Position;
import com.sarahdev.mymapapplication.persistance.Files;
import com.sarahdev.mymapapplication.persistance.GPXFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MapDisplays {

    public static void displayPositions(GoogleMap map, List<Position> positions) {
        PolylineOptions polylineOptions = new PolylineOptions();
        for (Position position : positions) {
            polylineOptions.add(position.getLatLng());
        }
        Polyline polyline = map.addPolyline(polylineOptions);
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(10);
        polyline.setColor(Color.CYAN);
        polyline.setJointType(JointType.ROUND);
    }

    public static void displayCircle(GoogleMap map, Location location) {
        if (location != null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(location.getLatitude(), location.getLongitude()))
                    .strokeColor(Color.YELLOW)
                    .radius(0.3);
            map.addCircle(circleOptions);
        }
    }

    public static void displayGPX(Context context, GoogleMap map, List<String> filenames) {
        for (String filename : filenames) {
            PolylineOptions polylineOptions = new PolylineOptions();
            try {
                InputStream inputStream = context.getAssets().open(filename);
                List<Location> gpxList = GPXFile.parseGPX(inputStream);
                for(int i = 0; i < gpxList.size(); i++){
                    polylineOptions.add( new LatLng(
                            (gpxList.get(i)).getLatitude(),
                            (gpxList.get(i)).getLongitude()
                    ));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Polyline polyline = map.addPolyline(polylineOptions);
            polyline.setEndCap(new RoundCap());
            polyline.setWidth(5);
            polyline.setColor(Color.YELLOW);
            polyline.setJointType(JointType.ROUND);
        }
    }

    public static void displayLocations(GoogleMap map, Map<String, LatLng> locationsMap) {
        for (Map.Entry<String, LatLng> entry : locationsMap.entrySet()) {
            map.addMarker(
                    new MarkerOptions()
                            .position(entry.getValue())
                            .title(entry.getKey())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            );
        }
    }

    public static void displayFiles(Context context, GoogleMap map, List<String> filenames) {
        List<Integer> colorsId = Arrays.asList(R.color.list0, R.color.list1, R.color.list2, R.color.list3, R.color.list4, R.color.list5);
        int colorId = 0;
        for (String filename : filenames) {
            PolylineOptions polylineOptions = new PolylineOptions();
            List<Position> positions = Files.readFile(context, filename);
            for (Position position : positions) {
                polylineOptions.add(position.getLatLng());
            }
            Polyline polyline = map.addPolyline(polylineOptions);
            polyline.setEndCap(new RoundCap());
            polyline.setWidth(10);
            polyline.setColor(context.getResources().getColor(colorsId.get(colorId)));
            colorId = (colorId +1) % 6;
            polyline.setJointType(JointType.ROUND);
        }
    }
}
