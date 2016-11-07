package clock.for_ziv;

public class Utils {

    public static final int SEC_MILLIS = 1000;
    public static final int MIN_MILLIS = 60 * SEC_MILLIS;
    public static final int HOUR_MILLIS = 60 * MIN_MILLIS;

    public static String formatTimeString(long valueMillis) {
        final StringBuilder text = new StringBuilder();
        long h = 0;
        long m = 0;
        long s = 0;
        if (valueMillis >= HOUR_MILLIS) {
            h = valueMillis / HOUR_MILLIS;
            valueMillis -= h * HOUR_MILLIS;
        }
        if (valueMillis >= MIN_MILLIS) {
            m = valueMillis / MIN_MILLIS;
            valueMillis -= m * MIN_MILLIS;

        }
        if (valueMillis >= SEC_MILLIS) {
            s = valueMillis / SEC_MILLIS;
            valueMillis -= s * SEC_MILLIS;

        }
        text.append(String.format("%02d", h)).append(":")
                .append(String.format("%02d", m)).append(":")
                .append(String.format("%02d", s)).append(".")
                .append(String.format("%03d", valueMillis));
        return text.toString();
    }

}
