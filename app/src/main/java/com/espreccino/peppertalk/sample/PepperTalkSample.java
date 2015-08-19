package com.espreccino.peppertalk.sample;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.util.Base64;
import android.util.Log;

import com.espreccino.peppertalk.PepperTalk;
import com.espreccino.peppertalk.PepperTalkError;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PepperTalk Sample Application
 */
public class PepperTalkSample extends Application implements
        PepperTalk.ConnectionListener {

    private static final String TAG = "PepperTalkSample";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void initPepperTalk(String userId) {
        String clientId = getString(R.string.client_id);
        String clientSecret = getString(R.string.client_secret);

        //Initialise PepperTalk
        PepperTalk.getInstance(this)
                .init(clientId,
                        clientSecret,
                        userId)
                .connectionListener(this)
                .connect();

        // Set UI preferences
        PepperTalk.getInstance(this)
                .getUIPreferences()
                .enableImages(true)
                .enableLocations(true)
                .theme(R.style.Theme_Custom)
                .set();

        //Intent with activity you want to return from chat
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Default notification ringtone
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PepperTalk.NotificationBuilder builder = new PepperTalk.NotificationBuilder();
        builder.notificationStatIcon(R.drawable.ic_stat_notification);
        builder.soundUri(soundUri);
        builder.taskStackBuilder(TaskStackBuilder.create(getApplicationContext())
                .addNextIntentWithParentStack(intent));

        //Enable In app notifications
        PepperTalk.getInstance(this).enabledInAppNotifications(builder);
    }

    @Override
    public void onConnecting(int i) {
        Log.i(TAG, "Connection status " + i);
    }

    @Override
    public void onConnected() {
        Log.i(TAG, "Connected");
    }

    @Override
    public void onConnectionFailed(PepperTalkError pepperTalkError) {
        Log.e(TAG, "Connection Failed", pepperTalkError);
    }
}
