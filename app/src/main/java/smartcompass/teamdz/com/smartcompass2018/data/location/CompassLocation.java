package smartcompass.teamdz.com.smartcompass2018.data.location;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import smartcompass.teamdz.com.smartcompass2018.R;
import smartcompass.teamdz.com.smartcompass2018.utils.Constants;

public class CompassLocation extends IntentService {
    private static final String TAG = CompassLocation.class.getSimpleName();
    private Location mLocation;
    private ResultReceiver mReceiver;

    public CompassLocation() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if (mReceiver == null) {
            Log.d("nghia", "null");
            return;
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";
        mLocation = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(),
                    mLocation.getLongitude(),
                    1);
        } catch (IOException e) {
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, e);
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + mLocation.getLatitude() +
                    ", Longitude = " +
                    mLocation.getLongitude(), illegalArgumentException);

        }

        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                {
                    errorMessage = getString(R.string.no_address_found);
                    Log.e(TAG, errorMessage);
                }
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            //ArrayList<String> addressFragments = new ArrayList<String>();
            /*for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                Log.d("nghia","a = " + address.getAddressLine(i));
                addressFragments.add(address.getAddressLine(i));
            }*/
            String addressStr = address.getLocality();
            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.SUCCESS_RESULT, addressStr);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
