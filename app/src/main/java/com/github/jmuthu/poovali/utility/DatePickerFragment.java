package com.github.jmuthu.poovali.utility;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private TextView mDateView;
    private Date mMinDate;
    private Date mMaxDate;

    public void setDateView(TextView mDateView) {
        this.mDateView = mDateView;
    }

    public void setMinDate(Date mMinDate) {
        this.mMinDate = mMinDate;
    }

    public void setMaxDate(Date mMaxDate) {
        this.mMaxDate = mMaxDate;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        Date endOfToday = Helper.getEndOfDay(calendar.getTime());
        try {
            calendar.setTime(Helper.DATE_FORMAT.parse(mDateView.getText().toString()));
        } catch (ParseException e) {
            Log.e(this.getClass().getName(), "Unable to parse date : " + mDateView.getText(), e);
            return new DatePickerDialog(getActivity(), this, 2016, 1, 1);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        if (mMinDate != null) {
            dialog.getDatePicker().setMinDate(Helper.getStartOfDay(mMinDate).getTime());
        }
        if (mMaxDate != null) {
            dialog.getDatePicker().setMaxDate(Helper.getEndOfDay(mMaxDate).getTime());
        } else {
            dialog.getDatePicker().setMaxDate(endOfToday.getTime());
        }
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mDateView.setText(Helper.DATE_FORMAT.format(calendar.getTime()));
    }
}
