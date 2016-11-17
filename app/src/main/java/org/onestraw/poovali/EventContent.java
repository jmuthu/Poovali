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

class EventContent implements Serializable {
    private static final String EVENTS_FILE = "poovali_events.json";
    private static List<Event> ITEMS = new ArrayList<Event>();

    static void initializeItems(Context context) {
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
    }

    static void addEvent(Context context, Event event) {
        try {
            File file = new File(context.getFilesDir(), EVENTS_FILE);
            if (!file.isFile()) {
                file.createNewFile();
            }
            ITEMS.add(0,event);
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(EventContent.ITEMS);
            oos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static List<Event> getItems(Context context) {
        if (ITEMS.isEmpty()) {
            initializeItems(context);
        }
        return ITEMS;
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
        },
        WATER {
            public String toString() {
                return "Water";
            }
        }

    }

    static class Event implements Serializable {
        static final long serialVersionUID = 1L;

        private String id;
        private Date createdDate;
        private EventType type;
        private String batchId;
        private String description;

        String getId() {
            return id;
        }

        void setId(String eventId) {
            this.id = eventId;
        }

        String getBatchId() {
            return batchId;
        }

        void setBatchId(String batchId) {
            this.batchId = batchId;
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