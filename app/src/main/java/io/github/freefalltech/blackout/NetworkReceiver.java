package io.github.freefalltech.blackout;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by siddh on 07-04-2017.
 */
public class NetworkReceiver extends BroadcastReceiver {

    private static final String tag = NetworkReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(tag, "****** NetworkReceiver // onReceive()");


        boolean noConnection = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if (noConnection) {
            final Intent startMainActivity =  new Intent(context, MainActivity.class);

            String notifyMsg = "You seem to have lost your connectivity. If you are in a disaster, you can enable blackout by clicking this notification",
            notifyTitle = "Connectivity lost";
            final PendingIntent startBlackout = PendingIntent.getActivity(context, 0,startMainActivity, 0);
            final NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.bolt_image)
                            .setContentTitle(notifyTitle)
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(startBlackout)
                            .setAutoCancel(true)
                            .setContentText(notifyMsg);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle(notifyTitle);
                bigTextStyle.bigText(notifyMsg);
                mBuilder.setStyle(bigTextStyle);
            }

            final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(startBlackout!=null){
                        startMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(startMainActivity);
                        mNotificationManager.cancelAll();
                    }
                }
            }, 20000);
        }
    }
}
