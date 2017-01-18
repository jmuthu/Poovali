package com.github.jmuthu.poovali.utility;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.interfaces.DisplayableItem;
import com.github.jmuthu.poovali.model.plant.Plant;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {
    public static final String DETAIL_IMAGE_SUFFIX = "_detail";
    public static final String ARG_IS_SOW_ACTIVITY = "IS_SOW_ACTIVITY";
    public static final String ARG_EVENT_ID = "EVENT_ID";
    public static final String ARG_PLANT_ID = "PLANT_ID";
    public static final String ARG_BATCH_ID = "BATCH_ID";
    public static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM);
    public static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);
    public static String[] batchEventNameList = null;

    public static void setImageSrc(ImageView imageView, DisplayableItem item) {
        File file = null;
        if (item.getImageUri() != null) {
            file = new File(item.getImageUri().getPath());
            if (file.exists()) {
                imageView.setImageURI(item.getImageUri());
            } else {
                file = null;
            }
        }
        if (file == null) {
            String name = getImageFileName(item.getTypeName());
            Context context = imageView.getContext();
            int resId = context.getResources().getIdentifier(name,
                    "drawable",
                    context.getPackageName());
            if (resId == 0) {
                resId = R.drawable.add_plant;
            }
            imageView.setImageResource(resId);
        }
    }

    public static String getImageFileName(String name) {
        return name.toLowerCase().replace(' ', '_').replaceAll("\\W", "");
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    public static void setOverDueText(Plant plant, TextView textView, int warnColor) {
        Integer overDue = plant.pendingSowDays();
        if (overDue != null) {
            if (overDue == 0) {
                textView.setText("sow today");
            } else if (overDue > 0) {
                String text = overDue == 1 ?
                        overDue + " day overdue" : overDue + " days overdue";
                textView.setText(text);
                textView.setTextColor(warnColor);
            } else {
                overDue = overDue * -1;
                String text = overDue == 1 ?
                        overDue + " day" : overDue + " days";
                textView.setText("sow in " + text);
            }
        }
    }

    public static void setBatchEventName(Context context) {
        batchEventNameList = context.getResources().getStringArray(R.array.batch_activity_type);
    }

    public static String getBatchEventName(int id) {
        return batchEventNameList[id];
    }

    public static void alertSaveFailure(Context context, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,
                R.style.AlertDialogTheme);
        builder.setMessage(messageId);
        builder.setTitle(R.string.save_failed);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

}
