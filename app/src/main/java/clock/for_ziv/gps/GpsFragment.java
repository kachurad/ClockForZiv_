package clock.for_ziv.gps;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import clock.for_ziv.ClockFragment;
import clock.for_ziv.R;

public class GpsFragment extends Fragment implements ClockFragment, LocationListener {

    private static final String KEY_PREFS = "PREFS_" + GpsFragment.class.getSimpleName();
    private static final String KEY_IS_RUNNING = "IS_RUNNING";

    private static final int MIN_TIME = 1000;
    private static final int MIN_DISTANCE = 0;

    private boolean isRunning;
    private LocationManager locationManager;
    private TextView gpsText;
    private boolean listening;

    public static GpsFragment newInstance() {
        return new GpsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gps_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        gpsText = (TextView) view.findViewById(R.id.gps_text);
    }

    @Override
    public void onStart() {
        super.onStart();
        restoreState();
        start();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveState();
        stop();
    }

    private void restoreState() {
        SharedPreferences prefs = getContext().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        isRunning = prefs.getBoolean(KEY_IS_RUNNING, false);
    }

    private void saveState() {
        SharedPreferences.Editor edit = getContext().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE).edit();
        edit.putBoolean(KEY_IS_RUNNING, isRunning);
        edit.apply();
    }

    @Override
    public void set() {
        Toast.makeText(getContext(), "I'm doing nothing HAHA", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void start() {
        if (!listening) {
            Criteria criteria = new Criteria();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(MIN_TIME, MIN_DISTANCE, criteria, this, Looper.getMainLooper());
            listening = true;
            isRunning = true;
        }
    }

    @Override
    public void stop() {
        if (listening) {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
            isRunning = false;
            listening = false;
        }
    }

    @Override
    public boolean hasSetFuncEnabled() {
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            gpsText.setText(getString(R.string.gps_locaton_text, latitude, longitude));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
