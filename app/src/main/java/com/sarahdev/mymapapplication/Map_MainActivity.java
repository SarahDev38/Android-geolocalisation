package com.sarahdev.mymapapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sarahdev.mymapapplication.model.Trajet;
import com.sarahdev.mymapapplication.model.Velodyssee;
import com.sarahdev.mymapapplication.persistance.Files;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.sarahdev.mymapapplication.model.Calculs.getOneDecimalFloat;
import static com.sarahdev.mymapapplication.DisplayFilesInfo_Activity.EXTRA_CHECKEDFILENAMES;
import static com.sarahdev.mymapapplication.DisplayFilesInfo_Activity.EXTRA_DELETEDFILENAMES;
import static com.sarahdev.mymapapplication.DisplayFilesInfo_Activity.EXTRA_FILESINFOS;
import static com.sarahdev.mymapapplication.SaveFile_Activity.EXTRA_DATE;
import static com.sarahdev.mymapapplication.SaveFile_Activity.EXTRA_DISTANCE;
import static com.sarahdev.mymapapplication.SaveFile_Activity.EXTRA_INFOS;
import static com.sarahdev.mymapapplication.SaveFile_Activity.EXTRA_SPEED;

public class Map_MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ToggleButton btnStart, btnCentrer, btnFiles;
    private Button btnAltitude, btnDistance, btnSpeed;
    private Drawable startDrawable, pauseDrawable, mapmodeDrawable, centerDrawable ;
    private LocationManager locManager;
    private GeoListener locListener;
    private final static int REQUEST_CODE_UPDATE_LOCATION = 48;
    private double altitude, latitude, longitude ;
    private Trajet trajet = new Trajet();
    private List<String> filesToDisplay = new ArrayList<>();
    private double distance = 0;
    protected final static String VELODYSSEE = "La Vélodyssée";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(Map_MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_UPDATE_LOCATION);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);

        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initButtons();
        trajet.setPositions(Files.readTrajet(this));

        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locManager != null) {
            locListener = new GeoListener();
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, locListener);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trajet.getLastPoint(),18));
    }

    protected void updateTrajet(Trajet trajet, Location location){
        updateTextViews(trajet);
        if (trajet != null && btnCentrer.isChecked()) {
            MapDisplays.displayPositions(mMap, Trajet.positions);
            MapDisplays.displayCircle(mMap,location);
            CameraPosition.Builder builder = CameraPosition.builder(mMap.getCameraPosition());
            builder.target(trajet.getLastPoint());
            if (location != null && location.hasBearing())
                builder.bearing(location.getBearing());
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        }
     }

    protected void updateTextViews(Trajet trajet){
        if (trajet != null) {
            String plus = (trajet.getCurrentAltitude() > trajet.getStartAltitude()) ? "+ " : "- ";
            btnAltitude.setText("" + (int) (trajet.getCurrentAltitude()) +  " m  \n( " + plus +  Math.abs((int) (trajet.getCurrentAltitude() - trajet.getStartAltitude())) + " m)");
            distance = trajet.getStackDistance();
            if (distance < 1000) {
                btnDistance.setText(""+((int) (distance)) + " \nmètres");
            } else {
                 btnDistance.setText(""+getOneDecimalFloat(distance / 1000) + "\nkm");
            }
            btnSpeed.setText(""+ getOneDecimalFloat(trajet.getCurrentSpeed())+ "\nkm/h");
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_UPDATE_LOCATION :
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(Map_MainActivity.this, "permission refusée", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locListener != null) {
            if (locManager == null) {
                locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                locManager.removeUpdates(locListener);
                locManager = null;
                locListener = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void toggleRun(View view){
        if (btnStart.isChecked()) {
            if (locListener.getLatitude() != 0 && locListener.getLongitude() != 0 ) {
                displayCurrentRoute();
                btnStart.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_yellow)));
                btnStart.setCompoundDrawablesWithIntrinsicBounds(null, pauseDrawable, null, null);
            } else {
                btnStart.toggle();
            }
        }
        if (! btnStart.isChecked()) {
            btnStart.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
            btnStart.setCompoundDrawablesWithIntrinsicBounds(null, startDrawable, null, null);
        }
     }

    private void displayCheckedFiles() {
        mMap.clear();
        if (filesToDisplay != null && filesToDisplay.size()>0) {
            List<String> savedFilenames2 = new ArrayList<>(filesToDisplay);
            if (filesToDisplay.contains(VELODYSSEE)) {
                savedFilenames2.remove(VELODYSSEE);
                MapDisplays.displayLocations(mMap, Velodyssee.velodysseeLodges);
                MapDisplays.displayGPX(this, mMap, Velodyssee.velodysseePositions);
            }
            if (savedFilenames2.size() > 0) {
                for (String filename : savedFilenames2)
                    Log.i("*          *", " -----------------------------> filesToDisplay = " + filename);

                MapDisplays.displayFiles(this, mMap, savedFilenames2);
            }
        }
    }

    private void displayCurrentRoute() {
        if (trajet.positions.size()==0)
            trajet = new Trajet(locListener.getLatitude(), locListener.getLongitude(), locListener.getAltitude(), System.currentTimeMillis());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(trajet.getFirstPoint()).title("Départ"));
        updateTrajet(trajet, null);
     }

    public void toggleCenter(View view) {
        if (btnCentrer.isChecked()) {
            btnCentrer.setCompoundDrawablesWithIntrinsicBounds(null, centerDrawable , null, null);
            btnCentrer.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
         } else {
            btnCentrer.setCompoundDrawablesWithIntrinsicBounds(null, mapmodeDrawable , null, null);
            btnCentrer.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
            CameraPosition.Builder builder = CameraPosition.builder(mMap.getCameraPosition());
            builder.bearing(0f);
        }
    }

    public void reset(View view) {
        mMap.clear();
        btnStart.setChecked(true);
        trajet.resetPositions();
        Files.resetTrajet(this);
        toggleCenter(view);
        toggleRun(view);
        displayCheckedFiles();
    }

    public void saveRouteWithInfos(View view) {
        Files.automaticBackup(this, trajet.positions);
        Intent intent = new Intent(this, SaveFile_Activity.class);
        intent.putExtra(EXTRA_SPEED, String.valueOf(getOneDecimalFloat(Trajet.averageSpeed())));
        intent.putExtra(EXTRA_DISTANCE, String.valueOf(getOneDecimalFloat(distance)));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        intent.putExtra(EXTRA_DATE, currentDateandTime);
        startActivityForResult(intent, 2);
    }

    public void showFilesInfo(View view) {
        Files.automaticBackup(this, trajet.positions);
        Intent intent = new Intent(this, DisplayFilesInfo_Activity.class);
        intent.putExtra(EXTRA_CHECKEDFILENAMES, (Serializable) filesToDisplay);
        Map<String, List<String>> fileInfos = new HashMap<>(Files.readFileInfos(this));
        fileInfos.put(VELODYSSEE, Arrays.asList("véloroute de la Rochelle à St-Jean-de-Luz le long de la côte Atlantique","","400000", ""));
        intent.putExtra(EXTRA_FILESINFOS, (Serializable) fileInfos);
        startActivityForResult(intent, 1);
     }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            filesToDisplay = (List<String>) data.getSerializableExtra(EXTRA_CHECKEDFILENAMES);
            btnFiles.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                        (filesToDisplay.size()==0) ? R.color.cyan : R.color.cyan_light  )));
            List<String>  deletedFilenames =  (List<String>) data.getSerializableExtra(EXTRA_DELETEDFILENAMES);
            for (String filename : deletedFilenames)
                Files.deleteFile(this, filename);
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            String infos = (String) data.getSerializableExtra(EXTRA_INFOS);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            double averageSpeed = Trajet.averageSpeed();
            Files.saveTrajet(this, trajet.positions, infos, currentDateandTime, getOneDecimalFloat(averageSpeed), getOneDecimalFloat(distance));
        }
        displayCheckedFiles();
    }

    private void initButtons() {
        startDrawable = getResources().getDrawable(android.R.drawable.ic_media_play);
        pauseDrawable = getResources().getDrawable(android.R.drawable.ic_media_pause);
        mapmodeDrawable = getResources().getDrawable(android.R.drawable.ic_menu_mapmode);
        centerDrawable = getResources().getDrawable(android.R.drawable.ic_menu_mylocation);

        btnStart = findViewById(R.id.btnEnvoyer);
        btnCentrer = findViewById(R.id.btnCenter);
        btnFiles = findViewById(R.id.btnFiles);

        btnAltitude = findViewById(R.id.btnAltitude);
        btnDistance = findViewById(R.id.btnDistance);
        btnSpeed = findViewById(R.id.btnSpeed);
        btnAltitude.setClickable(false);
        btnDistance.setClickable(false);
        btnSpeed.setClickable(false);
    }

    public class GeoListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();
            if (!btnStart.isChecked()) {
                btnStart.setText("");
                btnStart.setTextSize(0);
                btnStart.setBackgroundColor(getResources().getColor(R.color.green));
                btnStart.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
            }
            if (trajet != null && btnStart.isChecked() ) {
                trajet.addNewLocation(location, System.currentTimeMillis());
                Files.writeTrajet(getApplicationContext(), trajet.positions);
                updateTrajet(trajet, location);
             }
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public int getAltitude() { return (int) altitude; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
    }
}