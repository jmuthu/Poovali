package com.github.jmuthu.poovali;

import android.app.Application;

import com.github.jmuthu.poovali.model.PlantContent;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PlantContent.initialize(this);
    }
}
