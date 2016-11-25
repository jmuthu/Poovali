package org.onestraw.poovali;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import org.onestraw.poovali.model.BatchContent;
import org.onestraw.poovali.model.EventContent;
import org.onestraw.poovali.model.PlantContent;
import org.onestraw.poovali.utility.Helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity {

    EventContent.Event event = null;
    boolean isSowActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int plantId = -1;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isSowActivity = extras.getBoolean(Helper.ARG_IS_SOW_ACTIVITY);
            int eventId = extras.getInt(Helper.ARG_EVENT_ID, -1);
            plantId = extras.getInt(Helper.ARG_PLANT_ID, -1);
            if (eventId > -1) {
                event = EventContent.getItems(this).get(eventId);
            }
        }

        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner eventSpinner = (Spinner) findViewById(R.id.event_type_spinner);

        String label;

        SpinnerAdapter plantSpinnerAdapter;

        if (isSowActivity) {
            findViewById(R.id.event_type_label).setVisibility(View.GONE);
            eventSpinner.setVisibility(View.GONE);

            label = getResources().getString(R.string.plant_label);
            plantSpinnerAdapter = new CustomSpinnerAdapter<PlantContent.Plant>(this,
                    (ArrayList<PlantContent.Plant>) PlantContent.getItems());
        } else {

            ArrayList<EventContent.BatchActivityType> batchActivityList =
                    new ArrayList<EventContent.BatchActivityType>(Arrays.asList(EventContent.BatchActivityType.values()));

            SpinnerAdapter eventSpinnerAdapter = new EventTypeSpinnerAdapter(this, batchActivityList);
            eventSpinner.setAdapter(eventSpinnerAdapter);

            label = getResources().getString(R.string.batch_label);
            plantSpinnerAdapter = new CustomSpinnerAdapter<BatchContent.Batch>(this,
                    (ArrayList<BatchContent.Batch>) BatchContent.getItems(this));

        }

        Spinner plantSpinner = (Spinner) findViewById(R.id.plant_type_spinner);
        plantSpinner.setAdapter(plantSpinnerAdapter);

        TextView plant_label = (TextView) findViewById(R.id.plant_label);
        plant_label.setText(label);

        Date date;
        String description = null;
        if (event != null) {
            date = event.getCreatedDate();
            eventSpinner.setSelection(((EventContent.BatchActivityEvent) event).getType().ordinal());
            plantSpinner.setSelection(BatchContent.getItems(this).indexOf(event.getBatch(this)));
            description = event.getDescription();
        } else {
            date = Calendar.getInstance().getTime();
        }

        if (plantId > -1) {
            plantSpinner.setSelection(plantId);
        }

        TextView dateView = (TextView) findViewById(R.id.date);
        dateView.setText(Helper.DATE_FORMAT.format(date));

        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(Helper.TIME_FORMAT.format(date));

        TextView eventDescription = (TextView) findViewById(R.id.event_description);
        eventDescription.setText(description);
    }

    public void saveEvent(View v) {
        boolean isNewEvent = false;
        if (event == null) {
            if (isSowActivity) {
                event = new EventContent.SowBatchEvent();
            } else {
                event = new EventContent.BatchActivityEvent();
            }
            event.setId(UUID.randomUUID().toString());
            isNewEvent = true;
        }
        TextView dateView = (TextView) findViewById(R.id.date);
        TextView timeView = (TextView) findViewById(R.id.time);
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        try {
            Date date = df.parse(dateView.getText().toString() + " " + timeView.getText().toString());
            event.setCreatedDate(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isSowActivity) {
            Spinner spinner = (Spinner) findViewById(R.id.event_type_spinner);
            EventContent.BatchActivityType batchActivityType = (EventContent.BatchActivityType) spinner.getSelectedItem();
            ((EventContent.BatchActivityEvent) event).setType(batchActivityType);
        }

        Spinner spinner = (Spinner) findViewById(R.id.plant_type_spinner);
        if (isSowActivity) {
            BatchContent.Batch batch = new BatchContent.Batch();
            batch.setId(UUID.randomUUID().toString());
            batch.setCreatedDate(event.getCreatedDate());
            batch.setPlant((PlantContent.Plant) spinner.getSelectedItem());
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yy");
            String date = format.format(batch.getCreatedDate());
            batch.setName(batch.getPlant().getName() +
                    " - " + date);
            BatchContent.addBatch(this, batch);
            event.setBatch(batch);
        } else {
            event.setBatch((BatchContent.Batch) spinner.getSelectedItem());
        }

        EditText desc = (EditText) findViewById(R.id.event_description);
        event.setDescription(desc.getText().toString());

        if (isNewEvent) {
            EventContent.addEvent(this, event);
        } else {
            EventContent.saveItems(this);
        }
        finish();
    }

    public void cancelAddEvent(View v) {
        finish();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            TextView dateView = (TextView) getActivity().findViewById(R.id.date);
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(Helper.DATE_FORMAT.parse(dateView.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            TextView dateView = (TextView) getActivity().findViewById(R.id.date);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            dateView.setText(Helper.DATE_FORMAT.format(calendar.getTime()));
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        @NonNull
        public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
            TextView timeView = (TextView) getActivity().findViewById(R.id.time);
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(Helper.TIME_FORMAT.parse(timeView.getText().toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    android.text.format.DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            TextView dateView = (TextView) getActivity().findViewById(R.id.time);
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            dateView.setText(Helper.TIME_FORMAT.format(calendar.getTime()));
        }
    }

    public class EventTypeSpinnerAdapter extends ArrayAdapter<EventContent.BatchActivityType> {

        EventTypeSpinnerAdapter(Activity context, ArrayList<EventContent.BatchActivityType> list) {
            super(context, 0, list);
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            EventContent.BatchActivityType item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
            }
            if (item != null) {
                ImageView imageView = (ImageView) convertView.findViewById(R.id.img);
                imageView.setImageResource(getResources().getIdentifier(
                        Helper.getImageFileName(item.toString()),
                        "drawable",
                        imageView.getContext().getPackageName()));

                TextView textView = (TextView) convertView.findViewById(R.id.txt);
                textView.setText(item.toString());
            }
            return convertView;
        }

        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);

        }
    }

    public class CustomSpinnerAdapter<T extends Helper.DisplayableItem> extends ArrayAdapter<T> {

        CustomSpinnerAdapter(Activity context, ArrayList<T> list) {
            super(context, 0, list);
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            T item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
            }
            if (item != null) {
                ImageView imageView = (ImageView) convertView.findViewById(R.id.img);
                imageView.setImageResource(getResources().getIdentifier(
                        item.getImageName(),
                        "drawable",
                        imageView.getContext().getPackageName()));

                TextView textView = (TextView) convertView.findViewById(R.id.txt);
                textView.setText(item.getName());
            }
            return convertView;
        }

        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);

        }
    }
}
