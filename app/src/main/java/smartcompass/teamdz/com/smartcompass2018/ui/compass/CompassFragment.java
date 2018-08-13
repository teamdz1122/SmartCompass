package smartcompass.teamdz.com.smartcompass2018.ui.compass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import smartcompass.teamdz.com.smartcompass2018.R;
import smartcompass.teamdz.com.smartcompass2018.base.BaseFragment;
import smartcompass.teamdz.com.smartcompass2018.service.location.CompassLocationService;
import smartcompass.teamdz.com.smartcompass2018.data.sensor.CompassSensorManager;
import smartcompass.teamdz.com.smartcompass2018.utils.CompassUtils;
import smartcompass.teamdz.com.smartcompass2018.utils.Constants;
import smartcompass.teamdz.com.smartcompass2018.view.DirectionImage;

public class CompassFragment extends BaseFragment<CompassPresenter> implements SensorEventListener, CompassView {

    private CompassSensorManager mCompassSensorManager;
    private DirectionImage mDirectionImage;
    private TextView mTvDegrees, mTvLat, mTvLon, mTvCity;
    private float mMagnetic;
    private float[] mAccelValues = new float[]{0f, 0f, 9.8f};
    ;
    private float[] mMagneticValues = new float[]{0.5f, 0f, 0f};
    private float mAzimuth;
    //private CompassLocation mCompassLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private AddressResultReceiver mResultReceiver;
    private String mAddressOutput;

    public CompassFragment() {
    }

    public static CompassFragment newInstance() {
        return new CompassFragment();
    }

    @Override
    protected CompassPresenter createPresenter() {
        return new CompassPresenter(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompassSensorManager = new CompassSensorManager(getActivity());
        //mCompassLocation = new CompassLocation(getActivity());
        mResultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        createLocationRequest();

        //getLastLocation();

    }


    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_compass, container, false);
        mDirectionImage = view.findViewById(R.id.iv_compass);
        mTvDegrees = view.findViewById(R.id.tv_degrees);
        mTvLat = view.findViewById(R.id.tv_latitude);
        mTvLon = view.findViewById(R.id.tv_longitude);
        mTvCity = view.findViewById(R.id.tv_location_city);
        getLastLocation();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mPresenter.setLocationText(location.getLatitude(), location.getLongitude());
                    mLastLocation = location;
                    getAddress();
                }

            }
        };

        return view;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLastLocation = location;
                            mPresenter.setLocationText(location.getLatitude(), location.getLongitude());
                            getAddress();
                        } else {
                            Log.d("nghia", "null");
                        }
                    }
                });
    }

    private void getAddress() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(getActivity(),
                    "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        startIntentService();
    }

    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), CompassLocationService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCompassSensorManager.registerAccListener(this);
        mCompassSensorManager.registerMagneticListener(this);
        mCompassSensorManager.registerOrientListener(this);
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCompassSensorManager.unregisterListener(this);
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        mPresenter.changeDirection(sensorEvent);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void setChangeDirection(SensorEvent sensorEvent) {
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
            /*updateMagneticView(mCompassSensorManager.getMagnetic());
            CompassUtils.displayCurrentDirection(getApplicationContext(),
                    mAzimuth, mDirectUnitLeft, mDirectText, mDirectUnitRight);*/
            mDirectionImage.setDegress(-mAzimuth);
            mDirectionImage.invalidate();
            mTvDegrees.setText(String.valueOf(Math.round(mAzimuth)) + "\u00b0");
        }
    }

    @Override
    public void setLocationText(double lat, double lon) {
        String latitude = CompassUtils.decimalToDMS(lat) + CompassUtils.getLatSymbol(lat, true);
        String longitude = CompassUtils.decimalToDMS(lon) + CompassUtils.getLatSymbol(lon, false);
        mTvLat.setText(latitude);
        mTvLon.setText(longitude);
    }

    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (mAddressOutput == null) {
                mAddressOutput = "";
            }
            displayAddressOutput(mAddressOutput);
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(getActivity(),
                        "Address found, ",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void displayAddressOutput(String currentAddress) {
        mTvCity.setText(currentAddress);
    }
}
