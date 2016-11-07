package clock.for_ziv;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import clock.for_ziv.gps.GpsFragment;
import clock.for_ziv.stopper.StopperFragment;
import clock.for_ziv.timer.TimerFragment;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_CURRENT_MODE = "CURRENT_MODE";

    public static final int MODE_STOPPER = 0;
    public static final int MODE_TIMER = 1;
    public static final int MODE_GPS = 2;

    @Mode
    private int currentMode = MODE_STOPPER;
    private View btnSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View btnMode = findViewById(R.id.btn_mode);
        btnSet = findViewById(R.id.btn_set);
        View btnStart = findViewById(R.id.btn_start);
        View btnStop = findViewById(R.id.btn_stop);

        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchNextMode();
            }
        });
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_CURRENT_MODE)) {
            //noinspection WrongConstant
            currentMode = savedInstanceState.getInt(KEY_CURRENT_MODE);
        } else if (getIntent() != null && getIntent().hasExtra(KEY_CURRENT_MODE)) {
            //noinspection WrongConstant
            currentMode = getIntent().getIntExtra(KEY_CURRENT_MODE, 0);
        }
        switchMode(currentMode);

    }

    private void switchNextMode() {
        currentMode++;
        if (currentMode > MODE_GPS) {
            currentMode = MODE_STOPPER;
        }
        switchMode(currentMode);
    }

    private void switchMode(@Mode int mode) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(getTagForMode(mode));
        if (fragment == null) {
            switch (mode) {
                case MODE_STOPPER:
                    fragment = StopperFragment.newInstance();
                    break;
                case MODE_TIMER:
                    fragment = TimerFragment.newInstance();
                    break;
                case MODE_GPS:
                    fragment = GpsFragment.newInstance();
                    break;
            }
        }
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.container, fragment, getTagForMode(mode))
                .commit();
        if (fragment instanceof ClockFragment)
            btnSet.setEnabled(((ClockFragment) fragment).hasSetFuncEnabled());
    }

    private void set() {
        String tag = getTagForMode(currentMode);
        ClockFragment fragmentByTag = (ClockFragment) getSupportFragmentManager().findFragmentByTag(tag);
        fragmentByTag.set();
    }

    private void start() {
        String tagForMode = getTagForMode(currentMode);
        ClockFragment fragmentByTag = (ClockFragment) getSupportFragmentManager().findFragmentByTag(tagForMode);
        fragmentByTag.start();
    }

    private void stop() {
        String tagForMode = getTagForMode(currentMode);
        ClockFragment fragmentByTag = (ClockFragment) getSupportFragmentManager().findFragmentByTag(tagForMode);
        fragmentByTag.stop();
    }

    private String getTagForMode(@Mode int mode) {
        switch (mode) {
            case MODE_STOPPER:
                return "MODE_STOPPER";
            case MODE_TIMER:
                return "MODE_TIMER";
            case MODE_GPS:
                return "MODE_GPS";
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_MODE, currentMode);
    }

    @IntDef({MODE_STOPPER,
            MODE_TIMER,
            MODE_GPS,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface Mode {
    }

}
