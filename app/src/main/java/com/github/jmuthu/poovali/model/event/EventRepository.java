package com.github.jmuthu.poovali.model.event;

import com.github.jmuthu.poovali.model.plant.PlantBatch;
import com.github.jmuthu.poovali.model.plant.PlantBatchRepository;
import com.github.jmuthu.poovali.utility.FileRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRepository {
    private static final String ENTITY_NAME = "Event";
    private static Map<Integer, Event> eventMap = new HashMap<>();
    private static int maxEventId = 0;

    public static void store(Event event) {
        eventMap.put(event.getId(), event);
        FileRepository.writeAll(ENTITY_NAME, eventMap);
    }

    public static Event find(int eventId) {
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
            eventMap = (Map<Integer, Event>) result;
        }
        for (Event event : eventMap.values()) {
            PlantBatch plantBatch = PlantBatchRepository.find(event.getBatchId());
            plantBatch.addOrUpdateEvent(event);
            if (event.getId() > maxEventId) {
                maxEventId = event.getId();
            }
        }
    }

    public static int nextEventId() {
        return ++maxEventId;
    }
}