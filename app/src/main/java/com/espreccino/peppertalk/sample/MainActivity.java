package com.espreccino.peppertalk.sample;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.espreccino.peppertalk.PepperTalk;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements LoginFragment.LoginFragmentListener,
        PepperTalk.ConnectionListener {

    static final String USER_1 = "user_android_1@getpeppertalk.com";
    static final String USER_2 = "user_android_2@getpeppertalk.com";

    static String[] mUsers = {USER_1, USER_2};
    private final static String PREF_USER = "com.espreccino.peppertalk.sample_user_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            String userId = getRegisteredUser();
            Fragment fragment;
            if (userId == null) {
                fragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            } else {
                initPepperTalk(userId);
            }
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnecting(int i) {
        
    }

    @Override
    public void onConnected() {
        Log.d("MainActivity", "Connected...");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, UsersFragment.getInstance(getRegisteredUser()))
                .commit();
    }

    @Override
    public void onConnectionFailed() {
        Log.d("Main", "Failed..");
    }

    @Override
    public void onUserSelected(String userId) {
        registerUser(userId);
        initPepperTalk(userId);
    }

    private void initPepperTalk(String userId) {
        PepperTalk.getInstance(this)
                .initialize(Config.CLIENT_ID,
                        Config.CLIENT_SECRET,
                        userId)
                .connectionListener(this)
                .connect();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class UsersFragment extends ListFragment implements PepperTalk.MessageCallback {

        List<String> mUsersList = new ArrayList<String>();

        private String mRegisteredUser;

        public static UsersFragment getInstance(String registeredUserId) {
            UsersFragment fragment = new UsersFragment();
            fragment.mRegisteredUser = registeredUserId;
            return fragment;
        }

        public UsersFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_users, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            for (String u : mUsers) {
                if (!u.equals(mRegisteredUser)) {
                    mUsersList.add(u);
                }
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1);
            arrayAdapter.addAll(mUsersList);
            getListView().setAdapter(arrayAdapter);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            PepperTalk.getInstance(getActivity())
                    .chatWith(USER_2)
                    .start();
        }

        @Override
        public void onNewMessage(String userId, String topicId, int unreadCount) {

        }
    }

    private boolean isUserRegistered() {
        String userId = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(PREF_USER, null);
        return userId != null;
    }

    private String getRegisteredUser() {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getString(PREF_USER, null);
    }

    private void registerUser(String userId) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(PREF_USER, userId)
                .apply();
    }

}
