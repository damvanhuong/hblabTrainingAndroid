package com.esp.weather.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.util.Log;

import com.esp.weather.R;
import com.esp.weather.data.network.models.HourForecast;
import com.esp.weather.ui.MainActivity;

import java.util.Calendar;
import java.util.List;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        Intent alertIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.warning))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }

    public static void scheduleAlert(Context context, List<HourForecast> list) {
        for (HourForecast element : list) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            if (element.getDt() * 1000 > Calendar.getInstance().getTimeInMillis()) {
                boolean rain = element.getWeather().get(0).getMain().equals("Rain");
                double temp = element.getMain().getTemp() - 273;
                if (rain) {
                    intent.putExtra("message", "Có mưa");
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, element.getDt()*1000 - 900000, pendingIntent);
                } else if (temp > 30) {
                    intent.putExtra("message", "Nhiệt độ tăng cao");
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, element.getDt()*1000 - 900000, pendingIntent);
                } else if (temp < 28) {
                    intent.putExtra("message", "Nhiệt độ thấp");
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, element.getDt()*1000 - 900000, pendingIntent);
                }
                long mi = element.getDt()*1000 - 900000;
                Log.d("AlarmManager", mi + "");
            }
        }
    }
}
