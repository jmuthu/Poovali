package com.github.jmuthu.poovali.model.plant;

import com.github.jmuthu.poovali.utility.FileRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlantBatchRepository {
    private static final String ENTITY_NAME = "PlantBatch";
    private static Map<Integer, PlantBatch> batchMap = new HashMap<>();
    private static List<PlantBatch> plantBatchList = new LinkedList<PlantBatch>(); // To make findAll really fast
    private static PlantBatch.BatchNameComparator batchNameComparator = new PlantBatch.BatchNameComparator();
    private static PlantBatch.BatchModifiedDescendingComparator batchModifiedDescendingComparator =
            new PlantBatch.BatchModifiedDescendingComparator();
    private static int maxPlantBatchId = 0;

    public static void store(PlantBatch plantBatch) {
        batchMap.put(plantBatch.getId(), plantBatch);
        if (!plantBatchList.contains(plantBatch)) {
            plantBatchList.add(plantBatch);
        }
        Collections.sort(plantBatchList, batchModifiedDescendingComparator);
        FileRepository.writeAll(ENTITY_NAME, batchMap);
    }

    public static void delete(PlantBatch plantBatch) {
        batchMap.remove(plantBatch.getId());
        plantBatchList.remove(plantBatch);
        FileRepository.writeAll(ENTITY_NAME, batchMap);
    }

    public static PlantBatch find(int batchId) {
        return batchMap.get(batchId);
    }

    public static List<PlantBatch> findAll() {
        return findAll(false);
    }

    public static List<PlantBatch> findAll(boolean sortByModifiedDate) {
        if (!sortByModifiedDate) {
            //Collections.sort(plantBatchList, batchNameComparator);
        }
        return Collections.unmodifiableList(plantBatchList);
    }

    public static boolean isEmpty() {
        return batchMap.isEmpty();
    }

    public static void initialize() {
        Object result = FileRepository.readAll(ENTITY_NAME);
        if (result != null) {
            batchMap = (Map<Integer, PlantBatch>) result;
            plantBatchList = new LinkedList<>(batchMap.values());
            Collections.sort(plantBatchList, batchModifiedDescendingComparator);
        }
        for (PlantBatch plantBatch : batchMap.values()) {
            Plant plant = PlantRepository.find(plantBatch.getPlantId());
            plant.addOrUpdatePlantBatch(plantBatch);
            if (plantBatch.getId() > maxPlantBatchId) {
                maxPlantBatchId = plantBatch.getId();
            }
        }
    }

    public static int nextPlantBatchId() {
        return ++maxPlantBatchId;
    }
}
