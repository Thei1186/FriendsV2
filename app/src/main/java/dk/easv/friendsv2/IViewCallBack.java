package dk.easv.friendsv2;

import android.location.Location;

public interface IViewCallBack {

    void setCurrentLocation(Location location);

    void setCounter(int count);
}
