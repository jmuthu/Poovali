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
    public static final int DEFAULT_EVENT_TYPE_POSITION = EventType.HARVEST.ordinal();
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
                return "DeWeed";
            }
        },
        FERTILIZER {
            public String toString() {
                return "Fertilizer";
            }
        },
        HARVEST {
            public String toString() {
                return "Harvest";
            }
        },
        MICRO_NUTRIENTS {
            public String toString() {
                return "Micro nutrients";
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
        },
        SOW {
            public String toString() {
                return "Sow";
            }
        }

    }

    static class Event implements Serializable {
        static final long serialVersionUID = -1856133174987221772L;
        private Date createdDate;
        private EventType type;
        private int plantId;
        private String description;

        public int getPlantId() {
            return plantId;
        }

        public void setPlantId(int plantId) {
            this.plantId = plantId;
        }

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