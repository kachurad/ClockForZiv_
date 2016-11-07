package clock.for_ziv.stopper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import clock.for_ziv.ClockFragment;
import clock.for_ziv.R;
import clock.for_ziv.Utils;

public class StopperFragment extends Fragment implements ClockFragment {

    private static final long DELAY_MILLIS = 10L;

    private static final String KEY_PREFS = "PREFS_" + StopperFragment.class.getSimpleName();
    private static final String KEY_IS_RUNNING = "IS_RUNNING";
    private static final String KEY_BASE_VALUE = "BASE_VALUE";
    private static final String KEY_STOP_VALUE = "STOP_VALUE";

    private TextView stopWatch;
    private long baseValue;
    private long stopValue;
    private boolean isRunning;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateStopWatchValue();
            startUpdating();
        }
    };

    public static StopperFragment newInstance() {
        Bundle args = new Bundle();
        StopperFragment fragment = new StopperFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stopper_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        stopWatch = (TextView) view.findViewById(R.id.chronometer);
    }

    @Override
    public void onStart() {
        super.onStart();
        restoreState();
        if (isRunning) {
            startUpdating();
        } else {
            stopWatch.setText(Utils.formatTimeString(stopValue - baseValue));
        }
    }

    private void restoreState() {
        SharedPreferences prefs = getContext().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        isRunning = prefs.getBoolean(KEY_IS_RUNNING, false);
        baseValue = prefs.getLong(KEY_BASE_VALUE, 0);
        stopValue = prefs.getLong(KEY_STOP_VALUE, 0);

        long now = SystemClock.elapsedRealtime();
        if (baseValue > now) {
            baseValue = 0;
        }
        if (stopValue > now) {
            baseValue = 0;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        saveState();
        if (isRunning) {
            stopUpdating();
        }
    }

    private void saveState() {
        SharedPreferences.Editor edit = getContext().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE).edit();
        edit.putBoolean(KEY_IS_RUNNING, isRunning);
        edit.putLong(KEY_BASE_VALUE, baseValue);
        edit.putLong(KEY_STOP_VALUE, stopValue);
        edit.apply();
    }

    @Override
    public void set() {
        if (isRunning) {
            Toast.makeText(getContext(), R.string.err_mes_is_running, Toast.LENGTH_SHORT).show();
        } else {
            stopValue = 0;
            baseValue = 0;
            stopWatch.setText(Utils.formatTimeString(0));
            saveState();
        }
    }

    @Override
    public void start() {
        if (!isRunning) {
            if (baseValue == 0) {
                baseValue = SystemClock.elapsedRealtime();
            }
            if (stopValue != 0) {
                baseValue += SystemClock.elapsedRealtime() - stopValue;
                stopValue = 0;
            }
            startUpdating();
            isRunning = true;
            saveState();
        }
    }

    @Override
    public void stop() {
        if (isRunning) {
            stopValue = SystemClock.elapsedRealtime();
            stopUpdating();
            isRunning = false;
            saveState();
        }
    }

    @Override
    public boolean hasSetFuncEnabled() {
        return true;
    }

    private void startUpdating() {
        stopWatch.postDelayed(runnable, DELAY_MILLIS);
    }

    private void stopUpdating() {
        stopWatch.removeCallbacks(runnable);
    }

    private void updateStopWatchValue() {
        long l = SystemClock.elapsedRealtime() - baseValue;
        stopWatch.setText(Utils.formatTimeString(l));
    }


}
