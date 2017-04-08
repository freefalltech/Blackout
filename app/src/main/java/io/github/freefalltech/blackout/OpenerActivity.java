package io.github.freefalltech.blackout;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class OpenerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opener);
        TextView subText = (TextView) findViewById(R.id.subTextId);
        TextView titleHeader = (TextView) findViewById(R.id.heroText);
        titleHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/helvetica.otf"));
        subText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/centurygothic.ttf"));
        if (!isMyServiceRunning(NetworkListenerService.class)) {
            Log.v("OpenerActivity","Service aint running already, starting it now");
            Intent serviceIntent = new Intent(this, NetworkListenerService.class);
            startService(serviceIntent);
        }
    }

    public void startBlackout(View view) {
        startActivity(new Intent(OpenerActivity.this, MainActivity.class));
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
