package com.github.jmuthu.poovali.utility;


import android.content.Context;
import android.util.Log;

import com.github.jmuthu.poovali.model.PlantRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileRepository {
    public static String BATCH_FILE = "poovali_batch.dat";

    public static Context context = null;

    public static Object readAll() {
        Object result = null;
        try {
            File file = new File(context.getFilesDir(), BATCH_FILE);
            if (file.isFile()) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fin);
                result = ois.readObject();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.e(PlantRepository.class.getName(), "Unable to read data file", e);
            MyExceptionHandler.alertAndCloseApp(context, null);
        }
        return result;
    }

    public static void writeAll(Object data) {
        try {
            File file = new File(context.getFilesDir(), BATCH_FILE);
            if (!file.isFile()) {
                file.createNewFile();
            }
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(data);
            oos.close();
        } catch (IOException e) {
            Log.e(FileRepository.class.getName(), "Unable to save data to file", e);
            MyExceptionHandler.alertAndCloseApp(context, null);
        }
    }
}
