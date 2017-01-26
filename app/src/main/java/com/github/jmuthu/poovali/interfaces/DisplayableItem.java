package com.github.jmuthu.poovali.interfaces;

import android.net.Uri;

public interface DisplayableItem {
    int getId();

    String getName();

    Uri getImageUri();

    int getImageResourceId();
}
