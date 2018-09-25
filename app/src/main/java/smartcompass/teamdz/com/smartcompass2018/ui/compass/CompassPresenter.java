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

    public void setLocationText(double latitude, double longitude) {
        mView.setLocationText(latitude, longitude);
    }

    public void openViewMaps() {
        mView.showViewMaps();
    }

    public void calibrateCompass(int calibrate) {
        mView.needToCalibrateCompass(calibrate);
    }

    public void openWarning() {
        mView.showWarning();
    }
    public void openAddress() {
        mView.showAddress();
    }

    public void showLocationIcon() {
        mView.showLocationIcon();
    }
}
