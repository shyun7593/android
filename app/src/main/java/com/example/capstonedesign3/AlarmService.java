package com.example.capstonedesign3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmService extends MainActivity {
    public void Alarm()
    {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent operation = PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, operation);
        } else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+60000, operation);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+60000, operation);
        }
    }
}
