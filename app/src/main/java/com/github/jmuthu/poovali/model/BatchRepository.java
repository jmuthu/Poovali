package com.github.jmuthu.poovali.model;

import com.github.jmuthu.poovali.utility.FileRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BatchRepository {
    private static final String ENTITY_NAME = "Batch";
    private static Map<String, Batch> batchMap = new HashMap<>();
    private static List<Batch> batchList = new LinkedList<Batch>(); // To make findAll really fast
    private static Batch.BatchNameComparator batchNameComparator = new Batch.BatchNameComparator();
    private static Batch.BatchModifiedDescendingComparator batchModifiedDescendingComparator =
            new Batch.BatchModifiedDescendingComparator();

    public static void store(Batch batch) {
        batchMap.put(batch.getId(), batch);
        if (!batchList.contains(batch)) {
            batchList.add(batch);
        }
        Collections.sort(batchList, batchModifiedDescendingComparator);
        FileRepository.writeAll(ENTITY_NAME, batchMap);
    }

    public static void delete(Batch batch) {
        batchMap.remove(batch.getId());
        batchList.remove(batch);
        FileRepository.writeAll(ENTITY_NAME, batchMap);
    }

    public static Batch find(String batchId) {
        return batchMap.get(batchId);
    }

    public static List<Batch> findAll() {
        return findAll(false);
    }

    public static List<Batch> findAll(boolean sortByModifiedDate) {
        if (!sortByModifiedDate) {
            //Collections.sort(batchList, batchNameComparator);
        }
        return Collections.unmodifiableList(batchList);
    }

    public static boolean isEmpty() {
        return batchMap.isEmpty();
    }

    public static void initialize() {
        Object result = FileRepository.readAll(ENTITY_NAME);
        if (result != null) {
            batchMap = (Map<String, Batch>) result;
            batchList = new LinkedList<>(batchMap.values());
            Collections.sort(batchList, batchModifiedDescendingComparator);
        }
        for (Batch batch : batchMap.values()) {
            Plant plant = PlantRepository.find(batch.getPlantId());
            plant.addBatch(batch);
        }
    }
}
