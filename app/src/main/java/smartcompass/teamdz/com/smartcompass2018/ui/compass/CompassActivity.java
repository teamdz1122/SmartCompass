package smartcompass.teamdz.com.smartcompass2018.ui.compass;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import smartcompass.teamdz.com.smartcompass2018.R;

public class CompassActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 200;
    /*private ImageView mIvCompass;
        private TextView mTvDegress;
        private SensorManager mSensorManager;

        private float[] mGravity = new float[]{0f,0f,9.8f};
        private float[] mGeomagnetic = new float[3];
        private float azimuth = 0f;
        private float currectAzimuth = 0f;
        private static final float ALPHA = 0.1f;*/
    private String[] PERMISSION_NAME = {Manifest.permission.ACCESS_FINE_LOCATION};
    private CompassFragment mCompassFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass_layout);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            initView();
        }


    }

    private void initView() {
        mCompassFragment = (CompassFragment) getSupportFragmentManager().findFragmentById(R.id.layout_content);
        if (mCompassFragment == null) {
            mCompassFragment = CompassFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.layout_content, mCompassFragment).commitAllowingStateLoss();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, PERMISSION_NAME, REQUEST_CODE_PERMISSION);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView();
                } else {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /* @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = lowpass(sensorEvent.values, mGravity);

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            mGeomagnetic = lowpass(sensorEvent.values, mGeomagnetic);
        }
        float R[] = new float[9];
        float I[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                mGeomagnetic);
        if (success) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);
            azimuth = (float) Math.toDegrees(orientation[0]);
        }
        *//*final float alpha = 0.97f;
        synchronized (this) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha*mGravity[0] + (1-alpha)*sensorEvent.values[0];
                mGravity[1] = alpha*mGravity[1] + (1-alpha)*sensorEvent.values[1];
                mGravity[2] = alpha*mGravity[2] + (1-alpha)*sensorEvent.values[2];
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha*mGeomagnetic[0] + (1-alpha)*sensorEvent.values[0];
                mGeomagnetic[1] = alpha*mGeomagnetic[1] + (1-alpha)*sensorEvent.values[1];
                mGeomagnetic[2] = alpha*mGeomagnetic[2] + (1-alpha)*sensorEvent.values[2];
            }

            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth+360)%360;
                Log.d("nghia", "azimuth = " + azimuth);
                mTvDegress.setText(String.valueOf(azimuth));
                Animation anim = new RotateAnimation(-currectAzimuth, -azimuth, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                currectAzimuth = azimuth;
                anim.setDuration(500);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);

                mIvCompass.startAnimation(anim);
            }
        }*//*

    }

    private float[] lowpass(float[] values, float[] gravity) {
        if (gravity == null) {
            return values;
        }
        for (int i = 0; i<values.length; i++){
            gravity[i] = gravity[i] + ALPHA * (values[i] - gravity[i]);
        }
        return gravity;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }*/
}
