package com.github.jmuthu.poovali.model;

import com.github.jmuthu.poovali.utility.FileRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlantRepository {

    private static List<Plant> plantList = new ArrayList<Plant>();

    public static Plant find(String plantId) {
        // Small list size, hash map would be an overkill
        // so using list to traverse
        for (Plant plant : findAll()) {
            if (plant.getId().equals(plantId)) {
                return plant;
            }
        }
        return null;
    }

    public static List<Plant> findAll() {
        return Collections.unmodifiableList(plantList);
    }

    public static void store(Plant plant) {
        plantList.add(plant);
        FileRepository.writeAll(plantList);
    }

    public static void initialize() {
        Object result = FileRepository.readAll();
        if (result != null) {
            plantList = (List<Plant>) result;
        } else {
            initializeDefaultItems();
        }
    }

    private static void initializeDefaultItems() {
        plantList.add(
                new Plant(
                        UUID.randomUUID().toString(),
                        "Brinjal",
                        null,
                        10, 30, 30, 80));
        plantList.add(
                new Plant(
                        UUID.randomUUID().toString(),
                        "Chilli",
                        null,
                        10, 30, 40, 80));
        plantList.add(
                new Plant(
                        UUID.randomUUID().toString(),
                        "Lady's Finger",
                        null,
                        10, 30, 30, 30));
        plantList.add(
                new Plant(
                        UUID.randomUUID().toString(),
                        "Radish",
                        null,
                        15, 20, 10, 10));
        plantList.add(
                new Plant(
                        UUID.randomUUID().toString(),
                        "Tomato",
                        null,
                        10, 30, 30, 80));

    }
}
