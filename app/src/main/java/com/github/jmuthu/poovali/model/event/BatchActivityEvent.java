package com.github.jmuthu.poovali.model.event;

import android.net.Uri;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.utility.Helper;

import java.io.Serializable;

public class BatchActivityEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;
    private Type type;

    @Override
    public String getName() {
        return Helper.getLocalizedString(R.array.batch_activity_type, type.getValue());
    }

    @Override
    public Uri getImageUri() {
        return null;
    }

    public int getImageResourceId() {
        return Helper.getResourceIdFromName(type.name());
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        DEWEED(0),
        FERTILIZER(1),
        HARVEST(2),
        MICRO_NUTRIENTS(3),
        MULCH(4),
        PESTICIDE(5),
        PRUNE(6),
        REPLANT(7),
        WATER(8);

        private final int value;

        Type(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }
}
