package com.github.jmuthu.poovali.model;

import android.content.Context;
import android.util.Log;

import com.github.jmuthu.poovali.utility.Helper;
import com.github.jmuthu.poovali.utility.MyExceptionHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class PlantContent {
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
            Log.e(PlantContent.class.getName(), "Unable to read data file", e);
            MyExceptionHandler.alertAndCloseApp(context, null);
        }
        initializeMaps();
    }

    public static List<Plant> getItems() {
        return Collections.unmodifiableList(plantList);
    }

    private static void initializeMaps() {
        for (Plant plant : getItems()) {
            if (plant.getBatchList() == null) {
                continue;
            }
            for (BatchContent.Batch batch : plant.getBatchList()) {
                BatchContent.addToBatchMap(batch);
            }
        }
    }

    private static void addItem(Plant item) {
        plantList.add(item);
    }

    private static void initializeDefaultItems() {
        addItem(
                new Plant(
                        "1",
                        "Brinjal",
                        "Well drained loamy soils rich in organic matter with a pH range of 6.5-7.5",
                        "December – January and May – June.",
                        "Treat the seeds with Trichoderma viride @ 4 g / kg or Pseudomonas fluorescens @ 10 g / kg of seed. Treat the seeds with Azospirillum @ 40 g / 400 g of seeds using rice gruel as adhesive. Irrigate with rose can. In raised nursery beds, sow the seeds in lines at 10 cm apart and cover with sand. Transplant the seedlings 30 – 35 days after sowing at 60 cm apart in the ridges.",
                        10, 30, 30, 80));
        addItem(
                new Plant(
                        "2",
                        "Chilli",
                        "Well drained loamy soils rich in organic matter with a pH range of 6.5-7.5",
                        "January - February, June - July, September- October",
                        "Treat the seeds with Trichoderma viride @ 4 g / kg or Pseudomonas fluorescens @ 10 g/ kg and sow in lines spaced at 10 cm in raised nursery beds and cover with sand. Watering with rose can has to be done daily. Drench the nursery with Copper oxychloride @ 2.5 g/l of water at 15 days interval against damping off disease. Apply Carbofuran 3 G at 10 g/sq.m. at sowing.",
                        10, 30, 40, 80));
        addItem(
                new Plant(
                        "3",
                        "Lady's Finger",
                        "It is adaptable to a wide range of soils from sandy loam to clayey loam. ",
                        "Planting can be done during June - August and February",
                        "Seed treatment with Tricoderma viride @ 4 g/kg or Pseudomonas fluorescens @ 10 g/ kg of seeds and again with 400 g of Azospirillum using starch as adhesive and dried in shade for 20 minutes. Sow three seeds per hill at 30 cm apart and then thin to 2 plants per hill after 10 days.",
                        10, 30, 30, 30));
        addItem(
                new Plant(
                        "4",
                        "Radish",
                        "Sandy loam soils with high organic matter content are highly suited for radish_detail cultivation. The highest yield can be obtained at a soil pH of 5.5 to 6.8. Roots of best size, flavour and texture are developed at about 15°C.",
                        "June –July in hills and September in plains are best suited.",
                        "",
                        15, 20, 10, 10));
        addItem(
                new Plant(
                        "5",
                        "Tomato",
                        "Well drained loamy soils rich in organic matter with a pH range of 6.5-7.5",
                        "May - June and November – December",
                        "Treat the seeds with Trichoderma viride 4 g or Pseudomonas fluorescens 10 g or Carbendazim 2 g per kg of seeds 24 hours before sowing. Just before sowing, treat the seeds with Azospirillum @ 40 g / 400 g of seeds. Sow in lines at 10 cm apart in raised nursery beds and cover with sand.",
                        10, 30, 30, 80));

    }

    public static Plant getPlant(String plantId) {
        // Small list size, hash map would be an overkill
        // so using list to traverse
        for (Plant plant : getItems()) {
            if (plant.getId().equals(plantId)) {
                return plant;
            }
        }
        return null;
    }

    public static void saveItems(Context context) {
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
            Log.e(PlantContent.class.getName(), "Unable to save data to file", e);
            MyExceptionHandler.alertAndCloseApp(context, null);
        }
    }

    public static List<NotificationContent> pendingActivities() {
        List<NotificationContent> notification = new ArrayList<>();
        if (getItems().size() == 0) {
            return notification;
        }
        for (Plant plant : getItems()) {
            if (plant.getBatchList().isEmpty()) {
                continue;
            }
            Integer dayCount = plant.pendingSowDays();
            if (dayCount > 0) {
                notification.add(new NotificationContent(
                        "Sow " + plant.getName() + "!",
                        dayCount + (dayCount > 1 ? " days " : " day ") + "overdue"));
            } else if (dayCount == 0) {
                notification.add(new NotificationContent(
                        "Sow " + plant.getName() + " today!",
                        ""));
            }
        }
        return notification;
    }

    public static List<BatchContent.Batch> getBatchList() {
        LinkedList<BatchContent.Batch> batches = new LinkedList<>();
        for (Plant plant : getItems()) {
            if (!plant.getBatchList().isEmpty()) {
                batches.addAll(plant.getBatchList());
            }
        }
        Collections.sort(batches, new BatchContent.Batch.BatchDescendingComparator());
        return Collections.unmodifiableList(batches);
    }

    public enum GrowthStage {
        SEEDLING {
            public String toString() {
                return "Seedling";
            }
        },
        FLOWERING {
            public String toString() {
                return "Flowering";
            }
        },
        FRUITING {
            public String toString() {
                return "Fruiting";
            }
        },
        RIPENING {
            public String toString() {
                return "Ripening";
            }
        },
        DORMANT {
            public String toString() {
                return "Dormant";
            }
        }
    }

    public static class Plant implements Serializable, Helper.DisplayableItem {
        private static final long serialVersionUID = 1L;
        private String id;
        private String name;
        private String sowingSeason;
        private String seedTreatment;
        private String soil;
        private EnumMap<GrowthStage, Integer> growthStageMap = new EnumMap<GrowthStage, Integer>(GrowthStage.class);
        private List<BatchContent.Batch> batchList = new LinkedList<>();
        //public final String spacingRequirements;
        //public final Map fertilizerSchedule;

        public Plant() {
        }

        public Plant(String id,
                     String name,
                     String soil,
                     String sowingSeason,
                     String seedTreatment,
                     Integer seedling,
                     Integer flowering,
                     Integer fruiting,
                     Integer ripening) {
            this.id = id;
            this.name = name;
            this.soil = soil;
            this.sowingSeason = sowingSeason;
            this.seedTreatment = seedTreatment;
            this.growthStageMap.put(GrowthStage.SEEDLING, seedling);
            this.growthStageMap.put(GrowthStage.FLOWERING, flowering);
            this.growthStageMap.put(GrowthStage.FRUITING, fruiting);
            this.growthStageMap.put(GrowthStage.RIPENING, ripening);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSowingSeason() {
            return sowingSeason;
        }

        public void setSowingSeason(String sowingSeason) {
            this.sowingSeason = sowingSeason;
        }

        public String getSeedTreatment() {
            return seedTreatment;
        }

        public void setSeedTreatment(String seedTreatment) {
            this.seedTreatment = seedTreatment;
        }

        public Integer getCropDuration() {
            Integer cropDuration = 0;
            for (Integer value : growthStageMap.values()) {
                cropDuration += value;
            }
            return cropDuration;
        }

        public String getSoil() {
            return soil;
        }

        public void setSoil(String soil) {
            this.soil = soil;
        }

        public EnumMap<GrowthStage, Integer> getGrowthStageMap() {
            return growthStageMap;
        }

        public void setGrowthStageMap(EnumMap<GrowthStage, Integer> growthStageMap) {
            this.growthStageMap = growthStageMap;
        }

        public List<BatchContent.Batch> getBatchList() {
            return Collections.unmodifiableList(batchList);
        }

        public void setBatchList(List<BatchContent.Batch> batchList) {
            this.batchList = batchList;
        }

        public String getImageName() {
            return Helper.getImageFileName(name);
        }

        @Override
        public String toString() {
            return name;
        }

        public Date getNextSowingDate(Date createdDate) {
            Calendar c = Calendar.getInstance();
            c.setTime(createdDate);
            c.add(Calendar.DATE, growthStageMap.get(GrowthStage.RIPENING));
            return c.getTime();
        }

        public boolean isDuplicateBatch(Date newBatchdate) {
            Date date = Helper.getZeroTimeDate(newBatchdate);
            for (BatchContent.Batch batch : batchList) {
                if (date.compareTo(Helper.getZeroTimeDate(batch.getCreatedDate())) == 0) {
                    return true;
                }
            }
            return false;
        }

        public BatchContent.Batch getLatestBatch() {
            if (batchList.isEmpty()) {
                return null;
            }
            return batchList.get(0);
        }

        public Integer pendingSowDays() {
            if (getLatestBatch() == null) {
                return null;
            }
            Long diff = (Calendar.getInstance().getTimeInMillis() -
                    getNextSowingDate(getLatestBatch().getCreatedDate()).getTime()) / (24 * 60 * 60 * 1000);
            return diff.intValue();
        }

        public void addBatch(Context context, BatchContent.Batch batch) {
            batchList.add(0, batch);
            BatchContent.addToBatchMap(batch);
            Collections.sort(batchList, new BatchContent.Batch.BatchDescendingComparator());
            saveItems(context);
        }

        public void deleteBatch(Context context, int position) {
            BatchContent.Batch batch = batchList.get(position);
            BatchContent.removeFromBatchMap(batch);
            batchList.remove(position);
            saveItems(context);
        }
    }
}
