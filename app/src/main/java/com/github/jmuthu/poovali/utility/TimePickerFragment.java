package com.github.jmuthu.poovali.utility;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.jmuthu.poovali.R;

import java.text.ParseException;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private TextView mTimeView;

    public void setTimeView(TextView mDateView) {
        this.mTimeView = mDateView;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(Helper.TIME_FORMAT.parse(mTimeView.getText().toString()));
        } catch (ParseException e) {
            Log.e(this.getClass().getName(), "Unable to parse time : " + mTimeView.getText(), e);
            return new TimePickerDialog(getActivity(), this, 0, 0, false);
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), R.style.TimePickerDialogTheme, this, hour, minute,
                android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        mTimeView.setText(Helper.TIME_FORMAT.format(calendar.getTime()));
    }
}
