package com.github.jmuthu.poovali.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.model.plant.Plant;
import com.github.jmuthu.poovali.model.plant.PlantBatch;
import com.github.jmuthu.poovali.model.plant.PlantBatchRepository;
import com.github.jmuthu.poovali.model.plant.PlantRepository;
import com.github.jmuthu.poovali.utility.CustomSpinnerAdapter;
import com.github.jmuthu.poovali.utility.DatePickerFragment;
import com.github.jmuthu.poovali.utility.Helper;
import com.github.jmuthu.poovali.utility.MyExceptionHandler;
import com.github.jmuthu.poovali.utility.TimePickerFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.github.jmuthu.poovali.R.id.date;

public class AddPlantBatchActivity extends AppCompatActivity {

    PlantBatch mPlantBatch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));

        String plantId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String plantBatchId = extras.getString(Helper.ARG_BATCH_ID);
            plantId = extras.getString(Helper.ARG_PLANT_ID);
            if (plantBatchId != null) {
                mPlantBatch = PlantBatchRepository.find(plantBatchId);
            }
        }

        setContentView(R.layout.activity_add_plant_batch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String label;
        Spinner plantSpinner = (Spinner) findViewById(R.id.plant_type_spinner);

        Date date = Calendar.getInstance().getTime();

        TextView plant_label = (TextView) findViewById(R.id.plant_label);
        if (mPlantBatch != null) {
            plant_label.setTextAppearance(this, R.style.Label);
            label = mPlantBatch.getName();
            date = mPlantBatch.getCreatedDate();
            plantSpinner.setVisibility(View.GONE);
            TextView descriptionView = (TextView) findViewById(R.id.description);
            descriptionView.setText(mPlantBatch.getDescription());

        } else {
            label = getResources().getString(R.string.plant_label);
            SpinnerAdapter plantSpinnerAdapter = new CustomSpinnerAdapter<Plant>(this,
                    PlantRepository.findAll());
            plantSpinner.setAdapter(plantSpinnerAdapter);
            if (plantId != null) {
                plantSpinner.setSelection(PlantRepository.findAll()
                        .indexOf(PlantRepository.find(plantId)));
                plantSpinner.setEnabled(false);
            }
        }


        plant_label.setText(label);

        TextView dateView = (TextView) findViewById(R.id.date);
        dateView.setText(Helper.DATE_FORMAT.format(date));

        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(Helper.TIME_FORMAT.format(date));
    }

    public void saveEvent(View v) {
        TextView dateView = (TextView) findViewById(R.id.date);
        TextView timeView = (TextView) findViewById(R.id.time);
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        String dateString = dateView.getText().toString() + " " + timeView.getText().toString();
        Date date;
        try {
            date = df.parse(dateString);
        } catch (ParseException e) {
            Log.e(this.getClass().getName(), "Unable to parse date : " + dateString, e);
            Helper.alertSaveFailure(this, R.string.invalid_date);
            return;
        }
        Plant plant;
        if (mPlantBatch == null) {
            Spinner spinner = (Spinner) findViewById(R.id.plant_type_spinner);
            plant = (Plant) spinner.getSelectedItem();
        } else {
            plant = mPlantBatch.getPlant();
        }

        PlantBatch plantBatch = plant.findBatch(date);
        if (plantBatch != null && !plantBatch.sameIdentityAs(mPlantBatch)) {
            Helper.alertSaveFailure(this, R.string.duplicate_batch);
            return;
        }

        if (mPlantBatch == null) {
            mPlantBatch = new PlantBatch();
            mPlantBatch.setId(UUID.randomUUID().toString());
        }
        SimpleDateFormat format = new SimpleDateFormat("d MMM yy");
        mPlantBatch.setName(plant.getName() + " - " +
                format.format(date));
        EditText desc = (EditText) findViewById(R.id.description);
        mPlantBatch.setDescription(desc.getText().toString().trim());
        mPlantBatch.setCreatedDate(date);

        plant.addOrUpdatePlantBatch(mPlantBatch);
        PlantBatchRepository.store(mPlantBatch);
        finish();
    }

    public void cancelAddEvent(View v) {
        finish();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setDateView((TextView) findViewById(date));
        if (mPlantBatch != null && !mPlantBatch.getEvents().isEmpty()) {
            int size = mPlantBatch.getEvents().size();
            datePickerFragment.setMaxDate(mPlantBatch.getEvents().get(size - 1).getCreatedDate());
        }
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setTimeView((TextView) findViewById(R.id.time));
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

}
