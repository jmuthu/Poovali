package com.github.jmuthu.poovali.utility;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MyExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {
    private final Activity myContext;

    public MyExceptionHandler(Activity context) {
        myContext = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        String LINE_SEPARATOR = "\n";
        String errorReport =
                "************ CAUSE OF ERROR ************\n\n" +
                        stackTrace.toString() +
                        "\n************ DEVICE INFORMATION ***********\n" +
                        "Brand: " +
                        Build.BRAND + LINE_SEPARATOR +
                        "Device: " +
                        Build.DEVICE + LINE_SEPARATOR +
                        "Model: " +
                        Build.MODEL + LINE_SEPARATOR +
                        "Id: " +
                        Build.ID + LINE_SEPARATOR +
                        "Product: " +
                        Build.PRODUCT + LINE_SEPARATOR +
                        "\n************ FIRMWARE ************\n" +
                        "SDK: " +
                        Build.VERSION.SDK_INT + LINE_SEPARATOR +
                        "Release: " +
                        Build.VERSION.RELEASE + LINE_SEPARATOR +
                        "Incremental: " +
                        Build.VERSION.INCREMENTAL + LINE_SEPARATOR;

        Log.e(myContext.getPackageName(), exception.getMessage(), exception);
        Log.e(myContext.getPackageName(), errorReport);
        Helper.alertAndCloseApp(null);
    }

}