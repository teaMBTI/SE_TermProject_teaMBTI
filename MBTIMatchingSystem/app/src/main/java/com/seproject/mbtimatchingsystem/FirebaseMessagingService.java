package com.seproject.mbtimatchingsystem;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";
    private String msg, title;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived");
        title = remoteMessage.getNotification().getTitle();
        msg = remoteMessage.getNotification().getBody();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent = new Intent(this, ListCourseRoom.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //푸시 알림 눌렀을때 행위
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ListCourseRoom.class), 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.mbti_team)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{1, 1000});

            notificationManager.notify(0, mBuilder.build());
            mBuilder.setContentIntent(contentIntent);
        }
    }


