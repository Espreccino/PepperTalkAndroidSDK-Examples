package com.espreccino.peppertalk.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.espreccino.peppertalk.PepperTalk;


public class MainActivity extends ActionBarActivity implements PepperTalk.ConnectionListener {

    static final String USER_1 = "user_android_1@getpeppertalk.com";
    static final String USER_2 = "user_android_2@getpeppertalk.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new UsersFragment())
                    .commit();
        }

        PepperTalk.getInstance(this)
                .initialize(Config.CLIENT_ID,
                        Config.CLIENT_SECRET,
                        USER_1)
                .connectionListener(this)
                .connect();
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
    public void onConnecting() {

    }

    @Override
    public void onConnected() {
        Log.d("Main", "Connected..");
    }

    @Override
    public void onConnectionFailed() {
        Log.d("Main", "Failed..");
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class UsersFragment extends Fragment implements PepperTalk.MessageCallback {

        TextView mTextViewUser2;

        public UsersFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mTextViewUser2 = (TextView) view.findViewById(R.id.textView_user);
            mTextViewUser2.setText(USER_2);
            setListener();
        }

        private void setListener() {
            mTextViewUser2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PepperTalk.getInstance(getActivity())
                            .chatWith(USER_2)
                            .start();
                }
            });
        }

        @Override
        public void onNewMessage(String userId, String topicId, int unreadCount) {
            mTextViewUser2.setText(USER_2 + " " + (unreadCount));
        }
    }
}
