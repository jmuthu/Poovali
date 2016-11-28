package org.onestraw.poovali.model;

import org.onestraw.poovali.utility.Helper;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventContent {
    private static Map<String, Event> eventMap = new HashMap<>();

    public static void addToEventMap(Event event) {
        eventMap.put(event.getId(), event);
    }

    public static EventContent.Event getEvent(String eventId) {
        return eventMap.get(eventId);
    }

    public static void removeFromEventMap(Event event) {
        eventMap.remove(event.getId());
    }

    public static abstract class Event implements Serializable, Helper.DisplayableItem {
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
        private Type type;

        public String getName() {
            return type.toString();
        }

        public String getImageName() {
            return Helper.getImageFileName(type.name());
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public enum Type {
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
    }
}