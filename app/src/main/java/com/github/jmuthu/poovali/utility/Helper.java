package com.github.jmuthu.poovali.utility;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.interfaces.DisplayableItem;
import com.github.jmuthu.poovali.model.plant.Plant;

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

    public static void setImageSrc(ImageView imageView, DisplayableItem item) {
        if (item.getImageUri() != null) {
            imageView.setImageURI(item.getImageUri());
        } else {
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

    public static String getImageFileName(String name)
    {
        return name.toLowerCase().replace(' ', '_').replaceAll("\\W", "");
    }

    public static Date getZeroTimeDate(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

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
}
