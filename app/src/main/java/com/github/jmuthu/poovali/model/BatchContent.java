package com.github.jmuthu.poovali.model;

import android.content.Context;

import com.github.jmuthu.poovali.model.PlantContent.Plant;
import com.github.jmuthu.poovali.utility.Helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BatchContent {

    private static Map<String, Batch> batchMap = new HashMap<>();

    public static Batch getBatch(String batchId) {
        return batchMap.get(batchId);
    }

    public static void addToBatchMap(Batch batch) {
        batchMap.put(batch.getId(), batch);
        if (batch.getEvents() == null) {
            return;
        }
        for (EventContent.Event event : batch.getEvents()) {
            EventContent.addToEventMap(event);
        }
    }

    public static void removeFromBatchMap(Batch batch) {
        batchMap.remove(batch.getId());
    }

    public static List<Batch> getBatchList() {
        return getBatchList(false);
    }

    public static List<Batch> getBatchList(boolean sortByEvents) {
        ArrayList<Batch> result = new ArrayList<>(batchMap.values());
        if (sortByEvents) {
            Collections.sort(result, new Batch.BatchModifiedDescendingComparator());
        }
        return Collections.unmodifiableList(result);
    }


    public static class Batch implements Serializable, Helper.DisplayableItem {
        private static final long serialVersionUID = 1L;
        private String id;
        transient private Plant plant;
        private String plantId;
        private String name;
        private Date createdDate;
        private String description;
        private List<EventContent.Event> eventsList = new LinkedList<>();

        public Batch() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Plant getPlant() {
            if (plant == null && plantId != null) {
                plant = PlantContent.getPlant(plantId);
            }
            return plant;
        }

        public void setPlant(Plant plant) {
            this.plant = plant;
            this.plantId = plant.getId();
        }

        public List<EventContent.Event> getEvents() {
            return Collections.unmodifiableList(eventsList);
        }

        public void setEvents(List<EventContent.Event> eventsList) {
            this.eventsList = eventsList;
        }

        public void addEvent(Context context, EventContent.Event event) {
            if (!eventsList.contains(event)) {
                eventsList.add(0, event);
                EventContent.addToEventMap(event);
            }
            PlantContent.saveItems(context);
        }

        public void deleteEvent(Context context, EventContent.Event event) {
            eventsList.remove(event);
            EventContent.removeFromEventMap(event);
            PlantContent.saveItems(context);
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageName() {
            if (getPlant() == null) { // For garden
                return Helper.getImageFileName(name);
            }
            return Helper.getImageFileName(getPlant().getName());
        }

        public int getProgress() {
            long diff = Calendar.getInstance().getTimeInMillis() - createdDate.getTime();
            long dayCount = (long) diff / (24 * 60 * 60 * 1000);
            return (int) dayCount * 100 / getPlant().getCropDuration();
        }

        public PlantContent.GrowthStage getStage() {
            long diff = Calendar.getInstance().getTimeInMillis() - createdDate.getTime();
            long dayCount = (long) diff / (24 * 60 * 60 * 1000);
            EnumMap<PlantContent.GrowthStage, Integer> growthStageMap = getPlant().getGrowthStageMap();
            if (dayCount <= growthStageMap.get(PlantContent.GrowthStage.SEEDLING)) {
                return PlantContent.GrowthStage.SEEDLING;
            } else if (dayCount <= growthStageMap.get(PlantContent.GrowthStage.SEEDLING) +
                    growthStageMap.get(PlantContent.GrowthStage.FLOWERING)) {
                return PlantContent.GrowthStage.FLOWERING;
            } else if (dayCount <= growthStageMap.get(PlantContent.GrowthStage.SEEDLING) +
                    growthStageMap.get(PlantContent.GrowthStage.FLOWERING) +
                    growthStageMap.get(PlantContent.GrowthStage.FRUITING)) {
                return PlantContent.GrowthStage.FRUITING;
            } else if (dayCount <= growthStageMap.get(PlantContent.GrowthStage.SEEDLING) +
                    growthStageMap.get(PlantContent.GrowthStage.FLOWERING) +
                    growthStageMap.get(PlantContent.GrowthStage.FRUITING) +
                    growthStageMap.get(PlantContent.GrowthStage.RIPENING)) {
                return PlantContent.GrowthStage.RIPENING;
            }
            return PlantContent.GrowthStage.DORMANT;
        }

        @Override
        public String toString() {
            return name;
        }

        static class BatchDescendingComparator implements Comparator<Batch> {
            @Override
            public int compare(Batch b1, Batch b2) {
                return b2.getCreatedDate().compareTo(b1.getCreatedDate());
            }
        }

        static class BatchModifiedDescendingComparator implements Comparator<Batch> {
            @Override
            public int compare(Batch b1, Batch b2) {
                return b2.eventsList.get(0).getCreatedDate().compareTo(b1.eventsList.get(0).getCreatedDate());
            }
        }
    }
}
