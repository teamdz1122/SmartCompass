package smartcompass.teamdz.com.smartcompass2018.ui.compass;

import android.hardware.SensorEvent;

import smartcompass.teamdz.com.smartcompass2018.base.BasePresenter;

public class CompassPresenter extends BasePresenter<CompassView> {

    public CompassPresenter(CompassView view) {
        super.attach(view);
    }

    public void changeDirection(SensorEvent sensorEvent) {
        mView.setChangeDirection(sensorEvent);
    }
}
