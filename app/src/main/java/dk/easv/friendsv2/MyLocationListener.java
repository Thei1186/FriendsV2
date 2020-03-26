package dk.easv.friendsv2;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {

    IViewCallBack m_view;

    public MyLocationListener(IViewCallBack view)
    { m_view = view; }

    @Override
    public void onLocationChanged(Location location) {
        m_view.setCurrentLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
