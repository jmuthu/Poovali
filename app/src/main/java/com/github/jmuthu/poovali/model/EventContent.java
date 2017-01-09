package com.github.jmuthu.poovali.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventContent {
    private static Map<String, Event> eventMap = new HashMap<>();

    public static void addToEventMap(Event event) {
        eventMap.put(event.getId(), event);
    }

    public static Event getEvent(String eventId) {
        return eventMap.get(eventId);
    }

    public static void removeFromEventMap(Event event) {
        eventMap.remove(event.getId());
    }

    public static Event createEvent(boolean isSowActivity) {
        Event event;
        if (isSowActivity) {
            event = new SowBatchEvent();
        } else {
            event = new BatchActivityEvent();
        }
        event.setId(UUID.randomUUID().toString());
        return event;
    }

}