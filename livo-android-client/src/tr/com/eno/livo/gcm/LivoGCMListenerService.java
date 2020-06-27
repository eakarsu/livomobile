package tr.com.eno.livo.gcm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import tr.com.eno.livo.R;
import tr.com.eno.livo.WebContainer;

public class LivoGCMListenerService extends GcmListenerService {

    private static final String TAG = "LivoGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        //if (from.startsWith("/topics/")) {
            // message received from some topic.
        //} else {
            // normal downstream message.
       // }

        this.sendRegularNotification(message);

    }

    private void sendRegularNotification(String message) {

        Log.d(TAG,"Showing incoming notification.");
        Intent intent = new Intent(this, WebContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setColor(Color.GRAY)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.icon))
                .setContentTitle("Livo")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}

