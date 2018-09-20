package smartcompass.teamdz.com.smartcompass2018.ui.calibrate;

import smartcompass.teamdz.com.smartcompass2018.base.BasePresenter;

public class CalibratePresenter extends BasePresenter<CalibrateView> {
    public CalibratePresenter(CalibrateView view) {
        super.attach(view);
    }

    public void setIconInText(String warningStr) {
        mView.addIconInText(warningStr);
    }
}
