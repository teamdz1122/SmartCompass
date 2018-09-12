package smartcompass.teamdz.com.smartcompass2018.ui.maps;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import smartcompass.teamdz.com.smartcompass2018.R;
import smartcompass.teamdz.com.smartcompass2018.base.BaseActivity;
import smartcompass.teamdz.com.smartcompass2018.data.sensor.CompassSensorManager;
import smartcompass.teamdz.com.smartcompass2018.utils.CompassUtils;
import smartcompass.teamdz.com.smartcompass2018.utils.MapsUtils;
import smartcompass.teamdz.com.smartcompass2018.view.DirectionImage;

public class MapsActivity extends BaseActivity<MapsPresenter> implements OnMapReadyCallback, SensorEventListener, GoogleMap.OnMyLocationButtonClickListener, MapsView {

    private DirectionImage mIvCompassMap;
    private ImageView mIvAround;

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private Marker mMarker;
    private CompassSensorManager mCompassSensorManager;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private float[] mAccelValues = new float[]{0f, 0f, 9.8f};
    private float[] mMagneticValues = new float[]{0.5f, 0f, 0f};
    private float mAzimuth;
    private double mCurrentLat, mCurrentLong;
    private LatLng mLatLng;


    @Override
    protected MapsPresenter createPresenter() {
        return new MapsPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("nghia", "onCreate");
        setContentView(R.layout.activity_maps);

        mCompassSensorManager = new CompassSensorManager(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mIvAround = findViewById(R.id.iv_around_compass);
        mIvCompassMap = findViewById(R.id.iv_compass_map);
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

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("nghia", "onMapReady");
        mMap = googleMap;
        createLocationRequest();
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                mLastLocation = location;
                if (mMarker != null) {
                    mMarker.remove();
                }
                mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location));
                markerOptions.position(mLatLng);
                markerOptions.anchor(0.5f, 1.5f);
                markerOptions.flat(true);
                mMarker = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));

                if (mFusedLocationClient != null) {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            }
        }

    };


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
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
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
            if (mMarker != null) {
                mMarker.setRotation(mAzimuth);
            }
            mPresenter.rotateCamera(mAzimuth);
            mIvCompassMap.setDegress(-mAzimuth);
            mIvCompassMap.invalidate();
        }
    }

    @Override
    public void rotateCamera(float azimuth) {
        if (mMap == null) {
            return;
        }
        CameraPosition oldPos = mMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).target(new LatLng(mCurrentLat, mCurrentLong)).bearing(azimuth).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onMyLocationButtonClick() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
        return false;
    }


}
