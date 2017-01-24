package com.github.jmuthu.poovali.utility;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jmuthu.poovali.MyApplication;
import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.interfaces.DisplayableItem;
import com.github.jmuthu.poovali.model.plant.Plant;

import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Helper {
    public static final String DETAIL_IMAGE_SUFFIX = "_detail";
    public static final String ARG_IS_SOW_ACTIVITY = "IS_SOW_ACTIVITY";
    public static final String ARG_EVENT_ID = "EVENT_ID";
    public static final String ARG_PLANT_ID = "PLANT_ID";
    public static final String ARG_BATCH_ID = "BATCH_ID";
    public static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM);
    public static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);
    private static final Map<Integer, String[]> localizedMap = new HashMap<>();

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
            String fmt;
            if (overDue > 0) {
                textView.setTextColor(warnColor);
                fmt = textView.getContext().getResources().getText(R.string.sow_over_due).toString();
            } else {
                fmt = textView.getContext().getResources().getText(R.string.sow_next).toString();
                overDue *= -1;
            }
            textView.setText(MessageFormat.format(fmt, overDue));
        }
    }

    public static String getLocalizedString(int resId, int id) {
        if (!localizedMap.containsKey(resId)) {
            localizedMap.put(resId, MyApplication.getContext().getResources().getStringArray(resId));
        }
        return localizedMap.get(resId)[id];
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

    public static void alertAndCloseApp(String message) {
        //Intent intent = new Intent(myContext, AnotherActivity.class);
        //intent.putExtra("error", errorReport.toString());
        //myContext.startActivity(intent);
        if (message == null) {
            message = MyApplication.getContext().getString(R.string.fatal_error_message);
        }
       /* Toast toast = Toast.makeText(MyApplication.getContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
        toast.show();
        new android.app.AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setTitle("Application stopped")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
       */
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}
