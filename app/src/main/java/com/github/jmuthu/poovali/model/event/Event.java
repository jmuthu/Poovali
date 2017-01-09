package com.github.jmuthu.poovali.model.event;

import com.github.jmuthu.poovali.utility.Helper;

import java.io.Serializable;
import java.util.Date;

public abstract class Event implements Serializable, Helper.DisplayableItem {
    private static final long serialVersionUID = 1L;

    private String id;
    private Date createdDate;
    private String description;

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

    @Override
    public String toString() {
        return getName();
    }
}
