package org.onestraw.poovali;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PlantContent {

    static final String ARG_ITEM_ID = "item_id";
    private static final List<Plant> ITEMS = new ArrayList<Plant>();
    private static final Map<String, Plant> ITEM_MAP = new HashMap<String, Plant>();


    static void initializeItems() {
        addItem(
                new Plant(
                        "1",
                        "Brinjal",
                        "Well drained loamy soils rich in organic matter with a pH range of 6.5-7.5",
                        "December – January and May – June.",
                        "Treat the seeds with Trichoderma viride @ 4 g / kg or Pseudomonas fluorescens @ 10 g / kg of seed. Treat the seeds with Azospirillum @ 40 g / 400 g of seeds using rice gruel as adhesive. Irrigate with rose can. In raised nursery beds, sow the seeds in lines at 10 cm apart and cover with sand. Transplant the seedlings 30 – 35 days after sowing at 60 cm apart in the ridges.",
                        150));
        addItem(
                new Plant(
                        "2",
                        "Chilli",
                        "Well drained loamy soils rich in organic matter with a pH range of 6.5-7.5",
                        "January - February, June - July, September- October",
                        "Treat the seeds with Trichoderma viride @ 4 g / kg or Pseudomonas fluorescens @ 10 g/ kg and sow in lines spaced at 10 cm in raised nursery beds and cover with sand. Watering with rose can has to be done daily. Drench the nursery with Copper oxychloride @ 2.5 g/l of water at 15 days interval against damping off disease. Apply Carbofuran 3 G at 10 g/sq.m. at sowing.",
                        160));
        addItem(
                new Plant(
                        "3",
                        "Lady's Finger",
                        "It is adaptable to a wide range of soils from sandy loam to clayey loam. ",
                        "Planting can be done during June - August and February",
                        "Seed treatment with Tricoderma viride @ 4 g/kg or Pseudomonas fluorescens @ 10 g/ kg of seeds and again with 400 g of Azospirillum using starch as adhesive and dried in shade for 20 minutes. Sow three seeds per hill at 30 cm apart and then thin to 2 plants per hill after 10 days.",
                        100));
        addItem(
                new Plant(
                        "4",
                        "Radish",
                        "Sandy loam soils with high organic matter content are highly suited for radish cultivation. The highest yield can be obtained at a soil pH of 5.5 to 6.8. Roots of best size, flavour and texture are developed at about 15°C.",
                        "June –July in hills and September in plains are best suited.",
                        "",
                        55));
        addItem(
                new Plant(
                        "5",
                        "Tomato",
                        "Well drained loamy soils rich in organic matter with a pH range of 6.5-7.5",
                        "May - June and November – December",
                        "Treat the seeds with Trichoderma viride 4 g or Pseudomonas fluorescens 10 g or Carbendazim 2 g per kg of seeds 24 hours before sowing. Just before sowing, treat the seeds with Azospirillum @ 40 g / 400 g of seeds. Sow in lines at 10 cm apart in raised nursery beds and cover with sand.",
                        150));

    }

    static List<Plant> getItems(Context context) {
        if (ITEMS.isEmpty()) {
            initializeItems();
        }
        return ITEMS;
    }

    static Map<String, Plant> getItemMap(Context context) {
        if (ITEM_MAP.isEmpty()) {
            initializeItems();
        }
        return ITEM_MAP;
    }

    private static void addItem(Plant item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    static class Plant {
        public final String id;
        public final String name;
        final String soil;
        final String sowingSeason;
        final String seedTreatment;
        final String imageName;
        final Integer cropDuration;
        //public final String spacingRequirements;
        //public final Map fertilizerSchedule;

        Plant(String id, String name, String soil, String sowingSeason, String seedTreatment, Integer cropDuration) {
            this.id = id;
            this.name = name;
            this.soil = soil;
            this.sowingSeason = sowingSeason;
            this.seedTreatment = seedTreatment;
            this.cropDuration = cropDuration;
            this.imageName = Helper.getImageFileName(name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public final class GrowthStages {
        final Integer SowingToPlant;
        final Integer FlowerInitiationToFlowering;
        final Integer FloweringToFruit;
        final Integer Harvesting;

        public GrowthStages(Integer SowingToPlant,
                            Integer FlowerInitiationToFlowering,
                            Integer FloweringToFruit,
                            Integer Harvesting) {
            this.SowingToPlant = SowingToPlant;
            this.FlowerInitiationToFlowering = FlowerInitiationToFlowering;
            this.FloweringToFruit = FloweringToFruit;
            this.Harvesting = Harvesting;
        }
    }
}
