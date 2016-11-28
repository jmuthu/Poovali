package com.github.jmuthu.poovali.utility;

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

    public static String getImageFileName(String name)
    {
        return name.toLowerCase().replace(' ', '_').replaceAll("\\W", "");
    }

    public interface DisplayableItem {
        String getId();
        String getName();
        String getImageName();
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
}
