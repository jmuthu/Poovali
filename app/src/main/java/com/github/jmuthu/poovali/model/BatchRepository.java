package com.github.jmuthu.poovali.model;

import com.github.jmuthu.poovali.utility.FileRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchRepository {
    private static final String ENTITY_NAME = "Batch";
    private static Map<String, Batch> batchMap = new HashMap<>();

    public static void store(Batch batch) {
        batchMap.put(batch.getId(), batch);
        FileRepository.writeAll(ENTITY_NAME, batchMap);
    }

    public static void delete(Batch batch) {
        batchMap.remove(batch.getId());
        FileRepository.writeAll(ENTITY_NAME, batchMap);
    }

    public static Batch find(String batchId) {
        return batchMap.get(batchId);
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

    public static void initialize() {
        Object result = FileRepository.readAll(ENTITY_NAME);
        if (result != null) {
            batchMap = (Map<String, Batch>) result;
        }
        for (Batch batch : batchMap.values()) {
            Plant plant = PlantRepository.find(batch.getPlantId());
            plant.addBatch(batch);
        }
    }
}
