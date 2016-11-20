package org.onestraw.poovali;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.onestraw.poovali.model.PlantContent;
import org.onestraw.poovali.utility.Helper;

public class PlantDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);

        String itemId = getIntent().getStringExtra(PlantContent.ARG_ITEM_ID);
        PlantContent.Plant plant = PlantContent.getItemMap().get(itemId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(plant.getName());
        }
        ImageView imageBar = (ImageView) findViewById(R.id.image_id);
        imageBar.setImageResource(getResources().getIdentifier(plant.getImageName() + Helper.DETAIL_IMAGE_SUFFIX,
                "drawable",
                getPackageName()));
        TextView cropDuration = (TextView) findViewById(R.id.crop_duration);
        String days = String.format(getResources().getString(R.string.days), plant.getCropDuration().toString());
        cropDuration.setText(days);
        TextView soil = (TextView) findViewById(R.id.soil);
        soil.setText(plant.getSoil());
        TextView seedTreatment = (TextView) findViewById(R.id.seed_treatment);
        seedTreatment.setText(plant.getSeedTreatment());
        TextView sowingSeason = (TextView) findViewById(R.id.sowing_season);
        sowingSeason.setText(plant.getSowingSeason());
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

}
