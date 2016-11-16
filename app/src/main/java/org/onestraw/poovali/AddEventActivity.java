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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner eventSpinner = (Spinner) findViewById(R.id.event_type_spinner);

        SpinnerAdapter eventSpinnerAdapter = new CustomSpinnerAdapter<EventContent.EventType>(this,
                new ArrayList<EventContent.EventType>(Arrays.asList(EventContent.EventType.values())));
        eventSpinner.setAdapter(eventSpinnerAdapter);
        //mySpinner.setSelection(EventContent.DEFAULT_EVENT_TYPE_POSITION);

        Spinner plantSpinner = (Spinner) findViewById(R.id.plant_type_spinner);

        SpinnerAdapter plantSpinnerAdapter = new CustomSpinnerAdapter<PlantContent.Plant>(this,
                new ArrayList<PlantContent.Plant>(PlantContent.ITEMS));
        plantSpinner.setAdapter(plantSpinnerAdapter);

        Date currDate = Calendar.getInstance().getTime();

        TextView dateView = (TextView) findViewById(R.id.date);
        dateView.setText(DatePickerFragment.DATE_FORMAT.format(currDate));

        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(TimePickerFragment.TIME_FORMAT.format(currDate));
    }

    public void saveEvent(View v) {
        EventContent.Event event = new EventContent.Event();

        TextView dateView = (TextView) findViewById(R.id.date);
        TextView timeView = (TextView) findViewById(R.id.time);
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        try {
            Date date = df.parse(dateView.getText().toString() + " " + timeView.getText().toString());
            event.setCreatedDate(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Spinner spinner = (Spinner) findViewById(R.id.event_type_spinner);
        event.setType((EventContent.EventType) spinner.getSelectedItem());

        EditText desc = (EditText) findViewById(R.id.event_description);
        event.setDescription(desc.getText().toString());

        EventContent.addEvent(this, event);
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
        public static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM);

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            TextView dateView = (TextView) getActivity().findViewById(R.id.date);
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(DATE_FORMAT.parse(dateView.getText().toString()));
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

            dateView.setText(DATE_FORMAT.format(calendar.getTime()));
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        public static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.MEDIUM);

        @Override
        @NonNull
        public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
            TextView timeView = (TextView) getActivity().findViewById(R.id.time);
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(TIME_FORMAT.parse(timeView.getText().toString()));

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
            dateView.setText(TIME_FORMAT.format(calendar.getTime()));
        }
    }

    public class CustomSpinnerAdapter<T> extends ArrayAdapter<T> {

        CustomSpinnerAdapter(Activity context, ArrayList<T> list) {
            super(context, 0, list);
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            T item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
            }
            if (item!=null) {
                ImageView imageView = (ImageView) convertView.findViewById(R.id.img);
                imageView.setImageResource(getResources().getIdentifier(Helper.getImageFileName(item.toString()),
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
}
