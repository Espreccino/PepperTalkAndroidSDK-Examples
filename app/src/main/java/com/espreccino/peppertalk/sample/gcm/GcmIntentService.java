package com.espreccino.peppertalk.sample.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.espreccino.peppertalk.PepperTalk;
import com.espreccino.peppertalk.sample.MainActivity;
import com.espreccino.peppertalk.sample.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Sample GCM Intent Service to handle GCM notifications
 */
public class GcmIntentService extends IntentService {

    private static final String TAG = "GCMIntentService.class";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public GcmIntentService() {
        super("SampleGcmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String messageType = GoogleCloudMessaging.getInstance(this).getMessageType(intent);

        if (PepperTalk.getInstance(this).isNotificationFromPepperTalk(intent)) {

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            PepperTalk.NotificationBuilder builder = new PepperTalk.NotificationBuilder();
            builder.notificationStatIcon(R.drawable.ic_stat_notification);
            builder.soundUri(soundUri);
            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
            builder.taskStackBuilder(TaskStackBuilder.create(getApplicationContext())
                    .addNextIntentWithParentStack(intent1));

            PepperTalk.getInstance(this)
                    .handleNotification(intent, builder);
        } else {
            //Handle your own notification
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
