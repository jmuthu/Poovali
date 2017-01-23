package com.github.jmuthu.poovali;

import android.app.Application;
import android.content.Context;

import com.github.jmuthu.poovali.model.event.EventRepository;
import com.github.jmuthu.poovali.model.plant.PlantBatchRepository;
import com.github.jmuthu.poovali.model.plant.PlantRepository;

public class MyApplication extends Application {
    static private Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        PlantRepository.initialize();
        PlantBatchRepository.initialize();
        EventRepository.initialize();
    }

    static public Context getContext() {
        return context;
    }
}
