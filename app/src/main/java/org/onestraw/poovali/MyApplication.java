package org.onestraw.poovali;

import android.app.Application;

import org.onestraw.poovali.model.PlantContent;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PlantContent.initialize(this);
    }
}
