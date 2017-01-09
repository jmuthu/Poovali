package com.github.jmuthu.poovali.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchRepository {

    private static Map<String, Batch> batchMap = new HashMap<>();

    public static Batch getBatch(String batchId) {
        return batchMap.get(batchId);
    }

    public static void addToBatchMap(Batch batch) {
        batchMap.put(batch.getId(), batch);
        if (batch.getEvents() == null) {
            return;
        }
        for (EventContent.Event event : batch.getEvents()) {
            EventContent.addToEventMap(event);
        }
    }

    public static void removeFromBatchMap(Batch batch) {
        batchMap.remove(batch.getId());
    }

    public static List<Batch> getBatchList() {
        return getBatchList(false);
    }

    public static List<Batch> getBatchList(boolean sortByModifiedDate) {
        ArrayList<Batch> result = new ArrayList<>(batchMap.values());
        if (sortByModifiedDate) {
            Collections.sort(result, new Batch.BatchModifiedDescendingComparator());
        } else {
            Collections.sort(result, new Batch.BatchNameComparator());
        }
        return Collections.unmodifiableList(result);
    }

    public static boolean isBatchListEmpty() {
        return batchMap.isEmpty();
    }


}
