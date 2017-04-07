package io.github.freefalltech.blackout;

/**
 * Created by siddh on 07-04-2017.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NetworkListenerService.class);
        context.startService(serviceIntent);
    }
}
