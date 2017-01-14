package com.github.jmuthu.poovali.model.event;

import java.util.UUID;

public class BatchEventFactory {
    public static BatchActivityEvent createEvent(BatchActivityEvent.Type type) {
        BatchActivityEvent event = new BatchActivityEvent();
        event.setType(type);
        event.setId(UUID.randomUUID().toString());
        return event;
    }
}
