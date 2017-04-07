package io.github.freefalltech.blackout;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView searchWifiText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView subText = (TextView) findViewById(R.id.saveDataText);
        TextView titleHeader = (TextView) findViewById(R.id.heroTextMain);
        searchWifiText = (TextView) findViewById(R.id.wifiText);
        searchWifiText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/helvetica.otf"));
        titleHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/helvetica.otf"));
        subText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/centurygothic.ttf"));
        dimBrighntess();
    }

    public void searchWifi(View v){
        searchWifiText = (TextView) findViewById(R.id.wifiText);
        searchWifiText.setVisibility(View.GONE);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rLayout);
        rl.setVisibility(View.VISIBLE);
    }

    private void dimBrighntess() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&!Settings.System.canWrite(this)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M||Settings.System.canWrite(this)) {
            Settings.System.putInt(this.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, 20);

            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness =0.2f;// 100 / 100.0f;
            getWindow().setAttributes(lp);

        }
    }

    public void turnNetworkOff(View view) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings",
                "com.android.settings.Settings$DataUsageSummaryActivity");
        startActivity(intent);
    }
}
