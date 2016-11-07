package clock.for_ziv;

public interface ClockFragment {

    String PREF_FILE = "ClockPrefs";

    void set();

    void start();

    void stop();

    boolean hasSetFuncEnabled();
}
