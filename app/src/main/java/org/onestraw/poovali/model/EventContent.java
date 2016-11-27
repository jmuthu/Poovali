package org.onestraw.poovali.model;

import org.onestraw.poovali.utility.Helper;

import java.io.Serializable;
import java.util.Date;

public class EventContent implements Serializable {

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

        public BatchContent.Batch getBatch() {
            if (batch == null && batchId != null) {
                batch = BatchContent.getItemMap().get(batchId);
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
            return type.toString();
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