package com.github.jmuthu.poovali.model;

import android.content.Context;
import android.util.Log;

import com.github.jmuthu.poovali.utility.MyExceptionHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlantRepository {
    private static final String BATCH_FILE = "poovali_batch.dat";

    private static List<Plant> plantList = new ArrayList<Plant>();

    public static void initialize(Context context) {
        try {
            File file = new File(context.getFilesDir(), BATCH_FILE);
            if (file.isFile()) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fin);
                plantList = (List<Plant>) ois.readObject();
                ois.close();
            } else {
                initializeDefaultItems();
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.e(PlantRepository.class.getName(), "Unable to read data file", e);
            MyExceptionHandler.alertAndCloseApp(context, null);
        }
        initializeMaps();
    }


    private static void initializeMaps() {
        for (Plant plant : findAll()) {
            if (plant.getBatchList() == null) {
                continue;
            }
            for (Batch batch : plant.getBatchList()) {
                BatchRepository.store(batch);
            }
        }
    }


    private static void initializeDefaultItems() {
        store(
                new Plant(
                        "1",
                        "Brinjal",
                        "Well drained loamy soils rich in organic matter with a pH range of 6.5-7.5",
                        "December – January and May – June.",
                        "Treat the seeds with Trichoderma viride @ 4 g / kg or Pseudomonas fluorescens @ 10 g / kg of seed. Treat the seeds with Azospirillum @ 40 g / 400 g of seeds using rice gruel as adhesive. Irrigate with rose can. In raised nursery beds, sow the seeds in lines at 10 cm apart and cover with sand. Transplant the seedlings 30 – 35 days after sowing at 60 cm apart in the ridges.",
                        10, 30, 30, 80));
        store(
                new Plant(
                        "2",
                        "Chilli",
                        "Well drained loamy soils rich in organic matter with a pH range of 6.5-7.5",
                        "January - February, June - July, September- October",
                        "Treat the seeds with Trichoderma viride @ 4 g / kg or Pseudomonas fluorescens @ 10 g/ kg and sow in lines spaced at 10 cm in raised nursery beds and cover with sand. Watering with rose can has to be done daily. Drench the nursery with Copper oxychloride @ 2.5 g/l of water at 15 days interval against damping off disease. Apply Carbofuran 3 G at 10 g/sq.m. at sowing.",
                        10, 30, 40, 80));
        store(
                new Plant(
                        "3",
                        "Lady's Finger",
                        "It is adaptable to a wide range of soils from sandy loam to clayey loam. ",
                        "Planting can be done during June - August and February",
                        "Seed treatment with Tricoderma viride @ 4 g/kg or Pseudomonas fluorescens @ 10 g/ kg of seeds and again with 400 g of Azospirillum using starch as adhesive and dried in shade for 20 minutes. Sow three seeds per hill at 30 cm apart and then thin to 2 plants per hill after 10 days.",
                        10, 30, 30, 30));
        store(
                new Plant(
                        "4",
                        "Radish",
                        "Sandy loam soils with high organic matter content are highly suited for radish_detail cultivation. The highest yield can be obtained at a soil pH of 5.5 to 6.8. Roots of best size, flavour and texture are developed at about 15°C.",
                        "June –July in hills and September in plains are best suited.",
                        "",
                        15, 20, 10, 10));
        store(
                new Plant(
                        "5",
                        "Tomato",
                        "Well drained loamy soils rich in organic matter with a pH range of 6.5-7.5",
                        "May - June and November – December",
                        "Treat the seeds with Trichoderma viride 4 g or Pseudomonas fluorescens 10 g or Carbendazim 2 g per kg of seeds 24 hours before sowing. Just before sowing, treat the seeds with Azospirillum @ 40 g / 400 g of seeds. Sow in lines at 10 cm apart in raised nursery beds and cover with sand.",
                        10, 30, 30, 80));

    }

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
    }

    public static void storeAll(Context context) {
        try {
            File file = new File(context.getFilesDir(), BATCH_FILE);
            if (!file.isFile()) {
                file.createNewFile();
            }
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(plantList);
            oos.close();
        } catch (IOException e) {
            Log.e(PlantRepository.class.getName(), "Unable to save data to file", e);
            MyExceptionHandler.alertAndCloseApp(context, null);
        }
    }

}
