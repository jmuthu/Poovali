package com.github.jmuthu.poovali;

import android.app.Application;

import com.github.jmuthu.poovali.model.event.EventRepository;
import com.github.jmuthu.poovali.model.plant.PlantBatchRepository;
import com.github.jmuthu.poovali.model.plant.PlantRepository;
import com.github.jmuthu.poovali.utility.FileRepository;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileRepository.context = this;
        PlantRepository.initialize();
        PlantBatchRepository.initialize();
        EventRepository.initialize();
    }
}
