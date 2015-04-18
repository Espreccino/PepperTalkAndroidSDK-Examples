package com.espreccino.peppertalk.sample;

import android.app.Application;

import com.espreccino.peppertalk.PepperTalk;
import com.espreccino.peppertalk.PepperTalkError;

/**
 * ${CLASS_NAME} com.espreccino.peppertalk.sample.
 */
public class PepperTalkSample extends Application implements
        PepperTalk.ConnectionListener  {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void initPepperTalk(String userId) {
        String clientId = getString(R.string.client_id);
        String clientSecret = getString(R.string.client_secret);
        PepperTalk.getInstance(this)
                .init(clientId,
                        clientSecret,
                        userId)
                .connectionListener(this)
                .connect();
    }

    @Override
    public void onConnecting(int i) {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onConnectionFailed(PepperTalkError pepperTalkError) {

    }
}
