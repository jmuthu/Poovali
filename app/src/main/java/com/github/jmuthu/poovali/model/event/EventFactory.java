package com.github.jmuthu.poovali.model.event;

import java.util.UUID;

public class EventFactory {
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
