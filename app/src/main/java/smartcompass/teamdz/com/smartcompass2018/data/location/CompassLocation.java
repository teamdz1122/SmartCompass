package smartcompass.teamdz.com.smartcompass2018.data.location;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import smartcompass.teamdz.com.smartcompass2018.R;

public class CompassLocation extends IntentService implements LocationListener {
    private static final String TAG = CompassLocation.class.getSimpleName();
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    private Context mContext;
    private boolean mIsGPSEnable = false;
    private boolean mIsNetWorkEnable = false;
    private boolean mCanGetLocation = false;
    private Location mLocation;
    private LocationManager mLocationManager;
    private double mLatidute;
    private double mLongidute;

    public CompassLocation() {
        super(TAG);
    }

    @SuppressLint("MissingPermission")
    private Location getLocation() {
        try {
            mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            mIsGPSEnable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            mIsNetWorkEnable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this);
            if (!mIsGPSEnable && !mIsNetWorkEnable) {
                Toast.makeText(this, getResources().getString(R.string.no_gps_no_network), Toast.LENGTH_SHORT).show();
            } else if (!mIsGPSEnable && mIsNetWorkEnable) {

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return mLocation;
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
