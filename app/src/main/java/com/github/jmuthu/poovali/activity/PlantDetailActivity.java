package com.github.jmuthu.poovali.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.fragment.BatchFragment;
import com.github.jmuthu.poovali.model.PlantContent;
import com.github.jmuthu.poovali.utility.Helper;
import com.github.jmuthu.poovali.utility.MyExceptionHandler;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;


public class PlantDetailActivity extends AppCompatActivity {
    PlantContent.Plant mPlant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        setContentView(R.layout.activity_plant_detail);

        String plantId = getIntent().getStringExtra(Helper.ARG_PLANT_ID);
        mPlant = PlantContent.getPlant(plantId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitle(" " + mPlant.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(getResources().getIdentifier(
                mPlant.getImageName(),
                "drawable",
                getPackageName()));
       /* CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(plant.getName());
        }
        ImageView imageBar = (ImageView) findViewById(R.id.image_id);
        imageBar.setImageResource(getResources().getIdentifier(plant.getImageName() + Helper.DETAIL_IMAGE_SUFFIX,
              "drawable",
            getPackageName()));

        TextView soil = (TextView) findViewById(R.id.soil);
        soil.setText(plant.getSoil());
        TextView seedTreatment = (TextView) findViewById(R.id.seed_treatment);
        seedTreatment.setText(plant.getSeedTreatment());
        TextView sowingSeason = (TextView) findViewById(R.id.sowing_season);
        sowingSeason.setText(plant.getSowingSeason());
*/
        ImageView addImageView = (ImageView) findViewById(R.id.add_action);
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddEventActivity.class);
                intent.putExtra(Helper.ARG_IS_SOW_ACTIVITY, true);
                intent.putExtra(Helper.ARG_PLANT_ID, mPlant.getId());
                startActivity(intent);
            }
        });

        if (mPlant.getBatchList() != null) {
            TextView batchLabel = (TextView) findViewById(R.id.batch_label);
            batchLabel.setText(getString(R.string.batch_label) + " ("
                    + mPlant.getBatchList().size() + ")");
        }

        PieChart pieChart = (PieChart) findViewById(R.id.chart);
        List<PieEntry> entries = new ArrayList<>();
        EnumMap<PlantContent.GrowthStage, Integer> growthStages = mPlant.getGrowthStageMap();
        Iterator<PlantContent.GrowthStage> enumKeySet = growthStages.keySet().iterator();
        while (enumKeySet.hasNext()) {
            PlantContent.GrowthStage currentStage = enumKeySet.next();
            entries.add(new PieEntry(growthStages.get(currentStage), currentStage.toString()));
        }

        String days = String.format(getResources().getString(R.string.days), mPlant.getCropDuration().toString());
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark}, this);
        dataSet.setSliceSpace(3f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(16);
        data.setValueTypeface(Typeface.DEFAULT);

        pieChart.setData(data);
        pieChart.setHoleRadius(40);
        pieChart.setTransparentCircleRadius(45);

        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText(days);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT);
        pieChart.setCenterTextSize(16);

        pieChart.setExtraOffsets(0.f, 5.f, 0.f, 5.f);
        pieChart.setDrawEntryLabels(false);
        // pieChart.setEntryLabelTextSize(12);
        // pieChart.setEntryLabelTypeface(Typeface.DEFAULT);
        // pieChart.setEntryLabelColor(Color.DKGRAY);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setRotationEnabled(false);
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = pieChart.getLegend();
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setFormSize(10);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setDrawInside(true);
        l.setWordWrapEnabled(true);

        pieChart.invalidate();

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(Helper.ARG_PLANT_ID, plantId);
            BatchFragment fragment = new BatchFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.batch_list_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlant.getBatchList() != null) {
            TextView batchLabel = (TextView) findViewById(R.id.batch_label);
            batchLabel.setText(getString(R.string.batch_label) + " ("
                    + mPlant.getBatchList().size() + ")");
        }
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

    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }
}
