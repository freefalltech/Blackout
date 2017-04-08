package io.github.freefalltech.blackout;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    TextView searchWifiText;
    WifiManager wifiManager;
    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    private LocationManager mLocationManager = null;
    private LocationListener locationListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView subText = (TextView) findViewById(R.id.saveDataText);
        TextView titleHeader = (TextView) findViewById(R.id.heroTextMain);
        searchWifiText = (TextView) findViewById(R.id.wifiText);
        searchWifiText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/helvetica.otf"));
        titleHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/helvetica.otf"));
        subText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/centurygothic.ttf"));
        mLocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        dimBrightness();

        displayLocation();

        /*

        //initializing intentFIlter

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //initialize wifip2pmanager
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        */


    }

    private void start2Hotspot(String hotName) {
        //starts a hotspot with the coordinates as the name.
        //boolean isApOn = AccessPointManager.isApOn(this);
        //boolean configApState = AccessPointManager.configApState(MainActivity.this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiConfiguration netConfig = new WifiConfiguration();

        netConfig.SSID = hotName;
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        netConfig.preSharedKey = "testPassword";
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

        try {
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apstatus = (Boolean) setWifiApMethod.invoke(wifiManager, netConfig, true);

            Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled");
            Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState");
            int apstate = (Integer) getWifiApStateMethod.invoke(wifiManager);
            Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            netConfig = (WifiConfiguration) getWifiApConfigurationMethod.invoke(wifiManager);
            Log.e("CLIENT", "\nSSID:" + netConfig.SSID + "\nPassword:" + netConfig.preSharedKey + "\n");

            WifiConfiguration apConfig = null;
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            final ComponentName cn = new ComponentName(
                    "com.android.settings",
                    "com.android.settings.TetherSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    private void start1Hotspot(String hotName) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == grantResults[0]) {
            displayLocation();
        }
    }

    public void displayLocation() {
        MyLocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
            locationListener.createHotspot();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }


    public void searchWifi(View v) {
        searchWifiText = (TextView) findViewById(R.id.wifiText);
        searchWifiText.setVisibility(View.GONE);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rLayout);
        rl.setVisibility(View.VISIBLE);

        //starting the wifi search process


    }

    private void dimBrightness() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(this)) {
            Settings.System.putInt(this.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, 50);

            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = 0.5f;
            getWindow().setAttributes(lp);

        }
    }

    public void turnNetworkOff(View view) {
        Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
        startActivity(intent);
    }
/*
    public static void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        // Do something in response to the boolean you are supplied

        if (isWifiP2pEnabled) {

        } else {
            //not enabled
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WifiBroadcastReciever(mManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }*/


    private class MyLocationListener implements LocationListener {
        Location currentBestLocation = null;
        static final int TEN_SECONDS = 1000 * 10;

        void createHotspot() {
            Location curLocation = getLastBestLocation();
            if (currentBestLocation == null)
                currentBestLocation = curLocation;
            if (currentBestLocation != null && curLocation != null && isBetterLocation(curLocation, currentBestLocation))
                currentBestLocation = curLocation;
            if (currentBestLocation != null) {
                String hotspotName = "Blackout*" + currentBestLocation.getLatitude() + "*" + currentBestLocation.getLongitude();
                TextView location = (TextView) findViewById(R.id.location);
                location.setText(hotspotName);
                start2Hotspot(hotspotName);
            }

        }

        private Location getLastBestLocation() {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                long GPSLocationTime = 0;
                if (null != locationGPS) {
                    GPSLocationTime = locationGPS.getTime();
                }

                long NetLocationTime = 0;

                if (null != locationNet) {
                    NetLocationTime = locationNet.getTime();
                }

                if (0 < GPSLocationTime - NetLocationTime) {
                    return locationGPS;
                } else {
                    return locationNet;
                }
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
            return null;
        }


        @Override
        public void onLocationChanged(Location location) {
            makeUseOfNewLocation(location);

            if (currentBestLocation == null) {
                currentBestLocation = location;
            }
            createHotspot();
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

        /**
         * This method modify the last know good location according to the arguments.
         *
         * @param location The possible new location.
         */
        void makeUseOfNewLocation(Location location) {
            if (isBetterLocation(location, currentBestLocation)) {
                currentBestLocation = location;
            }
        }

        /**
         * Determines whether one location reading is better than the current location fix
         *
         * @param location            The new location that you want to evaluate
         * @param currentBestLocation The current location fix, to which you want to compare the new one.
         */
        boolean isBetterLocation(Location location, Location currentBestLocation) {
            if (currentBestLocation == null) {
                // A new location is always better than no location
                return true;
            }

            // Check whether the new location fix is newer or older
            long timeDelta = location.getTime() - currentBestLocation.getTime();
            boolean isSignificantlyNewer = timeDelta > TEN_SECONDS;
            boolean isSignificantlyOlder = timeDelta < -TEN_SECONDS;
            boolean isNewer = timeDelta > 0;

            // If it's been more than two minutes since the current location, use the new location,
            // because the user has likely moved.
            if (isSignificantlyNewer) {
                return true;
                // If the new location is more than two minutes older, it must be worse.
            } else if (isSignificantlyOlder) {
                return false;
            }

            // Check whether the new location fix is more or less accurate
            int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
            boolean isLessAccurate = accuracyDelta > 0;
            boolean isMoreAccurate = accuracyDelta < 0;
            boolean isSignificantlyLessAccurate = accuracyDelta > 200;

            // Check if the old and new location are from the same provider
            boolean isFromSameProvider = isSameProvider(location.getProvider(),
                    currentBestLocation.getProvider());

            // Determine location quality using a combination of timeliness and accuracy
            if (isMoreAccurate) {
                return true;
            } else if (isNewer && !isLessAccurate) {
                return true;
            } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
                return true;
            }
            return false;
        }

        /**
         * Checks whether two providers are the same
         */
        private boolean isSameProvider(String provider1, String provider2) {
            if (provider1 == null) {
                return provider2 == null;
            }
            return provider1.equals(provider2);
        }
    }

}
