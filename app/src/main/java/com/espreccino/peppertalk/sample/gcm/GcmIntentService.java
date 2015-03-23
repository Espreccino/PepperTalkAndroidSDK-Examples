package com.espreccino.peppertalk.sample.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.espreccino.peppertalk.PepperTalk;
import com.espreccino.peppertalk.sample.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
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
            PepperTalk.getInstance(this).handleNotification(intent,
                    R.drawable.ic_stat_notification,
                    soundUri);
        } else {
            //Handle your own notification
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
