package org.onestraw.poovali;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mike on 13/11/16.
 */

class EventContent implements Serializable {
    public static final String EVENTS_FILE = "poovali_events.json";
    static private List<Event> ITEMS = new ArrayList<Event>();

    static List<Event> getEventList(Context context) {
        try {
            File file = new File(context.getFilesDir(), EVENTS_FILE);

            if (file.isFile()) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fin);
                ITEMS = (List<Event>) ois.readObject();
                ois.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ITEMS;
    }

    static void addEvent(Context context, Event event) {
        try {
            File file = new File(context.getFilesDir(), EVENTS_FILE);
            if (!file.isFile()) {
                file.createNewFile();
            }
            ITEMS.add(event);
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(EventContent.ITEMS);
            oos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    enum EventType {
        DEWEED {
            public String toString() {
                return "De-Weed";
            }
        },
        FERTILIZER {
            public String toString() {
                return "Fertilizer";
            }
        },
        MICRO_NUTRIENTS {
            public String toString() {
                return "Micronutrients";
            }
        },
        MULCH {
            public String toString() {
                return "Mulch";
            }
        },
        PESTICIDE {
            public String toString() {
                return "Pesticide";
            }
        },
        PRUNE {
            public String toString() {
                return "Prune";
            }
        },
        REPLANT {
            public String toString() {
                return "Re-plant";
            }
        }
    }

    static class Event implements Serializable {
        static final long serialVersionUID = -1856133174987221772L;
        private Date createdDate;
        private EventType type;
        private String description;

        Date getCreatedDate() {
            return createdDate;
        }

        void setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
        }

        EventType getType() {
            return type;
        }

        void setType(EventType type) {
            this.type = type;
        }

        String getDescription() {
            return description;
        }

        void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}