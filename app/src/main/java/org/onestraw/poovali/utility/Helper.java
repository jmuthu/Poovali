package org.onestraw.poovali.utility;

public class Helper {
    public static final String DETAIL_IMAGE_SUFFIX = "_detail";
    public static final String ARG_IS_SOW_ACTIVITY = "IS_SOW_ACTIVITY";
    public static final String ARG_EVENT_ID = "EVENT_ID";
    public static final String ARG_PLANT_ID = "PLANT_ID";

    public static String getImageFileName(String name)
    {
        return name.toLowerCase().replace(' ', '_').replaceAll("\\W", "");
    }

    public interface DisplayableItem {
        String getId();
        String getName();
        String getImageName();
    }
}
