package clock.for_ziv.timer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import clock.for_ziv.MainActivity;
import clock.for_ziv.R;


public class AlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 1001;
    private static final int NOTIF_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        Bundle args = new Bundle();
        args.putInt(MainActivity.KEY_CURRENT_MODE, MainActivity.MODE_TIMER);
        PendingIntent activity = PendingIntent.getActivity(context, REQUEST_CODE, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT, args);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("Hey Dude")
                .setContentText("Timer elapsed")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(activity)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat.from(context).notify(NOTIF_ID, notification);
    }
}
