package smartcompass.teamdz.com.smartcompass2018.ui.maps;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import smartcompass.teamdz.com.smartcompass2018.R;
import smartcompass.teamdz.com.smartcompass2018.data.sensor.CompassSensorManager;
import smartcompass.teamdz.com.smartcompass2018.utils.CompassUtils;

public class MapActivityExample extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private Marker mMarker;
    private CompassSensorManager mCompassSensorManager;

    private float[] mAccelValues = new float[]{0f, 0f, 9.8f};
    private float[] mMagneticValues = new float[]{0.5f, 0f, 0f};
    private float mAzimuth;
    private double mCurrentLat, mCurrentLong;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mCompassSensorManager = new CompassSensorManager(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mCurrentLat = location.getLatitude();
                            mCurrentLong = location.getLongitude();
                        } else {
                            Log.d("nghia", "null");
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getMyLocation();
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mCurrentLat, mCurrentLong))
                .flat(true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCompassSensorManager.registerAccListener(this);
        mCompassSensorManager.registerMagneticListener(this);
        mCompassSensorManager.registerOrientListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCompassSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelValues = CompassUtils.lowPass(sensorEvent.values, mAccelValues);
            mCompassSensorManager.setGravity(mAccelValues);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagneticValues = CompassUtils.lowPass(sensorEvent.values,
                    mMagneticValues);
            mCompassSensorManager.setGeoMagnetic(mMagneticValues);
        }

        mCompassSensorManager.updateAzimuth();
        float newAzimuth = mCompassSensorManager.getAzimuth();
        if (mAzimuth != newAzimuth) {
            mAzimuth = newAzimuth;
            mMarker.setRotation(mAzimuth);
            /*updateMagneticView(mCompassSensorManager.getMagnetic());
            CompassUtils.displayCurrentDirection(getApplicationContext(),
                    mAzimuth, mDirectUnitLeft, mDirectText, mDirectUnitRight);*/

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
