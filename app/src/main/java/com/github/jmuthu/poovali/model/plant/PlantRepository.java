package com.github.jmuthu.poovali.model.plant;

import com.github.jmuthu.poovali.utility.FileRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlantRepository {
    private static final String ENTITY_NAME = "Plant";
    private static Map<Integer, Plant> plantMap = new HashMap<>();
    private static List<Plant> plantList = new LinkedList<>(); // To make findAll really fast
    private static Plant.PlantNameComparator plantNameComparator = new Plant.PlantNameComparator();
    private static int maxPlantId = 10000; // 1 -10000 reserved for default plants

    public static int nextPlantId() {
        return ++maxPlantId;
    }

    public static Plant find(int plantId) {
        return plantMap.get(plantId);
    }

    public static Plant findByName(String name) {
        for (Plant plant : plantList) {
            if (plant.getName().equals(name)) {
                return plant;
            }
        }
        return null;
    }

    public static List<Plant> findAll() {
        return Collections.unmodifiableList(plantList);
    }

    public static void store(Plant plant) {
        addPlant(plant);
        FileRepository.writeAll(ENTITY_NAME, plantMap);
    }

    public static void delete(Plant plant) {
        plantMap.remove(plant.getId());
        plantList.remove(plant);
        FileRepository.writeAll(ENTITY_NAME, plantMap);
    }

    @SuppressWarnings("unchecked")
    public static void initialize() {
        Object result = FileRepository.readAll(ENTITY_NAME);
        if (result != null) {
            plantMap = (Map<Integer, Plant>) result;
            plantList = new LinkedList<>(plantMap.values());
            for (Plant plant : plantList) {
                if (plant.getId() > maxPlantId) {
                    maxPlantId = plant.getId();
                }
            }
        } else {
            initializeDefaultItems();
        }
        Collections.sort(plantList, plantNameComparator);
    }

    private static void addPlant(Plant plant) {
        plantMap.put(plant.getId(), plant);
        if (!plantList.contains(plant)) {
            plantList.add(plant);
        }
        Collections.sort(plantList, plantNameComparator); // Need to do for modified plants
    }

    private static void initializeDefaultItems() {
        addPlant(
                new Plant(
                        1,
                        "Brinjal",
                        null,
                        10, 30, 30, 80));
        addPlant(
                new Plant(
                        2,
                        "Chilli",
                        null,
                        10, 30, 40, 80));
        addPlant(
                new Plant(
                        3,
                        "Lady's Finger",
                        null,
                        10, 30, 30, 30));
        addPlant(
                new Plant(
                        4,
                        "Radish",
                        null,
                        15, 20, 10, 10));
        addPlant(
                new Plant(
                        5,
                        "Tomato",
                        null,
                        10, 30, 30, 80));
        FileRepository.writeAll(ENTITY_NAME, plantMap);
    }
}
