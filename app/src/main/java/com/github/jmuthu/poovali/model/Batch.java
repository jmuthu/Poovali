package com.github.jmuthu.poovali.model;

import android.content.Context;

import com.github.jmuthu.poovali.utility.Helper;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import static com.github.jmuthu.poovali.utility.Helper.getZeroTimeDate;

public class Batch implements Serializable, Helper.DisplayableItem {
    private static final long serialVersionUID = 1L;
    private String id;
    transient private PlantContent.Plant plant;
    private String plantId;
    private String name;
    private Date createdDate;
    private String description;
    private List<Event> eventsList = new LinkedList<>();

    public Batch() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PlantContent.Plant getPlant() {
        if (plant == null && plantId != null) {
            plant = PlantContent.getPlant(plantId);
        }
        return plant;
    }

    public void setPlant(PlantContent.Plant plant) {
        this.plant = plant;
        this.plantId = plant.getId();
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(eventsList);
    }

    public void setEvents(List<Event> eventsList) {
        this.eventsList = eventsList;
    }

    public void addEvent(Context context, Event event) {
        if (!eventsList.contains(event)) {
            eventsList.add(0, event);
            EventRepository.store(event);
        }
        PlantContent.saveItems(context);
    }

    public void deleteEvent(Context context, Event event) {
        eventsList.remove(event);
        EventRepository.delete(event);
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
        return (int) getDurationInDays() * 100 / getPlant().getCropDuration();
    }

    public PlantContent.GrowthStage getStage() {
        long dayCount = getDurationInDays();
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

    public int getDurationInDays() {
        long diff = getZeroTimeDate(Calendar.getInstance().getTime()).getTime() - getZeroTimeDate(createdDate).getTime();
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    @Override
    public String toString() {
        return name;
    }

    static class BatchDescendingComparator implements Comparator<com.github.jmuthu.poovali.model.Batch> {
        @Override
        public int compare(com.github.jmuthu.poovali.model.Batch b1, com.github.jmuthu.poovali.model.Batch b2) {
            return b2.getCreatedDate().compareTo(b1.getCreatedDate());
        }
    }

    static class BatchModifiedDescendingComparator implements Comparator<com.github.jmuthu.poovali.model.Batch> {
        @Override
        public int compare(com.github.jmuthu.poovali.model.Batch b1, com.github.jmuthu.poovali.model.Batch b2) {
            return b2.eventsList.get(0).getCreatedDate().compareTo(b1.eventsList.get(0).getCreatedDate());
        }
    }

    static class BatchNameComparator implements Comparator<com.github.jmuthu.poovali.model.Batch> {
        @Override
        public int compare(com.github.jmuthu.poovali.model.Batch b1, com.github.jmuthu.poovali.model.Batch b2) {
            return b1.getName().compareTo(b2.getName());
        }
    }
}