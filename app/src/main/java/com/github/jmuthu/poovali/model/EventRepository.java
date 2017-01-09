package com.github.jmuthu.poovali.model;

import java.util.HashMap;
import java.util.Map;

public class EventRepository {
    private static Map<String, Event> eventMap = new HashMap<>();

    public static void store(Event event) {
        eventMap.put(event.getId(), event);
    }

    public static Event find(String eventId) {
        return eventMap.get(eventId);
    }

    public static void delete(Event event) {
        eventMap.remove(event.getId());
    }
}