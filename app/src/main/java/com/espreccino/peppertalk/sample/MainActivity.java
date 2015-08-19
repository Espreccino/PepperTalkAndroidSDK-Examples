package com.espreccino.peppertalk.sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.espreccino.peppertalk.Conversation;
import com.espreccino.peppertalk.Message;
import com.espreccino.peppertalk.PepperTalk;
import com.espreccino.peppertalk.PepperTalkError;
import com.espreccino.peppertalk.TalkCallback;
import com.espreccino.peppertalk.UserCallback;
import com.espreccino.peppertalk.sample.gcm.GcmUtil;

import org.json.JSONObject;

import java.util.List;


public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener,
        PepperTalk.ConnectionListener {

    private final String TAG = "MainActivity.class";

    public static final String[] USERS = {"Jon:j_android@getpeppertalk.com"};

    public static final String[] TOPICS = {"10011:Lets Ride!"};

    public static final String[] GROUPS = {"Talk Group 1#grp:talk_group_id_5"};

    private final static String PREF_USER = "com.espreccino.peppertalk.sample_user_login";
    private final static String PREF_USER_NAME = "com.espreccino.peppertalk.sample_user_name";

    UsersFragment mUsersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            String userId = getRegisteredUser();
            Fragment fragment;
            if (userId == null) {
                getSupportActionBar().hide();
                fragment = LoginFragment.getInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            } else {
                if (GcmUtil.checkGooglePlayServices(this)) {
                    if (!GcmUtil.isGcmRegistered(this)) {
                        GcmUtil.registerInBackground(this);
                    }
                }
                loadUserFragment(userId);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_logout: {
                PepperTalk.getInstance(this).logout(new TalkCallback<Void>() {
                    @Override
                    public void onSuccess(Void object) {
                        getSupportActionBar().hide();
                        Fragment fragment = LoginFragment.getInstance();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, fragment)
                                .commit();
                    }

                    @Override
                    public void onFail(PepperTalkError error) {

                    }
                });
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        if (mUsersFragment != null) {
            mUsersFragment.saveDataToSharedPrefs(getSharedPrefs());
        }
        super.onPause();
    }

    @Override
    public void onConnecting(int i) {
        Log.d(TAG, "Connection Status " + i);
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "Connected");
    }

    @Override
    public void onConnectionFailed(PepperTalkError error) {
        error.printStackTrace();
    }

    private void dumpConversations() {
        List<Conversation> conversations = PepperTalk.getInstance(this).getAllConversationsList();
        for (Conversation conversation : conversations) {
            Log.d(TAG, conversation.toString());
        }
        conversations = PepperTalk.getInstance(this).getConversationsByTopicId("10011");
        for (Conversation conversation : conversations) {
            Log.d(TAG, conversation.toString());
        }
        conversations = PepperTalk.getInstance(this).getConversationsByParticipantId("i@pepper.com");
        for (Conversation conversation : conversations) {
            Log.d(TAG, conversation.toString());
        }
        conversations = PepperTalk.getInstance(this).getConversationsByParticipantId("grp:talk_group_id_4");
        for (Conversation conversation : conversations) {
            Log.d(TAG, conversation.toString());
        }
    }

    @Override
    public void onLogin(String name, String userId) {
        registerUser(userId);
        saveUserName(name);
        loadUserFragment(userId);
    }

    private void loadUserFragment(String userId) {
        if (userId != null) {
            getSupportActionBar().show();
            setTitle("PepperTalk");
            if (mUsersFragment == null) {
                mUsersFragment = UsersFragment.getInstance(getRegisteredUser(), getUserName());
            }
            mUsersFragment.loadDataFromPrefs(getSharedPrefs());
            ((PepperTalkSample) getApplicationContext()).initPepperTalk(userId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, mUsersFragment)
                    .commit();
            //dumpConversations();
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private boolean isUserRegistered() {
        String userId = getSharedPrefs()
                .getString(PREF_USER, null);
        return userId != null;
    }

    private String getRegisteredUser() {
        return getSharedPrefs()
                .getString(PREF_USER, null);
    }

    private void registerUser(String userId) {
        getSharedPrefs()
                .edit()
                .putString(PREF_USER, userId)
                .apply();
    }

    private void saveUserName(String userName) {
        getSharedPrefs()
                .edit()
                .putString(PREF_USER_NAME, userName)
                .apply();
    }

    public String getUserName() {
        return getSharedPrefs().getString(PREF_USER_NAME, null);
    }

    private SharedPreferences getSharedPrefs() {
        return getSharedPreferences("com.esp", MODE_PRIVATE);
    }

}
