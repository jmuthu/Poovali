package com.github.jmuthu.poovali.model;

import com.github.jmuthu.poovali.utility.FileRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchRepository {

    private static Map<String, Batch> batchMap = new HashMap<>();

    private static void updateCache(Batch batch) {
        batchMap.put(batch.getId(), batch);
    }

    public static void store(Batch batch) {
        updateCache(batch);
        FileRepository.writeAll(PlantRepository.findAll());
    }

    public static void delete(Batch batch) {
        batchMap.remove(batch.getId());
        FileRepository.writeAll(PlantRepository.findAll());
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
        for (Plant plant : PlantRepository.findAll()) {
            if (plant.getBatchList() == null) {
                continue;
            }
            for (Batch batch : plant.getBatchList()) {
                batch.setPlant(plant);
                updateCache(batch);
            }
        }
    }

}
