package org.onestraw.poovali;

class Helper {
    static final String DETAIL_IMAGE_SUFFIX = "_detail";

    static String getImageFileName(String name)
    {
        return name.toLowerCase().replace(' ', '_').replaceAll("\\W", "");
    }

    interface DisplayableItem {
        String getId();

        String getName();

        String getImageName();
    }
}
