package org.onestraw.poovali;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mike on 13/11/16.
 */

class GardenEvent implements Serializable {
    static final long serialVersionUID = -1856133174987221772L;
    public static final List<GardenEvent> items = new ArrayList<GardenEvent>();

    public enum EventType {

        MICRO_NUTRIENTS {
            public String toString() {
                return "Micronutrients";
            }
        },

        FERTILIZER {
            public String toString() {
                return "Fertilizer";
            }
        },

        PESTICIDE {
            public String toString() {
                return "Pesticide";
            }
        }

    }

    private Date createdDate;
    private EventType type;
    private String description;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


        /*public GardenEvent() {
            this.createdDate = new Date();
            this.type = 0;
            this.description = "Test";
        }*/

    @Override
    public String toString() {
        return new StringBuffer(" Description : ")
                .append(this.description)
                .append(" Type : ")
                .append(this.type).toString();
    }
}