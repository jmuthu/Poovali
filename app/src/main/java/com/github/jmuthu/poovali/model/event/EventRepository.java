package com.github.jmuthu.poovali.model.event;

import com.github.jmuthu.poovali.model.Batch;
import com.github.jmuthu.poovali.model.BatchRepository;
import com.github.jmuthu.poovali.model.PlantRepository;
import com.github.jmuthu.poovali.utility.FileRepository;

import java.util.HashMap;
import java.util.Map;

public class EventRepository {
    private static Map<String, Event> eventMap = new HashMap<>();

    private static void updateCache(Event event) {
        eventMap.put(event.getId(), event);
    }

    public static void store(Event event) {
        updateCache(event);
        FileRepository.writeAll(PlantRepository.findAll());
    }

    public static Event find(String eventId) {
        return eventMap.get(eventId);
    }

    public static void delete(Event event) {
        eventMap.remove(event.getId());
        FileRepository.writeAll(PlantRepository.findAll());
    }

    public static void initialize() {
        for (Batch batch : BatchRepository.findAll()) {
            if (batch.getEvents() == null) {
                return;
            }
            for (Event event : batch.getEvents()) {
                EventRepository.updateCache(event);
            }
        }
    }
}