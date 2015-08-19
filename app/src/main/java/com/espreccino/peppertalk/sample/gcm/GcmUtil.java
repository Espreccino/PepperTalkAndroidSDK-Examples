package com.espreccino.peppertalk.sample.gcm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.espreccino.peppertalk.PepperTalk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

public class GcmUtil {

    private static final String PREFERENCES = "com.espreccino.sample.gcm";

    private final static String TAG = "GcmUtil.class";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String GCM_SENDER_ID = "208698183965";

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public static boolean isGcmRegistered(Context context) {
        return !getRegistrationId(context).isEmpty();
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        } else {
            Log.i(TAG, "Reg Id " + registrationId);
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();

        //Register GCM Id on peppertalk
        PepperTalk.getInstance(context).registerGcm(regId);
    }

    public static void removeRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        prefs.edit().remove(PROPERTY_REG_ID).apply();
    }

    /**
     * Register this account/device pair within the server.
     *
     * @param context Current context
     */
    private static void register(final Context context, final String registrationId) {
        storeRegistrationId(context, registrationId);
    }

    /**
     * Unregister this account/device pair within the server.
     *
     * @param context Current context
     */
    public static void unregister(final Context context) {
        removeRegistrationId(context);
        //TODO unregister device on server
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static boolean checkGooglePlayServices(final Activity activity) {
        final int googlePlayServicesCheck = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        switch (googlePlayServicesCheck) {
            case ConnectionResult.SUCCESS:
                return true;
            case ConnectionResult.SERVICE_DISABLED:
            case ConnectionResult.SERVICE_INVALID:
            case ConnectionResult.SERVICE_MISSING: {
                break;
            }
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(googlePlayServicesCheck, activity, 0);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        activity.finish();
                    }
                });
                dialog.show();
        }
        return false;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    public static void registerInBackground(Context context) {
        new GCMRegisterTask(context.getApplicationContext()).execute(null, null, null);
    }

    /**
     * An object representing GCMRegisterTask
     * Register GCM in the background
     */
    private static class GCMRegisterTask extends AsyncTask<Void, Void, String> {

        Context context;

        GCMRegisterTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            String msg;
            try {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                String registrationId = gcm.register(GCM_SENDER_ID);
                msg = "Device registered, registration ID=" + registrationId;
                register(context, registrationId);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            super.onPostExecute(msg);
            Log.i(TAG, msg);
        }
    }

}
