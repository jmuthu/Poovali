package com.github.jmuthu.poovali;

import android.app.Application;

import com.github.jmuthu.poovali.model.PlantRepository;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PlantRepository.initialize(this);
    }
}
