package com.github.jmuthu.poovali.model.event;

public class BatchEventFactory {
    public static BatchActivityEvent createEvent(BatchActivityEvent.Type type) {
        BatchActivityEvent event = new BatchActivityEvent();
        event.setType(type);
        event.setId(EventRepository.nextEventId());
        return event;
    }
}
