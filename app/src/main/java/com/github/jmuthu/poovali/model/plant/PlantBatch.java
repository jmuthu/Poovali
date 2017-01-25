package com.github.jmuthu.poovali.model.plant;

import android.net.Uri;

import com.github.jmuthu.poovali.interfaces.DisplayableItem;
import com.github.jmuthu.poovali.model.event.Event;
import com.github.jmuthu.poovali.utility.Helper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import static com.github.jmuthu.poovali.utility.Helper.getStartOfDay;

public class PlantBatch implements Serializable, DisplayableItem {
    private static final long serialVersionUID = 1L;
    private int id;
    transient private Plant plant;
    private int plantId;
    private String name;
    private Date createdDate;
    private Date latestEventCreatedDate;
    private String description;
    transient private List<Event> eventsList = new LinkedList<>();

    public PlantBatch() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
        this.plantId = plant.getId();
    }

    public int getPlantId() {
        return plantId;
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(eventsList);
    }

    public void setEvents(List<Event> eventsList) {
        this.eventsList = eventsList;
    }

    public void addOrUpdateEvent(Event event) {
        if (!eventsList.contains(event)) {
            eventsList.add(0, event);
            event.setBatchId(id);
        }
        if (event.getCreatedDate().compareTo(latestEventCreatedDate) > 0) {
            latestEventCreatedDate = event.getCreatedDate();
        }
        Collections.sort(eventsList, new Event.EventModifiedDescendingComparator());
    }

    public void deleteEvent(Event event) {
        eventsList.remove(event);
    }

    public Event findEvent(String name, Date inputDate) {
        Date date = Helper.getStartOfDay(inputDate);
        for (Event event : eventsList) {
            if (event.getName().equals(name) &&
                    date.compareTo(Helper.getStartOfDay(event.getCreatedDate())) == 0) {
                return event;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return plant.getName();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        this.latestEventCreatedDate = createdDate;
    }

    public Date getLatestEventCreatedDate() {
        return this.latestEventCreatedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getImageUri() {
        return plant.getImageUri();
    }

    public int getImageResourceId() {
        return plant.getImageResourceId();
    }

    public int getProgress() {
        return getDurationInDays() * 100 / plant.getCropDuration();
    }

    public Plant.GrowthStage getStage() {
        long dayCount = getDurationInDays();
        EnumMap<Plant.GrowthStage, Integer> growthStageMap = plant.getGrowthStageMap();
        if (dayCount <= growthStageMap.get(Plant.GrowthStage.SEEDLING)) {
            return Plant.GrowthStage.SEEDLING;
        } else if (dayCount <= growthStageMap.get(Plant.GrowthStage.SEEDLING) +
                growthStageMap.get(Plant.GrowthStage.FLOWERING)) {
            return Plant.GrowthStage.FLOWERING;
        } else if (dayCount <= growthStageMap.get(Plant.GrowthStage.SEEDLING) +
                growthStageMap.get(Plant.GrowthStage.FLOWERING) +
                growthStageMap.get(Plant.GrowthStage.FRUITING)) {
            return Plant.GrowthStage.FRUITING;
        } else if (dayCount <= growthStageMap.get(Plant.GrowthStage.SEEDLING) +
                growthStageMap.get(Plant.GrowthStage.FLOWERING) +
                growthStageMap.get(Plant.GrowthStage.FRUITING) +
                growthStageMap.get(Plant.GrowthStage.RIPENING)) {
            return Plant.GrowthStage.RIPENING;
        }
        return Plant.GrowthStage.DORMANT;
    }

    public int getDurationInDays() {
        long diff = getStartOfDay(Calendar.getInstance().getTime()).getTime() - getStartOfDay(createdDate).getTime();
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        eventsList = new LinkedList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean sameIdentityAs(final PlantBatch other) {
        return other != null && this.getId() == other.getId();
    }

    static class BatchDescendingComparator implements Comparator<PlantBatch> {
        @Override
        public int compare(PlantBatch b1, PlantBatch b2) {
            return b2.getCreatedDate().compareTo(b1.getCreatedDate());
        }
    }

    static class BatchModifiedDescendingComparator implements Comparator<PlantBatch> {
        @Override
        public int compare(PlantBatch b1, PlantBatch b2) {
            return b2.getLatestEventCreatedDate().compareTo(b1.getLatestEventCreatedDate());
        }
    }

    static class BatchNameComparator implements Comparator<PlantBatch> {
        @Override
        public int compare(PlantBatch b1, PlantBatch b2) {
            return b1.getName().compareTo(b2.getName());
        }
    }
}