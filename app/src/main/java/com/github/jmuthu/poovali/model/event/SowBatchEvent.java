package com.github.jmuthu.poovali.model.event;

import com.github.jmuthu.poovali.utility.Helper;

import java.io.Serializable;

public class SowBatchEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String NAME = "Sow";

    @Override
    public String getName() {
        return NAME;
    }

    public String getImageName() {
        return Helper.getImageFileName(NAME);
    }
}
