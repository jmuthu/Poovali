package com.github.jmuthu.poovali;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.github.jmuthu.poovali.model.Notification;
import com.github.jmuthu.poovali.model.Plant;
import com.github.jmuthu.poovali.model.PlantRepository;

import java.util.ArrayList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    private static int notificationID = 1;

    final static String ACTIVITY_GROUP = "activity_group";

    public static List<Notification> pendingActivities() {
        List<Notification> notification = new ArrayList<>();
        if (PlantRepository.findAll().size() == 0) {
            return notification;
        }
        for (Plant plant : PlantRepository.findAll()) {
            if (plant.getBatchList().isEmpty()) {
                continue;
            }
            Integer dayCount = plant.pendingSowDays();
            if (dayCount > 0) {
                notification.add(new Notification(
                        "Sow " + plant.getName() + "!",
                        dayCount + (dayCount > 1 ? " days " : " day ") + "overdue"));
            } else if (dayCount == 0) {
                notification.add(new Notification(
                        "Sow " + plant.getName() + " today!",
                        ""));
            }
        }
        return notification;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long when = System.currentTimeMillis();
        //NotificationManager notificationManager = (NotificationManager) context
        //        .getSystemService(NOTIFICATION_SERVICE);
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Set the intent that will fire when the user taps the notification.
        //Intent resultIntent = new Intent(this, ResultActivity.class);
        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
        //        resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        /*List<String> sowVegetables = BatchRepository.pendingActivities();
        int notificationCount = sowVegetables.size();
        if (notificationCount > 0) {
            String title = notificationCount == 1 ? notificationCount + " pending Sow activity" :
                    notificationCount + " pending Sow activities";

            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
            for (String s : sowVegetables) {
                style.addLine(s);
            }
            style.setSummaryText(title);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.notification);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.watering_can));
            //builder.setGroupSummary(true);
            //builder.setGroup(ACTIVITY_GROUP);
            builder.setContentTitle(title);
            builder.setStyle(style);
            //builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            //builder.setSound(alarmSound);

            notificationManager.notify(++notificationID, builder.build());
        }*/

        for (Notification content : pendingActivities()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.notification);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.watering_can));
            builder.setContentTitle(content.title);
            builder.setContentText(content.text);
            builder.setAutoCancel(true);
            //builder.setSubText("Only 10 days left for harvesting!");
            //builder.setContentIntent(pendingIntent);
            //builder.setSound(alarmSound);
            //builder.setGroup(ACTIVITY_GROUP);
            notificationManager.notify(++notificationID, builder.build());
        }
    }

}