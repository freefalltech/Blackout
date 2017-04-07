package io.github.freefalltech.blackout;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OpenerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opener);
        TextView subText = (TextView) findViewById(R.id.subTextId);
        TextView titleHeader = (TextView) findViewById(R.id.heroText);
        titleHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/helvetica.otf"));
        subText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/centurygothic.ttf"));

        Intent serviceIntent = new Intent(this, NetworkListenerService.class);
        startService(serviceIntent);
    }

    public void startBlackout(View view) {
        startActivity(new Intent(OpenerActivity.this,MainActivity.class));
    }
}
