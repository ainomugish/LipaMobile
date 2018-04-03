package com.duali.itouchpop2_test;

import android.net.wifi.p2p.WifiP2pInfo;

/**
 * Created by i on 9/17/16.
 */
public class ConnCreated {
    private WifiP2pInfo deviceInfo;

    public ConnCreated (WifiP2pInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public WifiP2pInfo getDeviceInfo() {
        return deviceInfo;
    }
}
