package org.onestraw.poovali;

import android.app.Application;

import org.onestraw.poovali.model.BatchContent;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BatchContent.initialize(this);
    }
}
