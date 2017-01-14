package com.github.jmuthu.poovali.model.event;

import android.net.Uri;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public abstract class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Date createdDate;
    private String description;
    private String batchId;

    public String getId() {
        return id;
    }

    public void setId(String eventId) {
        this.id = eventId;
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

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Override
    public String toString() {
        return getName();
    }

    public abstract String getName();

    public Uri getImageUri() {
        return null;
    }

    public static class EventModifiedDescendingComparator implements Comparator<Event> {
        @Override
        public int compare(Event b1, Event b2) {
            return b2.getCreatedDate().compareTo(b1.getCreatedDate());
        }
    }
}
