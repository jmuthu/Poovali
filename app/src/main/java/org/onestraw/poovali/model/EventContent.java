package org.onestraw.poovali.model;

import android.content.Context;

import org.onestraw.poovali.utility.Helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventContent implements Serializable {
    public static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM);
    public static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.MEDIUM);
    private static final String EVENTS_FILE = "poovali_events.json";
    private static List<Event> ITEMS = new ArrayList<Event>();

    private static void initializeItems(Context context) {
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

    public static void addEvent(Context context, Event event) {
        ITEMS.add(0, event);
        saveItems(context);
    }

    public static void saveItems(Context context) {
        try {
            File file = new File(context.getFilesDir(), EVENTS_FILE);
            if (!file.isFile()) {
                file.createNewFile();
            }

            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(EventContent.ITEMS);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static List<Event> getItems(Context context) {
        if (ITEMS.isEmpty()) {
            initializeItems(context);
        }
        return ITEMS;
    }

    public enum BatchActivityType {
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
        WATER {
            public String toString() {
                return "Water";
            }
        }
    }

    public static abstract class Event implements Serializable, Helper.DisplayableItem {
        private static final long serialVersionUID = 1L;

        private String id;
        private Date createdDate;
        private String batchId;  // For serialization only
        transient private BatchContent.Batch batch;
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

        public BatchContent.Batch getBatch(Context context) {
            if (batch == null && batchId != null) {
                batch = BatchContent.getItemMap(context).get(batchId);
            }
            return batch;
        }

        public void setBatch(BatchContent.Batch batch) {
            this.batch = batch;
            this.batchId = batch.getId();
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

    public static class SowBatchEvent extends Event implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String NAME = "Sow";

        public String getName() {
            return NAME;
        }

        public String getImageName() {
            return Helper.getImageFileName(NAME);
        }
    }

    public static class BatchActivityEvent extends Event implements Serializable {
        private static final long serialVersionUID = 1L;
        private BatchActivityType type;

        public String getName() {
            return type.name();
        }

        public String getImageName() {
            return Helper.getImageFileName(type.name());
        }

        public BatchActivityType getType() {
            return type;
        }

        public void setType(BatchActivityType type) {
            this.type = type;
        }

    }
}