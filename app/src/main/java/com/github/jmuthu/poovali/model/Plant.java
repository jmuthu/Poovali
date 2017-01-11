package com.github.jmuthu.poovali.model;

import android.content.Context;
import android.net.Uri;

import com.github.jmuthu.poovali.interfaces.DisplayableItem;
import com.github.jmuthu.poovali.utility.Helper;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class Plant implements Serializable, DisplayableItem {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    //Storing uri as string as it is not serializable
    private String imageUri;
    private EnumMap<GrowthStage, Integer> growthStageMap = new EnumMap<GrowthStage, Integer>(GrowthStage.class);
    private List<Batch> batchList = new LinkedList<>();
    //public final Map fertilizerSchedule;

    public Plant() {
    }

    public Plant(String id,
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

    public List<Batch> getBatchList() {
        return Collections.unmodifiableList(batchList);
    }

    public void setBatchList(List<Batch> batchList) {
        this.batchList = batchList;
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
        for (Batch batch : batchList) {
            if (date.compareTo(Helper.getZeroTimeDate(batch.getCreatedDate())) == 0) {
                return true;
            }
        }
        return false;
    }

    public Batch getLatestBatch() {
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

    public void addBatch(Batch batch) {
        batchList.add(0, batch);
        Collections.sort(batchList, new Batch.BatchDescendingComparator());
    }

    public void deleteBatch(Context context, int position) {
        Batch batch = batchList.get(position);
        batchList.remove(position);
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
}
