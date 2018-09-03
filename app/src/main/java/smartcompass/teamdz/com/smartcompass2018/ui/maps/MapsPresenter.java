package smartcompass.teamdz.com.smartcompass2018.ui.maps;

import smartcompass.teamdz.com.smartcompass2018.base.BasePresenter;

public class MapsPresenter extends BasePresenter<MapsView> {
    public MapsPresenter(MapsView mapsView) {
        super.attach(mapsView);
    }

    public void rotateCamera(float mAzimuth) {
        mView.rotateCamera(mAzimuth);
    }
}
