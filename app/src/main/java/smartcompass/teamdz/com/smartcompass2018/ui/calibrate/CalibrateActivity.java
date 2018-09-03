package smartcompass.teamdz.com.smartcompass2018.ui.calibrate;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import smartcompass.teamdz.com.smartcompass2018.R;
import smartcompass.teamdz.com.smartcompass2018.base.BaseActivity;
import smartcompass.teamdz.com.smartcompass2018.ui.compass.CompassFragment;

public class CalibrateActivity extends BaseActivity<CalibratePresenter> implements SensorEventListener, CalibrateView {

    private Toolbar mToolbar;
    private TextView mTvMagnetic;
    private int mCaliMagnetic;

    @Override
    protected CalibratePresenter createPresenter() {
        return new CalibratePresenter();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibrate_activity);
        initView();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.calibrate_title);
        mTvMagnetic = findViewById(R.id.tv_magnetic_value);
        mCaliMagnetic = getIntent().getIntExtra(CompassFragment.CALIBRATE_MAGENTIC, 0);
        setTextForMagnetic(mCaliMagnetic);
    }

    private void setTextForMagnetic(int magnetic) {
        if (magnetic == 1) {
            mTvMagnetic.setText(R.string.calibrate_low);
            mTvMagnetic.setTextColor(ContextCompat.getColor(this, R.color.color_calibrate_low));
        } else if (magnetic == 2) {
            mTvMagnetic.setText(R.string.calibrate_medium);
            mTvMagnetic.setTextColor(ContextCompat.getColor(this, R.color.color_calibrate_medium));
        } else {
            mTvMagnetic.setText(R.string.calibrate_hight);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        switch (sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                switch (i) {
                    case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                        setTextForMagnetic(SensorManager.SENSOR_STATUS_ACCURACY_LOW);
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                        setTextForMagnetic(SensorManager.SENSOR_STATUS_ACCURACY_LOW);
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                        setTextForMagnetic(SensorManager.SENSOR_STATUS_ACCURACY_LOW);
                        break;

                }
        }
    }
}
