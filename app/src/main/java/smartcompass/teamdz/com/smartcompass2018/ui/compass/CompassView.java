package smartcompass.teamdz.com.smartcompass2018.ui.compass;

import android.hardware.SensorEvent;

public interface CompassView {
    void setChangeDirection(SensorEvent sensorEvent);
    void setLocationText(double lat, double lon);
    void showViewMaps();

    void needToCalibrateCompass(int calibrate);

    void showWarning();
}
