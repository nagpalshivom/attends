package com.example.phoenix.attends;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by phoenix on 28/10/15.
 */
public class SetRemindersAfterBoot extends Service {

    Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        ArrayList<Course> coursesBoot;
        Log.d("myd", "SetRemindersAfterBoot onCreate => start function");
        context = this;
        try {
            FileInputStream fin = context.openFileInput("courses.ser");
            ObjectInputStream ois = new ObjectInputStream(fin);
            coursesBoot = (ArrayList<Course>) ois.readObject();
        } catch (Exception e) {
            Log.d("myd", "SetRemindersAfterBoot onCreate => " + e.toString());
            return;
        }
        Log.d("myd", "SetRemindersAfterBoot onCreate => object extracted from file");

        if(coursesBoot.size() > 0)
            if(coursesBoot.get(0).events.size() > 0)
                CourseEvent.context = context;

        for(int i = 0;i < coursesBoot.size();i++)
            for(int j = 0;j < coursesBoot.get(i).events.size();j++)
                coursesBoot.get(i).events.get(j).setReminder(coursesBoot.get(i).name);
        Log.d("myd", "SetRemindersAfterBoot onCreate => reminders set");
    }

    @Override
    public void onDestroy() {

    }

}
