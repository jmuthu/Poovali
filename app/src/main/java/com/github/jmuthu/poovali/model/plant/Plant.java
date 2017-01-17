package com.github.jmuthu.poovali.model.plant;

import android.net.Uri;

import com.github.jmuthu.poovali.interfaces.DisplayableItem;
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

public class Plant implements Serializable, DisplayableItem {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    //Storing uri as string as it is not serializable
    private String imageUri;
    private EnumMap<GrowthStage, Integer> growthStageMap = new EnumMap<GrowthStage, Integer>(GrowthStage.class);
    transient private List<PlantBatch> plantBatchList = new LinkedList<>();
    //public final Map fertilizerSchedule;

    public Plant() {
    }

    public Plant(int id,
                 String name,
                 Uri imageUri,
                 Integer seedling,
                 Integer flowering,
                 Integer fruiting,
                 Integer ripening) {
        this.id = id;
        this.name = name;
        if (imageUri != null) {
            this.imageUri = imageUri.toString();
        }
        this.growthStageMap.put(GrowthStage.SEEDLING, seedling);
        this.growthStageMap.put(GrowthStage.FLOWERING, flowering);
        this.growthStageMap.put(GrowthStage.FRUITING, fruiting);
        this.growthStageMap.put(GrowthStage.RIPENING, ripening);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return name;
    }

    public Integer getCropDuration() {
        Integer cropDuration = 0;
        for (Integer value : growthStageMap.values()) {
            cropDuration += value;
        }
        return cropDuration;
    }

    public EnumMap<GrowthStage, Integer> getGrowthStageMap() {
        return growthStageMap;
    }

    public void setGrowthStageMap(EnumMap<GrowthStage, Integer> growthStageMap) {
        this.growthStageMap = growthStageMap;
    }

    public List<PlantBatch> getPlantBatchList() {
        return Collections.unmodifiableList(plantBatchList);
    }

    public void setPlantBatchList(List<PlantBatch> plantBatchList) {
        this.plantBatchList = plantBatchList;
    }

    public PlantBatch findBatch(Date inputDate) {
        Date date = Helper.getStartOfDay(inputDate);
        for (PlantBatch plantBatch : plantBatchList) {
            if (date.compareTo(Helper.getStartOfDay(plantBatch.getCreatedDate())) == 0) {
                return plantBatch;
            }
        }
        return null;
    }

    public Uri getImageUri() {
        if (this.imageUri != null) {
            return Uri.parse(this.imageUri);
        }
        return null;
    }

    public void setImageUri(Uri imageUri) {
        if (imageUri != null) {
            this.imageUri = imageUri.toString();
        }
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

    public PlantBatch getLatestBatch() {
        if (plantBatchList.isEmpty()) {
            return null;
        }
        return plantBatchList.get(0);
    }

    public Integer pendingSowDays() {
        if (getLatestBatch() == null) {
            return null;
        }
        Long diff = (Calendar.getInstance().getTimeInMillis() -
                getNextSowingDate(getLatestBatch().getCreatedDate()).getTime()) / (24 * 60 * 60 * 1000);
        return diff.intValue();
    }

    public void addOrUpdatePlantBatch(PlantBatch plantBatch) {
        if (!plantBatchList.contains(plantBatch)) {
            plantBatchList.add(0, plantBatch);
            plantBatch.setPlant(this);
        }
        Collections.sort(plantBatchList, new PlantBatch.BatchDescendingComparator());
    }

    public void deleteBatch(PlantBatch plantBatch) {
        plantBatchList.remove(plantBatch);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        plantBatchList = new LinkedList<>();
    }

    public boolean sameIdentityAs(final Plant other) {
        return other != null && this.getId() == other.getId();
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

    static class PlantNameComparator implements Comparator<Plant> {
        @Override
        public int compare(Plant p1, Plant p2) {
            return p1.getName().compareTo(p2.getName());
        }
    }
}
