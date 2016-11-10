package onestraw.org.poovali;

/**
 * Created by mike on 8/11/16.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlantContent {


    public static final String ARG_ITEM_ID = "item_id";
    /**
     * An array of sample (dummy) items.
     */
    public static final List<Plant> ITEMS = new ArrayList<Plant>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Plant> ITEM_MAP = new HashMap<String,Plant>();

    private static final int COUNT = 1;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(
                    new Plant(
                            "1",
                            "Tomato",
                            "Well drained loamy soils rich in organic matter with a pH range of 6.5-7.5",
                            "May - June and November – December",
                            "Treat the seeds with Trichoderma viride 4 g or Pseudomonas fluorescens 10 g or Carbendazim 2 g per kg of seeds 24 hours before sowing. Just before sowing, treat the seeds with Azospirillum @ 40 g / 400 g of seeds. Sow in lines at 10 cm apart in raised nursery beds and cover with sand.",
                            145));
        }
    }

    private static void addItem(Plant item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
    /*
    private static Plant createPlant(int position) {
        return new Plant(String.valueOf(position), name, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Plant: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
   */
    /**
     * A dummy item representing a piece of content.
     */
    public static class Plant {
        public final String id;
        public final String name;
        public final String soil;
        public final String sowingSeason;
        public final String seedTreatment;
        public final Integer cropDuration;
        //public final String spacingRequirements;
        //public final Map fertilizerSchedule;

        public Plant(String id, String name, String soil, String sowingSeason, String seedTreatment, Integer cropDuration) {
            this.id = id;
            this.name = name;
            this.soil= soil;
            this.sowingSeason = sowingSeason;
            this.seedTreatment = seedTreatment;
            this.cropDuration = cropDuration;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public final class GrowthStages {
        public final Integer SowingToPlant;
        public final Integer FlowerInitiationToFlowering;
        public final Integer FloweringToFruit;
        public final Integer Harvesting;

        public GrowthStages(Integer SowingToPlant,
                     Integer FlowerInitiationToFlowering,
                     Integer FloweringToFruit,
                     Integer Harvesting) {
            this.SowingToPlant = SowingToPlant;
            this.FlowerInitiationToFlowering = FlowerInitiationToFlowering;
            this.FloweringToFruit= FloweringToFruit;
            this.Harvesting = Harvesting;
        }

    }
}
