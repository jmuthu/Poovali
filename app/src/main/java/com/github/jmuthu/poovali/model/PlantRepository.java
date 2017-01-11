package com.github.jmuthu.poovali.model;

import com.github.jmuthu.poovali.utility.FileRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlantRepository {
    private static final String ENTITY_NAME = "Plant";
    private static Map<String, Plant> plantMap = new HashMap<>();

    public static Plant find(String plantId) {
        return plantMap.get(plantId);
    }

    public static List<Plant> findAll() {
        ArrayList<Plant> result = new ArrayList<>(plantMap.values());
        Collections.sort(result, new Plant.PlantNameComparator());
        return Collections.unmodifiableList(result);
    }

    public static void store(Plant plant) {
        addPlant(plant);
        FileRepository.writeAll(ENTITY_NAME, plantMap);
    }

    public static void delete(Plant plant) {
        plantMap.remove(plant.getId());
        FileRepository.writeAll(ENTITY_NAME, plantMap);
    }

    public static void initialize() {
        Object result = FileRepository.readAll(ENTITY_NAME);
        if (result != null) {
            plantMap = (Map<String, Plant>) result;
        } else {
            initializeDefaultItems();
        }
    }

    private static void addPlant(Plant plant) {
        plantMap.put(plant.getId(), plant);
    }

    private static void initializeDefaultItems() {
        addPlant(
                new Plant(
                        UUID.randomUUID().toString(),
                        "Brinjal",
                        null,
                        10, 30, 30, 80));
        addPlant(
                new Plant(
                        UUID.randomUUID().toString(),
                        "Chilli",
                        null,
                        10, 30, 40, 80));
        addPlant(
                new Plant(
                        UUID.randomUUID().toString(),
                        "Lady's Finger",
                        null,
                        10, 30, 30, 30));
        addPlant(
                new Plant(
                        UUID.randomUUID().toString(),
                        "Radish",
                        null,
                        15, 20, 10, 10));
        addPlant(
                new Plant(
                        UUID.randomUUID().toString(),
                        "Tomato",
                        null,
                        10, 30, 30, 80));

    }
}
