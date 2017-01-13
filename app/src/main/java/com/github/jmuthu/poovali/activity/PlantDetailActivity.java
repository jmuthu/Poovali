package com.github.jmuthu.poovali.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.fragment.BatchListFragment;
import com.github.jmuthu.poovali.model.plant.Plant;
import com.github.jmuthu.poovali.model.plant.PlantRepository;
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
    Plant mPlant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        setContentView(R.layout.activity_plant_detail);

        String plantId = getIntent().getStringExtra(Helper.ARG_PLANT_ID);
        mPlant = PlantRepository.find(plantId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView plantIcon = (ImageView) findViewById(R.id.plant_type_icon);
        Helper.setImageSrc(plantIcon, mPlant);

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mPlant.getName());
        }

        PieChart pieChart = (PieChart) findViewById(R.id.chart);
        List<PieEntry> entries = new ArrayList<>();
        EnumMap<Plant.GrowthStage, Integer> growthStages = mPlant.getGrowthStageMap();
        Iterator<Plant.GrowthStage> enumKeySet = growthStages.keySet().iterator();
        while (enumKeySet.hasNext()) {
            Plant.GrowthStage currentStage = enumKeySet.next();
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

        setupUpdatableViews();
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(Helper.ARG_PLANT_ID, plantId);
            BatchListFragment fragment = new BatchListFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.batch_list_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.add_plant).setVisible(false);
        if (mPlant.getPlantBatchList().size() == 0) {
            menu.findItem(R.id.add_event).setVisible(false);
            menu.findItem(R.id.delete).setVisible(true);
        } else {
            menu.findItem(R.id.add_event).setVisible(true);
            menu.findItem(R.id.delete).setVisible(false);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupUpdatableViews();
        invalidateOptionsMenu();
    }

    public void setupUpdatableViews() {
        TextView batchLabel = (TextView) findViewById(R.id.batch_label);
        if (mPlant.getPlantBatchList().size() > 0) {
            batchLabel.setVisibility(View.VISIBLE);
            batchLabel.setText(getString(R.string.batch_label) + " ("
                    + mPlant.getPlantBatchList().size() + ")");
        } else {
            batchLabel.setVisibility(View.GONE);
        }
        TextView nextBatchDueView = (TextView) findViewById(R.id.next_batch_due);
        Helper.setOverDueText(mPlant, nextBatchDueView, Color.YELLOW);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add_batch:
                intent = new Intent(this, AddEventActivity.class);
                intent.putExtra(Helper.ARG_IS_SOW_ACTIVITY, true);
                intent.putExtra(Helper.ARG_PLANT_ID, mPlant.getId());
                startActivity(intent);
                return true;
            case R.id.add_event:
                intent = new Intent(this, AddEventActivity.class);
                intent.putExtra(Helper.ARG_IS_SOW_ACTIVITY, false);
                intent.putExtra(Helper.ARG_PLANT_ID, mPlant.getId());
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.edit:
                intent = new Intent(this, AddPlantActivity.class);
                intent.putExtra(Helper.ARG_PLANT_ID, mPlant.getId());
                startActivity(intent);
                finish();
                return true;
            case R.id.delete:
                DeletePlantDialogFragment dialog = new DeletePlantDialogFragment();
                dialog.setPlant(mPlant);
                dialog.show(getSupportFragmentManager(), "DeleteEvent");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DeletePlantDialogFragment extends DialogFragment {
        Plant mPlantToDelete;

        public void setPlant(Plant plant) {
            mPlantToDelete = plant;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity(), R.style.AlertDialogTheme);
            builder.setTitle(R.string.delete_plant_alert);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    PlantRepository.delete(mPlantToDelete);
                    (getActivity()).finish();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
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
