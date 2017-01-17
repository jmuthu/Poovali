package com.github.jmuthu.poovali.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.model.event.BatchActivityEvent;
import com.github.jmuthu.poovali.model.event.BatchEventFactory;
import com.github.jmuthu.poovali.model.event.Event;
import com.github.jmuthu.poovali.model.event.EventRepository;
import com.github.jmuthu.poovali.model.plant.PlantBatch;
import com.github.jmuthu.poovali.model.plant.PlantBatchRepository;
import com.github.jmuthu.poovali.utility.DatePickerFragment;
import com.github.jmuthu.poovali.utility.Helper;
import com.github.jmuthu.poovali.utility.MyExceptionHandler;
import com.github.jmuthu.poovali.utility.TimePickerFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity {

    Event mEvent = null;
    PlantBatch mPlantBatch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));

        int batchId = -1;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            batchId = extras.getInt(Helper.ARG_BATCH_ID, -1);
            int eventId = extras.getInt(Helper.ARG_EVENT_ID, -1);
            if (eventId != -1) {
                mEvent = EventRepository.find(eventId);
                mPlantBatch = PlantBatchRepository.find(mEvent.getBatchId());
            } else {
                mPlantBatch = PlantBatchRepository.find(batchId);
            }
        }

        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner eventSpinner = (Spinner) findViewById(R.id.event_type_spinner);
        BatchActivityEvent.Type[] batchActivityList =
                BatchActivityEvent.Type.values();
        SpinnerAdapter eventSpinnerAdapter =
                new EventTypeSpinnerAdapter(this, batchActivityList);
        eventSpinner.setAdapter(eventSpinnerAdapter);

        TextView event_label = (TextView) findViewById(R.id.event_type_label);
        String text = getString(R.string.event_type_label) + " " + mPlantBatch.getName();
        event_label.setText(text);

        Date date = Calendar.getInstance().getTime();

        if (mEvent != null) {
            date = mEvent.getCreatedDate();
            eventSpinner.setSelection(
                    ((BatchActivityEvent) mEvent).getType().ordinal());
            eventSpinner.setEnabled(false);

            TextView eventDescription = (TextView) findViewById(R.id.event_description);
            eventDescription.setText(mEvent.getDescription());
        }

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
        Spinner eventTypeSpinner = (Spinner) findViewById(R.id.event_type_spinner);
        if (mEvent == null) {
            mEvent = BatchEventFactory.createEvent((BatchActivityEvent.Type) eventTypeSpinner.getSelectedItem());
        }

        Event event = mPlantBatch.findEvent(mEvent.getName(), date);
        if (event != null && !event.sameIdentityAs(mEvent)) {
            Helper.alertSaveFailure(this, R.string.duplicate_event);
            return;
        }

        EditText desc = (EditText) findViewById(R.id.event_description);
        mEvent.setCreatedDate(date);
        mEvent.setDescription(desc.getText().toString().trim());

        mPlantBatch.addOrUpdateEvent(mEvent);
        EventRepository.store(mEvent);
        finish();
    }

    public void cancelAddEvent(View v) {
        finish();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setDateView((TextView) findViewById(R.id.date));
        datePickerFragment.setMinDate(mPlantBatch.getCreatedDate());
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setTimeView((TextView) findViewById(R.id.time));
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public class EventTypeSpinnerAdapter extends ArrayAdapter<BatchActivityEvent.Type> {

        EventTypeSpinnerAdapter(Activity context, BatchActivityEvent.Type[] list) {
            super(context, 0, list);
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            String typeName = Helper.getBatchEventName(getItem(position).getValue());
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.spinner_item, parent, false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.img);
            imageView.setImageResource(getResources().getIdentifier(
                    Helper.getImageFileName(typeName),
                    "drawable",
                    imageView.getContext().getPackageName()));

            TextView textView = (TextView) convertView.findViewById(R.id.txt);
            textView.setText(typeName);

            return convertView;
        }

        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);

        }
    }
}
