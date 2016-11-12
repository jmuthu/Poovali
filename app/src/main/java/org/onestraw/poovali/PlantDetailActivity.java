package org.onestraw.poovali;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class PlantDetailActivity extends AppCompatActivity {

    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);

        String itemId = getIntent().getStringExtra(PlantContent.ARG_ITEM_ID);
        PlantContent.Plant plant = PlantContent.ITEM_MAP.get(itemId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(plant.name);
        }

        TextView cropDuration = (TextView) findViewById(R.id.crop_duration);
        cropDuration.setText(plant.cropDuration.toString() + " days");
        TextView soil = (TextView) findViewById(R.id.soil);
        soil.setText(plant.soil);
        TextView seedTreatment = (TextView) findViewById(R.id.seed_treatment);
        seedTreatment.setText(plant.seedTreatment);
        TextView sowingSeason = (TextView) findViewById(R.id.sowing_season);
        sowingSeason.setText(plant.sowingSeason);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, PlantListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendNotification(View view) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.notification);

        // Set the intent that will fire when the user taps the notification.
        //Intent resultIntent = new Intent(this, ResultActivity.class);
        //builder.setContentIntent(resultIntent);

        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.watering_can));

        builder.setContentTitle("Sow vegetables");
        builder.setContentText("Time to sow tomatoes! Only 10 days left for harvesting!");
        //builder.setSubText("");

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        // END_INCLUDE(send_notification)
    }
}
