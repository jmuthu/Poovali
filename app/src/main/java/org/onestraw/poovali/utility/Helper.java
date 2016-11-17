package org.onestraw.poovali.utility;

public class Helper {
    public static final String DETAIL_IMAGE_SUFFIX = "_detail";

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
