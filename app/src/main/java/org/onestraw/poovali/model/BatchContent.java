package org.onestraw.poovali.model;

import android.content.Context;
import android.util.Log;

import org.onestraw.poovali.model.PlantContent.Plant;
import org.onestraw.poovali.utility.Helper;
import org.onestraw.poovali.utility.MyExceptionHandler;

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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BatchContent implements Serializable {

    private static final String BATCH_FILE = "poovali_batch.json";
    private static final Map<String, Batch> ITEM_MAP = new HashMap<String, Batch>();
    private static final Map<String, LinkedList<Batch>> PLANT_MAP = new HashMap<String, LinkedList<Batch>>();
    private static List<Batch> ITEMS = new ArrayList<Batch>();
    private static List<Batch> EVENTS = new LinkedList<>();

    public static void initialize(Context context) {
        try {
            File file = new File(context.getFilesDir(), BATCH_FILE);

            if (file.isFile()) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fin);
                ITEMS = (List<Batch>) ois.readObject();
                ois.close();
                for (Batch batch : ITEMS) {
                    addBatchToMap(batch);
                }
            } else {
                // Adding the garden batch to account for all plants
                Batch garden = new Batch("0", "Garden", new Date(), null);
                ITEMS.add(garden);
                ITEM_MAP.put(garden.id, garden);
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.e(BatchContent.class.getName(), "Unable to read batch file", e);
            MyExceptionHandler.alertAndCloseApp(context, null);
        }
    }

    public static List<Batch> getItems() {
        return ITEMS;
    }

    public static Map<String, Batch> getItemMap() {
        return ITEM_MAP;
    }

    public static List<Batch> getBatchList(String plantId) {
        return PLANT_MAP.get(plantId);
    }

    public static Integer getNoOfItems(String plantId) {
        List<Batch> list = getBatchList(plantId);
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public static boolean isDuplicateBatch(Plant plant, Date date) {
        date = Helper.getZeroTimeDate(date);
        List<Batch> list = getBatchList(plant.getId());
        if (list == null) {
            return false;
        }
        for (Batch batch : list) {
            if (date.compareTo(Helper.getZeroTimeDate(batch.getCreatedDate())) == 0) {
                return true;
            }
        }
        return false;
    }


    public static List<NotificationContent> pendingActivities() {
        List<NotificationContent> notification = new ArrayList<>();
        if (PLANT_MAP.size() == 0) {
            return notification;
        }
        for (LinkedList<Batch> list : PLANT_MAP.values()) {
            Batch latestBatch = list.getLast();
            Integer dayCount = pendingSowDays(latestBatch);
            if (dayCount > 0) {
                notification.add(new NotificationContent(
                        "Sow " + latestBatch.getPlant().getName() + "!",
                        dayCount + (dayCount > 1 ? " days " : " day ") + "overdue"));
            } else if (dayCount == 0) {
                notification.add(new NotificationContent(
                        "Sow " + latestBatch.getPlant().getName() + " today!",
                        ""));
            }
        }
        return notification;
    }

    public static Batch getLastBatch(String plantId) {
        LinkedList<Batch> list = PLANT_MAP.get(plantId);
        if (list == null) {
            return null;
        }
        return list.getLast();
    }

    public static Integer pendingSowDays(String plantId) {
        Batch lastBatch = getLastBatch(plantId);
        if (lastBatch == null) {
            return null;
        }
        return pendingSowDays(lastBatch);
    }

    public static Integer pendingSowDays(Batch batch) {
        Long diff = (Calendar.getInstance().getTimeInMillis() -
                batch.getPlant().getNextSowingDate(batch.getCreatedDate()).getTime()) / (24 * 60 * 60 * 1000);
        return diff.intValue();
    }

    private static void addBatchToMap(Batch batch) {
        ITEM_MAP.put(batch.getId(), batch);
        if (batch.getPlant() != null) {
            LinkedList<Batch> list = PLANT_MAP.get(batch.getPlant().getId());
            if (list == null) {
                list = new LinkedList<Batch>();
            }
            list.add(batch);
            Collections.sort(list, new BatchComparator());
            PLANT_MAP.put(batch.getPlant().getId(), list);
        }
    }

    public static void addBatch(Context context, Batch batch) {
        ITEMS.add(0, batch);
        addBatchToMap(batch);
        saveItems(context);
    }

    public static void saveItems(Context context) {
        try {
            File file = new File(context.getFilesDir(), BATCH_FILE);
            if (!file.isFile()) {
                file.createNewFile();
            }
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(ITEMS);
            oos.close();

        } catch (IOException e) {
            Log.e(BatchContent.class.getName(), "Unable to save batch to file", e);
            MyExceptionHandler.alertAndCloseApp(context, null);
        }
    }

    public static class Batch implements Serializable, Helper.DisplayableItem {
        private static final long serialVersionUID = 1L;
        private String id;
        transient private Plant plant;
        private String plantId;
        private String name;
        private Date createdDate;
        private List<EventContent.Event> eventsList;

        public Batch() {
        }

        public Batch(String id, String name, Date createdDate, Plant plant) {
            this.id = id;
            this.plant = plant;
            if (plant != null) {
                this.plantId = plant.getId();
            }
            this.name = name;
            this.createdDate = createdDate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Plant getPlant() {
            if (plant == null && plantId != null) {
                plant = PlantContent.getItem(plantId);
            }
            return plant;
        }

        public void setPlant(Plant plant) {
            this.plant = plant;
            this.plantId = plant.getId();
        }

        public List<EventContent.Event> getEvents() {
            return eventsList;
        }

        public void setEvents(List<EventContent.Event> eventsList) {
            this.eventsList = eventsList;
        }

        public void addEvent(Context context, EventContent.Event event) {
            if (eventsList == null) {
                eventsList = new LinkedList<EventContent.Event>();
            }
            eventsList.add(0, event);
            saveItems(context);
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
            if (getPlant() == null) { // For garden
                return Helper.getImageFileName(name);
            }
            return Helper.getImageFileName(getPlant().getName());
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static class BatchComparator implements Comparator<Batch> {
        @Override
        public int compare(Batch b1, Batch b2) {
            return b2.getCreatedDate().compareTo(b1.getCreatedDate());
        }
    }
}
