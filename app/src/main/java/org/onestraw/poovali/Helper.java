package org.onestraw.poovali;

class Helper {
    static final String SMALL_IMAGE_SUFFIX = "_small";

    static String getImageFileName(String name)
    {
        return name.toLowerCase().replace(' ', '_').replaceAll("\\W", "");
    }
}
