package com.github.jmuthu.poovali.activity;

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

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.model.Batch;
import com.github.jmuthu.poovali.model.BatchRepository;
import com.github.jmuthu.poovali.model.Plant;
import com.github.jmuthu.poovali.model.PlantRepository;
import com.github.jmuthu.poovali.model.event.BatchActivityEvent;
import com.github.jmuthu.poovali.model.event.Event;
import com.github.jmuthu.poovali.model.event.EventFactory;
import com.github.jmuthu.poovali.model.event.EventRepository;
import com.github.jmuthu.poovali.utility.Helper;
import com.github.jmuthu.poovali.utility.MyExceptionHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity {

    static boolean isSowActivity = true;
    Event mEvent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));

        String plantId = null;
        String batchId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isSowActivity = extras.getBoolean(Helper.ARG_IS_SOW_ACTIVITY);
            batchId = extras.getString(Helper.ARG_BATCH_ID);
            String eventId = extras.getString(Helper.ARG_EVENT_ID);
            plantId = extras.getString(Helper.ARG_PLANT_ID);
            if (eventId != null) {
                mEvent = EventRepository.find(eventId);
            }
        }

        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner eventSpinner = (Spinner) findViewById(R.id.event_type_spinner);

        String label;
        SpinnerAdapter plantSpinnerAdapter;
        Spinner plantSpinner = (Spinner) findViewById(R.id.plant_type_spinner);

        Date date = Calendar.getInstance().getTime();
        String description = null;

        if (isSowActivity) {
            findViewById(R.id.event_type_label).setVisibility(View.GONE);
            eventSpinner.setVisibility(View.GONE);

            label = getResources().getString(R.string.plant_label);
            plantSpinnerAdapter = new CustomSpinnerAdapter<Plant>(this,
                    PlantRepository.findAll());
            plantSpinner.setAdapter(plantSpinnerAdapter);
            if (plantId != null) {
                plantSpinner.setSelection(PlantRepository.findAll()
                        .indexOf(PlantRepository.find(plantId)));
                plantSpinner.setEnabled(false);
            }
        } else {
            BatchActivityEvent.Type[] batchActivityList =
                    BatchActivityEvent.Type.values();

            SpinnerAdapter eventSpinnerAdapter =
                    new EventTypeSpinnerAdapter(this, batchActivityList);
            eventSpinner.setAdapter(eventSpinnerAdapter);

            label = getResources().getString(R.string.batch_label);
            List<Batch> batchList;
            if (plantId != null) {
                batchList = PlantRepository.find(plantId).getBatchList();
            } else {
                batchList = BatchRepository.findAll();
            }
            plantSpinnerAdapter = new CustomSpinnerAdapter<Batch>(this, batchList);
            plantSpinner.setAdapter(plantSpinnerAdapter);
            if (batchId != null) {
                plantSpinner.setSelection(batchList.indexOf(BatchRepository.find(batchId)));
                plantSpinner.setEnabled(false);
            }
            if (mEvent != null) {
                date = mEvent.getCreatedDate();
                eventSpinner.setSelection(
                        ((BatchActivityEvent) mEvent).getType().ordinal());
                eventSpinner.setEnabled(false);
                description = mEvent.getDescription();
            }
        }

        TextView plant_label = (TextView) findViewById(R.id.plant_label);
        plant_label.setText(label);

        TextView dateView = (TextView) findViewById(R.id.date);
        dateView.setText(Helper.DATE_FORMAT.format(date));

        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(Helper.TIME_FORMAT.format(date));

        TextView eventDescription = (TextView) findViewById(R.id.event_description);
        eventDescription.setText(description);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this,
                    android.R.style.Theme_Material_Dialog_Alert);
            builder.setMessage("Invalid date, try again!");
            builder.setTitle("Save failed");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
            return;
        }
        if (!validateDate(date)) {
            return;
        }
        Spinner spinner = (Spinner) findViewById(R.id.plant_type_spinner);
        Spinner eventTypeSpinner = (Spinner) findViewById(R.id.event_type_spinner);
        EditText desc = (EditText) findViewById(R.id.event_description);

        if (mEvent == null) {
            mEvent = EventFactory.createEvent(isSowActivity);
        }
        mEvent.setCreatedDate(date);
        mEvent.setDescription(desc.getText().toString());

        Batch batch;
        if (isSowActivity) {
            batch = new Batch();
            batch.setId(UUID.randomUUID().toString());
            batch.setDescription(desc.getText().toString());
            batch.setCreatedDate(date);
            Plant plant = (Plant) spinner.getSelectedItem();
            batch.setPlant(plant);
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yy");
            batch.setName(batch.getPlant().getName() + " - " +
                    format.format(batch.getCreatedDate()));
            plant.addBatch(this, batch);
        } else {
            batch = (Batch) spinner.getSelectedItem();
            ((BatchActivityEvent) mEvent).
                    setType((BatchActivityEvent.Type) eventTypeSpinner.getSelectedItem());
        }
        batch.addEvent(this, mEvent);
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
            Plant plant = (Plant) spinner.getSelectedItem();

            if (plant.isDuplicateBatch(date)) {
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
            Long now = calendar.getTimeInMillis();
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
                Batch batch = (Batch) spinner.getSelectedItem();
                if (batch.getCreatedDate() != null) {
                    dialog.getDatePicker().setMinDate(batch.getCreatedDate().getTime());
                }
            }
            dialog.getDatePicker().setMaxDate(now);
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

    public class EventTypeSpinnerAdapter extends ArrayAdapter<BatchActivityEvent.Type> {

        EventTypeSpinnerAdapter(Activity context, BatchActivityEvent.Type[] list) {
            super(context, 0, list);
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            BatchActivityEvent.Type item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.spinner_item, parent, false);
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

        CustomSpinnerAdapter(Activity context, List<T> list) {
            super(context, 0, list);
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            T item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.spinner_item, parent, false);
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
