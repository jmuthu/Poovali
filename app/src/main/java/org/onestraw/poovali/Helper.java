package org.onestraw.poovali;

/**
 * Created by mike on 16/11/16.
 */
public class Helper {
    public static String getImageFileName(String name)
    {
        return name.toLowerCase().replace(' ', '_').replaceAll("\\W", "");
    }
}
