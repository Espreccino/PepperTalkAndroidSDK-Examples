package com.espreccino.peppertalk.sample;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.espreccino.peppertalk.PepperTalk;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements LoginFragment.LoginFragmentListener,
        PepperTalk.ConnectionListener {

    private final String TAG = "MainActivity.class";

    static final String[] USERS = {"Jon:jon_android@getpeppertalk.com",
            "Ben:ben_android@getpeppertalk.com"};

    static List<User> mUsers = new ArrayList<User>();
    private final static String PREF_USER = "com.espreccino.peppertalk.sample_user_login";
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog = new ProgressDialog(this);
        if (mUsers.size() != USERS.length) {
            for (String usr : USERS) {
                String[] split = usr.split(":");
                mUsers.add(new User(split[0], split[1]));
            }
        }

        if (savedInstanceState == null) {
            String userId = getRegisteredUser();
            Fragment fragment;
            if (userId == null) {
                getSupportActionBar().hide();
                fragment = LoginFragment.getInstance(mUsers);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            } else {
                loadUserFragment(userId);
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

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnecting(int i) {
        showDialog(true);
    }

    @Override
    public void onConnected() {
        showDialog(false);
    }

    @Override
    public void onConnectionFailed(Throwable e) {
        showDialog(false);
        e.printStackTrace();
    }

    @Override
    public void onUserSelected(String userId) {
        registerUser(userId);
        loadUserFragment(userId);
    }

    private void loadUserFragment(String userId) {
        getSupportActionBar().show();
        setTitle("PepperTalk");
        initPepperTalk(userId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, UsersFragment.getInstance(getRegisteredUser()))
                .commit();
    }

    private void showDialog(boolean show) {
        if (show) {
            mDialog.setTitle("loading...");
            mDialog.show();
        } else {
            mDialog.dismiss();
        }
    }

    /**
     * Add client_id and client_secret in strings.xml
     *
     * @param userId
     */
    private void initPepperTalk(String userId) {
        String clientId = getString(R.string.client_id);
        String clientSecret = getString(R.string.client_secret);
        PepperTalk.getInstance(this)
                .initialize(clientId,
                        clientSecret,
                        userId)
                .connectionListener(this)
                .connect();
    }

    /**
     * User fragment
     */
    public static class UsersFragment extends ListFragment implements PepperTalk.MessageCallback {

        private String mTopicId = "100010001";
        UserAdapter mUserAdapter;

        public static UsersFragment getInstance(String registeredUserId) {
            UsersFragment fragment = new UsersFragment();
            for (User usr : mUsers) {
                if (usr.email.equals(registeredUserId)) {
                    mUsers.remove(usr);
                    break;
                }
            }
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
            mUserAdapter = new UserAdapter();
            getListView().setAdapter(mUserAdapter);
            PepperTalk.getInstance(getActivity())
                    .setMessageCallback(this);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            PepperTalk.getInstance(getActivity())
                    .chatWith(mUsers.get(position).email)
                    .topicId(mTopicId)
                    .topicTitle("Let ride!")
                    .start();
        }

        @Override
        public void onNewMessage(String userId, String topicId, int unreadCount) {
            if (mUserAdapter != null) {
                mUserAdapter.notifyDataSetChanged();
            }
        }

        private class UserAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                return mUsers.size();
            }

            @Override
            public User getItem(int position) {
                return mUsers.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                UserHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity())
                            .inflate(R.layout.item_user, parent, false);
                    holder = new UserHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (UserHolder) convertView.getTag();
                }

                holder.loadUser(getItem(position));
                return convertView;
            }

            private class UserHolder {

                final TextView textUserName;
                final TextView textUserEmail;
                final TextView textMessageCount;

                public UserHolder(View view) {
                    textUserName = (TextView) view.findViewById(R.id.textView_user_name);
                    textUserEmail = (TextView) view.findViewById(R.id.textView_user_email);
                    textMessageCount = (TextView) view.findViewById(R.id.textView_message_count);
                }

                public void loadUser(User user) {
                    textUserEmail.setText(user.email);
                    textUserName.setText(user.name);
                    textMessageCount.setText(PepperTalk.getInstance(getActivity()).getUnreadForTopicUser(user.email, mTopicId) + "");
                }
            }
        }
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

    private SharedPreferences getSharedPrefs() {
        return getSharedPreferences("com.esp", MODE_PRIVATE);
    }

}
