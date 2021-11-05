package com.sarahdev.mymapapplication.model;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Velodyssee {
    public final static List<String> velodysseePositions2 = Arrays.asList(new String[]{"velodyssee1.gpx", "velodyssee2.gpx"});
    public final static List<String> velodysseePositions = Arrays.asList(new String[]{
            "SityTrail1.gpx",
            "SityTrail2.gpx",
            "SityTrail3.gpx",
            "SityTrail5.gpx",
            "SityTrail7.gpx",
            "SityTrail8.gpx",
            "SityTrail10b.gpx",
            "SityTrail11.gpx",
            "SityTrail13.gpx",
            "SityTrail14.gpx",
            "SityTrail16.gpx",
            "SityTrailx.gpx"
    });
    public final static Map<String, LatLng> velodysseeLodges = new HashMap<String, LatLng>() {
        {
          put("fin étape 1", new LatLng(46.140, -1.167));
          put("fin étape 2", new LatLng(45.913,-0.958));
          put("fin étape 3", new LatLng(45.640,-1.072));
          put("fin étape 4", new LatLng(45.495,-1.140));
          put("fin étape 5", new LatLng(45.193,-1.074));
          put("fin étape 6", new LatLng(44.840,-1.130));
          put("fin étape 7", new LatLng(44.444,-1.252));
          put("fin étape 8", new LatLng(44.216,-1.293));
          put("fin étape 9", new LatLng(43.846,-1.357));
          put("fin étape 10", new LatLng(43.535,-1.456));
          put("fin étape 11", new LatLng(43.387,-1.690));
        }

    };
}
