package org.onestraw.poovali;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner mySpinner = (Spinner) findViewById(R.id.event_type_spinner);
        mySpinner.setAdapter(new ArrayAdapter<GardenEvent.EventType>(this, android.R.layout.simple_spinner_item, GardenEvent.EventType.values()));

        Date currDate = Calendar.getInstance().getTime();

        TextView dateView = (TextView) findViewById(R.id.date);
        dateView.setText(DatePickerFragment.DATE_FORMAT.format(currDate));

        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(DatePickerFragment.TIME_FORMAT.format(currDate));
    }

    public void saveEvent(View v) {
        File file = new File(getFilesDir(), EventsFragment.EVENTS_FILE);
        try {
            if (!file.isFile()) {
                file.createNewFile();
            }
            GardenEvent obj = new GardenEvent();

            TextView dateView = (TextView) findViewById(R.id.date);
            TextView timeView = (TextView) findViewById(R.id.time);
            SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy h:mm a");
            Date date = format.parse(dateView.getText().toString() + " " + timeView.getText().toString());
            obj.setCreatedDate(date);

            Spinner spinner = (Spinner) findViewById(R.id.event_type_spinner);
            obj.setType((GardenEvent.EventType) spinner.getSelectedItem());

            EditText desc = (EditText) findViewById(R.id.event_description);
            obj.setDescription(desc.getText().toString());

            EventsFragment.ITEMS.add(obj);
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(obj);
            oos.close();
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelAddEvent(View v) {
        finish();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, MMM dd, yyyy");
        public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a");

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
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

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            TextView dateView = (TextView) getActivity().findViewById(R.id.time);
            String dayPeriod = "AM";
            if (hourOfDay > 12) {
                dayPeriod = "PM";
                hourOfDay = hourOfDay - 12;
            }
            dateView.setText(hourOfDay + ":" + String.format("%02d", minute) + " " + dayPeriod);
        }
    }

}
