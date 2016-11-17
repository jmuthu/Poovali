package org.onestraw.poovali.model;

import android.content.Context;

import org.onestraw.poovali.utility.Helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchContent implements Serializable {

    private static final String BATCH_FILE = "poovali_batch.json";
    private static final Map<String, Batch> ITEM_MAP = new HashMap<String, Batch>();
    private static final Map<String, Integer> PLANT_MAP = new HashMap<String, Integer>();
    private static List<Batch> ITEMS = new ArrayList<Batch>();

    private static void initializeItems(Context context) {
        try {
            File file = new File(context.getFilesDir(), BATCH_FILE);

            if (file.isFile()) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fin);
                ITEMS = (List<Batch>) ois.readObject();
                ois.close();
                for (Batch batch : ITEMS) {
                    ITEM_MAP.put(batch.getId(), batch);
                    Integer count = PLANT_MAP.get(batch.getPlantId());
                    PLANT_MAP.put(batch.getPlantId(), (count == null) ? 1 : count + 1);
                }
            } else {
                // Adding the garden batch to account for all plants
                Batch garden = new Batch("0", "", "Garden", new Date());
                ITEMS.add(garden);
                ITEM_MAP.put(garden.id, garden);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Batch> getItems(Context context) {
        if (ITEMS.isEmpty()) {
            initializeItems(context);
        }
        return ITEMS;
    }

    public static Map<String, Batch> getItemMap(Context context) {
        if (ITEM_MAP.isEmpty()) {
            initializeItems(context);
        }
        return ITEM_MAP;
    }

    public static Integer getNoOfItems(Context context, String plantId) {
        if (ITEMS.isEmpty()) {  // Check items as batch can be empty
            initializeItems(context);
        }
        return PLANT_MAP.get(plantId);
    }

    public static void addBatch(Context context, Batch batch) {
        try {
            File file = new File(context.getFilesDir(), BATCH_FILE);
            if (!file.isFile()) {
                file.createNewFile();
            }
            ITEMS.add(0, batch);

            ITEM_MAP.put(batch.id, batch);
            Integer count = PLANT_MAP.get(batch.getPlantId());
            PLANT_MAP.put(batch.getPlantId(), (count == null) ? 1 : count + 1);

            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(ITEMS);
            oos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Batch implements Serializable, Helper.DisplayableItem {
        private static final long serialVersionUID = 1L;
        private String id;
        private String plantId;
        private String name;
        private Date createdDate;

        public Batch() {
        }

        public Batch(String id, String plantId, String name, Date createdDate) {
            this.id = id;
            this.plantId = plantId;
            this.name = name;
            this.createdDate = createdDate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlantId() {
            return plantId;
        }

        public void setPlantId(String plantId) {
            this.plantId = plantId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
        }

        public String getImageName() {
            if (plantId.isEmpty()) { // For garden
                return Helper.getImageFileName(name);
            }
            return Helper.getImageFileName(PlantContent.getItemMap().get(plantId).getName());
        }

        @Override
        public String toString() {
            return name;
        }
    }
}