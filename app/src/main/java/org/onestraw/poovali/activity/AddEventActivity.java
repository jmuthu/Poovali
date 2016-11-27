package org.onestraw.poovali.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import org.onestraw.poovali.R;
import org.onestraw.poovali.model.BatchContent;
import org.onestraw.poovali.model.EventContent;
import org.onestraw.poovali.model.PlantContent;
import org.onestraw.poovali.utility.Helper;
import org.onestraw.poovali.utility.MyExceptionHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity {

    static boolean isSowActivity = true;
    EventContent.Event event = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));

        int plantId = -1;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isSowActivity = extras.getBoolean(Helper.ARG_IS_SOW_ACTIVITY);
            int batchId = extras.getInt(Helper.ARG_BATCH_ID, -1);
            int eventId = extras.getInt(Helper.ARG_EVENT_ID, -1);
            plantId = extras.getInt(Helper.ARG_PLANT_ID, -1);
            if (eventId > -1) {
                event = BatchContent.getItems().get(batchId).getEvents().get(eventId);
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
                    (ArrayList<BatchContent.Batch>) BatchContent.getItems());
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
            plantSpinner.setSelection(BatchContent.getItems().indexOf(event.getBatch()));
            plantSpinner.setEnabled(false);
            description = event.getDescription();
        } else {
            date = Calendar.getInstance().getTime();
        }

        if (plantId > -1) {
            plantSpinner.setSelection(plantId);
            plantSpinner.setEnabled(false);
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
        TextView dateView = (TextView) findViewById(R.id.date);
        TextView timeView = (TextView) findViewById(R.id.time);
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        String dateString = dateView.getText().toString() + " " + timeView.getText().toString();
        try {
            Date date = df.parse(dateString);
            if (!validateDate(date)) {
                return;
            }
            if (event == null) {
                if (isSowActivity) {
                    event = new EventContent.SowBatchEvent();
                } else {
                    event = new EventContent.BatchActivityEvent();
                }
                event.setId(UUID.randomUUID().toString());
                isNewEvent = true;
            }
            event.setCreatedDate(date);
        } catch (ParseException e) {
            Log.e(this.getClass().getName(), "Unable to parse date : " + dateString, e);
            AlertDialog.Builder builder = new AlertDialog.Builder(this,
                    android.R.style.Theme_Material_Dialog_Alert);
            builder.setMessage("Invalid date, try again!");
            builder.setTitle("Save failed");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
            return;
        }
        if (!isSowActivity) {
            Spinner spinner = (Spinner) findViewById(R.id.event_type_spinner);
            EventContent.BatchActivityType batchActivityType = (EventContent.BatchActivityType) spinner.getSelectedItem();
            ((EventContent.BatchActivityEvent) event).setType(batchActivityType);
        }

        Spinner spinner = (Spinner) findViewById(R.id.plant_type_spinner);
        int batchPos = 0;
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
            batchPos = spinner.getSelectedItemPosition();
        }

        EditText desc = (EditText) findViewById(R.id.event_description);
        event.setDescription(desc.getText().toString());

        if (isNewEvent) {
            BatchContent.getItems().get(batchPos).addEvent(this, event);
        } else {
            BatchContent.saveItems(this);
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

    public boolean validateDate(Date date) {
        if (isSowActivity) {
            Spinner spinner = (Spinner) findViewById(R.id.plant_type_spinner);
            PlantContent.Plant plant = (PlantContent.Plant) spinner.getSelectedItem();

            if (BatchContent.isDuplicateBatch(plant, date)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_Alert);
                builder.setMessage("Batch already exists for the given date, select another date!");
                builder.setTitle("Save failed");
                builder.setPositiveButton(android.R.string.ok, null);

                builder.show();
                return false;
            }
        }
        return true;
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
            } catch (ParseException e) {
                Log.e(this.getClass().getName(), "Unable to parse date : " + dateView.getText(), e);
                return new DatePickerDialog(getActivity(), this, 2016, 1, 1);
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            if (!isSowActivity) {
                Spinner spinner = (Spinner) getActivity().findViewById(R.id.plant_type_spinner);
                BatchContent.Batch batch = (BatchContent.Batch) spinner.getSelectedItem();
                if (batch.getCreatedDate() != null) {
                    dialog.getDatePicker().setMinDate(batch.getCreatedDate().getTime());
                }
            }
            return dialog;
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
            } catch (ParseException e) {
                Log.e(this.getClass().getName(), "Unable to parse time : " + timeView.getText(), e);
                return new TimePickerDialog(getActivity(), this, 0, 0, false);
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
