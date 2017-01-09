package com.github.jmuthu.poovali.model;

import com.github.jmuthu.poovali.model.event.Event;
import com.github.jmuthu.poovali.model.event.EventRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchRepository {

    private static Map<String, Batch> batchMap = new HashMap<>();

    public static Batch find(String batchId) {
        return batchMap.get(batchId);
    }

    public static void store(Batch batch) {
        batchMap.put(batch.getId(), batch);
        if (batch.getEvents() == null) {
            return;
        }
        for (Event event : batch.getEvents()) {
            EventRepository.store(event);
        }
    }

    public static void delete(Batch batch) {
        batchMap.remove(batch.getId());
    }

    public static List<Batch> findAll() {
        return findAll(false);
    }

    public static List<Batch> findAll(boolean sortByModifiedDate) {
        ArrayList<Batch> result = new ArrayList<>(batchMap.values());
        if (sortByModifiedDate) {
            Collections.sort(result, new Batch.BatchModifiedDescendingComparator());
        } else {
            Collections.sort(result, new Batch.BatchNameComparator());
        }
        return Collections.unmodifiableList(result);
    }

    public static boolean isEmpty() {
        return batchMap.isEmpty();
    }


}
