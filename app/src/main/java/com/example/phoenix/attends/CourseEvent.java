package com.example.phoenix.attends;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by phoenix on 18/10/15.
 */
public class CourseEvent implements Serializable {
    static Context context;
    static int nid;
    int reqCode;
    int day, hour, minute;

    CourseEvent(int day, int hour, int minute) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        Log.d("myd", "COURSEEVENT CONSTRUCTOR => event created on " + day + " " + hour + " : " + minute);
    }

    public void removeReminder() {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Log.d("myd", "COURSEEVENT removeReminder => reminder removed having request code " + reqCode);
    }

    public void setReminder(String nameOfCourse) {
        SharedPreferences sharedPref = context.getSharedPreferences("chilka", Context.MODE_PRIVATE);
        nid = sharedPref.getInt("nid", 0);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("name", nameOfCourse);
        intent.putExtra("hrs", hour);
        intent.putExtra("min", minute);
        intent.putExtra("nid", nid);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("nid", nid + 1);
        editor.commit();
        Log.d("myd", "COURSEEVENT setReminder => nid incremented to " + String.valueOf(nid + 1));
        Calendar firingCal = Calendar.getInstance();

        int dow = day + 2;
        if(dow == 8)
            dow = 1;

        firingCal.set(Calendar.HOUR_OF_DAY, hour);
        firingCal.set(Calendar.MINUTE, minute);
        firingCal.set(Calendar.SECOND, 0);
        firingCal.set(Calendar.MILLISECOND, 0);
        if(firingCal.get(Calendar.DAY_OF_WEEK) == dow) {
            Log.d("myd", "COURSEEVENT setReminder => day of week is same as today for event");
            if(firingCal.before(Calendar.getInstance()))
                firingCal.add(Calendar.DATE, 1);
            else {
                firingCal.add(Calendar.MINUTE, -10);
                if (firingCal.before(Calendar.getInstance())) {
                    Log.d("myd", "COURSEEVENT setReminder => instant reminder called");
                    long[] pattern = {0, 1200, 600, 1200, 600, 1200, 600, 1200, 600, 1200, 600, 1200, 600};
                    int notificationId = sharedPref.getInt("nid", 0);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    Notification notification = new NotificationCompat.Builder(context)
                            .setContentTitle("Class at " + String.valueOf(hour) + ":" + String.valueOf(minute) + " !")
                            .setContentText(nameOfCourse)
                            .setSmallIcon(android.R.drawable.ic_dialog_alert)
                            .setColor(ContextCompat.getColor(context, R.color.Red))
                            .setVibrate(pattern)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, HomeActivity.class), 0))
                            .build();

                    notificationManager.notify(notificationId, notification);
                    editor.putInt("nid", notificationId + 1);
                    editor.commit();
                    firingCal.add(Calendar.DATE, 1);
                }
                firingCal.add(Calendar.MINUTE, 10);
            }
        }
        firingCal.add(Calendar.MINUTE, -10);
        while(firingCal.get(Calendar.DAY_OF_WEEK) != dow) {
            Log.d("myd", "COURSEEVENT setReminder => " + String.valueOf(firingCal.get(Calendar.DAY_OF_WEEK)) + " | " + String.valueOf(dow));
            firingCal.add(Calendar.DATE, 1);
        }

        Log.d("myd", "COURSEEVENT setReminder => timeToFire : " + String.valueOf(firingCal.getTimeInMillis()));
        Log.d("myd", "COURSEEVENT setReminder => timeNow : " + String.valueOf(Calendar.getInstance().getTimeInMillis()));
        reqCode = sharedPref.getInt("reqCode", 0);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, reqCode, intent, 0);
        editor.putInt("reqCode", reqCode + 1);
        editor.commit();
        Log.d("myd", "COURSEEVENT setReminder => request code incremented to " + String.valueOf(reqCode + 1));
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, firingCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
        Log.d("myd", "COURSEEVENT setReminder => reminder set");
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        int notificationId;
        @Override
        public void onReceive(Context context, Intent intent) {
            long [] pattern = {0, 1200, 600, 1200, 600, 1200, 600, 1200, 600, 1200, 600, 1200, 600};
            notificationId = intent.getIntExtra("nid", 0);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle("Class at " + String.valueOf(intent.getIntExtra("hrs", 0)) + ":" + String.valueOf(intent.getIntExtra("min", 0)) + " !")
                    .setContentText(intent.getStringExtra("name"))
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setColor(ContextCompat.getColor(context, R.color.Red))
                    .setVibrate(pattern)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, HomeActivity.class), 0))
                    .build();

            notificationManager.notify(notificationId, notification);
        }
    }
}
