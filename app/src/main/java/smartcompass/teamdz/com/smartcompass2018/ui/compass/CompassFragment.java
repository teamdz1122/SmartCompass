package smartcompass.teamdz.com.smartcompass2018.ui.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import smartcompass.teamdz.com.smartcompass2018.R;
import smartcompass.teamdz.com.smartcompass2018.base.BaseFragment;
import smartcompass.teamdz.com.smartcompass2018.data.sensor.CompassSensorManager;
import smartcompass.teamdz.com.smartcompass2018.utils.CompassUtils;
import smartcompass.teamdz.com.smartcompass2018.view.DirectionImage;

public class CompassFragment extends BaseFragment<CompassPresenter> implements SensorEventListener, CompassView{

    private CompassSensorManager mCompassSensorManager;
    private DirectionImage mDirectionImage;
    private TextView mTvDegrees;
    private float mMagnetic;
    private float[] mAccelValues = new float[] { 0f, 0f, 9.8f };;
    private float[] mMagneticValues = new float[] { 0.5f, 0f, 0f };
    private float mAzimuth;

    public CompassFragment() {
    }

    public static CompassFragment newInstance() {
        return new CompassFragment();
    }

    @Override
    protected CompassPresenter createPresenter() {
        return new CompassPresenter(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompassSensorManager = new CompassSensorManager(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_compass, container, false);
        mDirectionImage = view.findViewById(R.id.iv_compass);
        mTvDegrees = view.findViewById(R.id.tv_degrees);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCompassSensorManager.registerAccListener(this);
        mCompassSensorManager.registerMagneticListener(this);
        mCompassSensorManager.registerOrientListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCompassSensorManager.unregisterListener(this);
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
            mTvDegrees.setText(String.valueOf(Math.round(mAzimuth)));
        }
    }
}
