package com.example.phoenix.attends;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by phoenix on 28/10/15.
 */
public class BootCompleteEventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SetRemindersAfterBoot.class));
        Log.d("myd", "BootCompleteEventReceiver onReceive => service started");
    }
}
