package smartcompass.teamdz.com.smartcompass2018.ui.compass;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import smartcompass.teamdz.com.smartcompass2018.R;
import smartcompass.teamdz.com.smartcompass2018.base.BaseActivity;
import smartcompass.teamdz.com.smartcompass2018.base.BasePresenter;
import smartcompass.teamdz.com.smartcompass2018.utils.InterstitialUtils;

public class CompassActivity extends BaseActivity {

    private static final int REQUEST_CODE_PERMISSION = 200;
    private String[] PERMISSION_NAME = {Manifest.permission.ACCESS_FINE_LOCATION};
    private CompassFragment mCompassFragment;


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

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
        InterstitialUtils.getSharedInstance().init(getApplicationContext());

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


}
