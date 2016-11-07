package clock.for_ziv.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.Calendar;

import clock.for_ziv.ClockFragment;
import clock.for_ziv.R;
import clock.for_ziv.Utils;

public class TimerFragment extends Fragment implements ClockFragment {

    private static final int REQUEST_CODE = 101;

    private static final long DELAY_MILLIS = 1000L;

    private static final String KEY_PREFS = "PREFS_" + TimerFragment.class.getSimpleName();
    private static final String KEY_IS_RUNNING = "IS_RUNNING";
    private static final String KEY_HOURS = "HOURS";
    private static final String KEY_MINUTES = "MINUTES";
    private static final String KEY_SECONDS = "SECONDS";
    private static final String KEY_TIMER_VALUE = "TIMER_VALUE";

    private NumberPicker hoursPicker;
    private NumberPicker minutesPicker;
    private NumberPicker secPicker;

    private long timerValue;
    private boolean isRunning;
    private PendingIntent pendingIntent;
    private Runnable action = new Runnable() {
        @Override
        public void run() {
            if (updateTimerState())
                startUpdating();
        }
    };

    public static TimerFragment newInstance() {
        return new TimerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.timer_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        hoursPicker = (NumberPicker) view.findViewById(R.id.picker_hours);
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(24);
        minutesPicker = (NumberPicker) view.findViewById(R.id.picker_minutes);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        secPicker = (NumberPicker) view.findViewById(R.id.picker_seconds);
        secPicker.setMinValue(0);
        secPicker.setMaxValue(59);
    }

    @Override
    public void onStart() {
        super.onStart();
        restoreState();
        if (isRunning) {
            secPicker.setEnabled(false);
            minutesPicker.setEnabled(false);
            hoursPicker.setEnabled(false);
            updateTimerState();
            startUpdating();
        }
    }

    private void restoreState() {
        SharedPreferences prefs = getContext().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        isRunning = prefs.getBoolean(KEY_IS_RUNNING, false);
        timerValue = prefs.getLong(KEY_TIMER_VALUE, 0);
        hoursPicker.setValue(prefs.getInt(KEY_HOURS, 0));
        minutesPicker.setValue(prefs.getInt(KEY_MINUTES, 0));
        secPicker.setValue(prefs.getInt(KEY_SECONDS, 0));
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
        edit.putLong(KEY_TIMER_VALUE, timerValue);
        edit.putInt(KEY_HOURS, hoursPicker.getValue());
        edit.putInt(KEY_MINUTES, minutesPicker.getValue());
        edit.putInt(KEY_SECONDS, secPicker.getValue());
        edit.apply();
    }

    @Override
    public void set() {
        if (!isRunning) {
            hoursPicker.setValue(0);
            minutesPicker.setValue(0);
            secPicker.setValue(0);
            updateTimerState();
        } else {
            Toast.makeText(getContext(), R.string.err_mes_is_running, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void start() {
        if (!isRunning) {
            int seconds = secPicker.getValue();
            int minutes = minutesPicker.getValue();
            int hours = hoursPicker.getValue();

            secPicker.setEnabled(false);
            minutesPicker.setEnabled(false);
            hoursPicker.setEnabled(false);

            Calendar now = Calendar.getInstance();
            now.add(Calendar.SECOND, seconds);
            now.add(Calendar.MINUTE, minutes);
            now.add(Calendar.HOUR, hours);

            timerValue = now.getTimeInMillis();
            startUpdating();
            isRunning = true;
            saveState();
            registerNotification();
        }
    }

    private void registerNotification() {
        if (pendingIntent == null) {
            Intent broadcastIntent = new Intent(getContext(), AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(getContext(), REQUEST_CODE, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, timerValue, pendingIntent);
        }
    }

    private void cancelPendingNotification() {
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent = null;
        }
    }

    @Override
    public void stop() {
        if (isRunning) {
            secPicker.setEnabled(true);
            minutesPicker.setEnabled(true);
            hoursPicker.setEnabled(true);
            stopUpdating();
            cancelPendingNotification();
            isRunning = false;
            saveState();
        }
    }

    @Override
    public boolean hasSetFuncEnabled() {
        return true;
    }

    private void startUpdating() {
        hoursPicker.postDelayed(action, DELAY_MILLIS);
    }

    private void stopUpdating() {
        hoursPicker.removeCallbacks(action);
    }

    private boolean updateTimerState() {
        long now = System.currentTimeMillis();
        long diff = timerValue - now;
        int h = 0;
        int m = 0;
        int s = 0;
        if (diff >= Utils.HOUR_MILLIS) {
            h = (int) (diff / Utils.HOUR_MILLIS);
            diff -= h * Utils.HOUR_MILLIS;

        }
        if (diff >= Utils.MIN_MILLIS) {
            m = (int) (diff / Utils.MIN_MILLIS);
            diff -= m * Utils.MIN_MILLIS;

        }
        if (diff >= Utils.SEC_MILLIS) {
            s = (int) (diff / Utils.SEC_MILLIS);
        }
        hoursPicker.setValue(h);
        minutesPicker.setValue(m);
        secPicker.setValue(s);

        if (h == 0 && m == 0 && s == 0) {
            stop();
            notifyElapsedTime();
            return false;
        }
        return true;
    }

    private void notifyElapsedTime() {
        Toast.makeText(getContext(), R.string.mes_timer_elapsed, Toast.LENGTH_SHORT).show();
    }
}
