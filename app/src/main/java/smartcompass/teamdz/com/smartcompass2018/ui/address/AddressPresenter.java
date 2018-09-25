package smartcompass.teamdz.com.smartcompass2018.ui.address;

import smartcompass.teamdz.com.smartcompass2018.base.BasePresenter;

public class AddressPresenter extends BasePresenter<AddressView> {

    public AddressPresenter(AddressView view) {
        super.attach(view);
    }

    public void setTextAll(String addressFull, double latitude, double longitude) {
        mView.setTextAll(addressFull, latitude, longitude);
    }
}
