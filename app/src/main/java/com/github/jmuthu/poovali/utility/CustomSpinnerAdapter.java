package com.github.jmuthu.poovali.utility;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jmuthu.poovali.R;
import com.github.jmuthu.poovali.interfaces.DisplayableItem;

import java.util.List;

public class CustomSpinnerAdapter<T extends DisplayableItem> extends ArrayAdapter<T> {

    public CustomSpinnerAdapter(Activity context, List<T> list) {
        super(context, 0, list);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        T item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.spinner_item, parent, false);
        }
        if (item != null) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.img);
            Helper.setImageSrc(imageView, item);

            TextView textView = (TextView) convertView.findViewById(R.id.txt);
            textView.setText(item.getName());
        }
        return convertView;
    }

    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);

    }
}
