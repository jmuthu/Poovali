package com.github.jmuthu.poovali.model.event;

import com.github.jmuthu.poovali.model.Batch;
import com.github.jmuthu.poovali.model.BatchRepository;
import com.github.jmuthu.poovali.utility.FileRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRepository {
    private static final String ENTITY_NAME = "Event";
    private static Map<String, Event> eventMap = new HashMap<>();

    public static void store(Event event) {
        eventMap.put(event.getId(), event);
        FileRepository.writeAll(ENTITY_NAME, eventMap);
    }

    public static Event find(String eventId) {
        return eventMap.get(eventId);
    }

    public static List<Event> findAll() {
        return new ArrayList<>(eventMap.values());
    }

    public static void delete(Event event) {
        eventMap.remove(event.getId());
        FileRepository.writeAll(ENTITY_NAME, eventMap);
    }

    public static void initialize() {
        Object result = FileRepository.readAll(ENTITY_NAME);
        if (result != null) {
            eventMap = (Map<String, Event>) result;
        }
        for (Event event : eventMap.values()) {
            Batch batch = BatchRepository.find(event.getBatchId());
            batch.addEvent(event);
        }
    }
}