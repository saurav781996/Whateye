package com.familyon.server;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.familyon.HomeActivity;
import com.example.wstatsapp.R;
import com.familyon.PremiumPlansctivity;
import com.familyon.SPHelpher.SharedData;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import androidx.core.app.NotificationCompat;

//import android.support.v4.app.NotificationCompat;

/**
 * Created by android on 18/4/17.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static int NOTIFICATION_ID = 1;
    boolean canShowNotification = false;
    boolean premium=true;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            JSONObject jsonObject ;
            if (remoteMessage.getData().containsKey("data"))
                jsonObject = new JSONObject(remoteMessage.getData().get("data"));
            else
                jsonObject = new JSONObject(remoteMessage.getData());



            if (jsonObject.getString("status").toLowerCase().equals("offline") && SharedData.getOfflineNotification(this) == true) {
                canShowNotification = true;
            } else if (jsonObject.getString("status").toLowerCase().equals("online") && SharedData.getOnlineNotification(this) == true) {
                canShowNotification = true;
            }
            else if (jsonObject.getString("status").toLowerCase().equals("trial") ) {
                canShowNotification = true;
                premium=false;
            }
            else {
                canShowNotification = false;
            }

            if (canShowNotification) {
                int num = ++NOTIFICATION_ID;
                Log.d("msg", "onMessageReceived: " + remoteMessage.getData().get("data"));
                Intent intent;
                if (premium) intent = new Intent(this, HomeActivity.class);
                else intent=new Intent(this, PremiumPlansctivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                String channelId = "Default";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)

                        //.setContentTitle(jsonObject.getString("title"))
                        .setContentText(jsonObject.getString("body"))
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true).setContentIntent(pendingIntent);

//                if (SharedData.getNotificationVibrate(this) == true) {
//                    builder.setDefaults(DEFAULT_VIBRATE | FLAG_SHOW_LIGHTS);
//                }
                //if (SharedData.getNotificationSound(this) == true) {
                Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.notification_sound);
                builder.setSound(sound);

                //}

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_HIGH);

                    /*int importance = manager.getImportance();
                    boolean soundAllowed = importance < 0 || importance >= NotificationManager.IMPORTANCE_DEFAULT;
                    Log.v("djfdslkfjdlkf", String.valueOf(soundAllowed));*/

                   /* boolean soundAllowed = channel.getImportance() >= NotificationManager.IMPORTANCE_DEFAULT;
                    Log.v("djfdslkfjdlkf", String.valueOf(soundAllowed));*/

                    AudioAttributes audioAttributes = new AudioAttributes.Builder()

                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();

                    channel.enableLights(true);
                    channel.setLightColor(Color.RED);

//                    if (SharedData.getNotificationVibrate(this) == true) {
                        channel.enableVibration(true);
//                        //channel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;
//                    }

                    //if (SharedData.getNotificationSound(this) == true){
                    channel.setSound(sound, audioAttributes);
                    //}
                    builder.setChannelId(channelId);

                    assert manager != null;


                    manager.createNotificationChannel(channel);
                }

                manager.notify(num, builder.build());
            }
        } catch (Exception ex) {

        }

    }


}
