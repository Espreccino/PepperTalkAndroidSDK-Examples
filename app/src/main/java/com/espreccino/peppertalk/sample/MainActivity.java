package com.espreccino.peppertalk.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.espreccino.peppertalk.PepperTalk;
import com.espreccino.peppertalk.PepperTalkError;
import com.espreccino.peppertalk.UserCallback;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends ActionBarActivity implements LoginFragment.LoginFragmentListener {

    private final String TAG = "MainActivity.class";

    static final String[] USERS = {"Jon:j_android@getpeppertalk.com"};

    static final String[] TOPICS = {"10011:Lets Ride!"};

    static List<User> mUsers = new ArrayList<User>();
    static List<Topic> mTopics = new ArrayList<Topic>();
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
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromPrefs();
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDataToSharedPrefs();
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
            initPepperTalk(userId);
            if (mUsersFragment == null) {
                mUsersFragment = UsersFragment.getInstance(getRegisteredUser());
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, mUsersFragment)
                    .commit();
        }
    }

    /**
     * Add client_id and client_secret in strings.xml
     *
     * @param userId
     */
    private void initPepperTalk(String userId) {
        ((PepperTalkSample) getApplication()).initPepperTalk(userId);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    /**
     * User fragment
     */
    public static class UsersFragment extends Fragment {

        RecyclerView mRecyclerView;
        ChatListAdapter mAdapter;
        String mRegisteredUser;
        Button mButtonUpdateUser, mButtonChatWith;

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
            View view = inflater.inflate(R.layout.fragment_users, container, false);
            loadRecyclerView(view);
            return view;
        }

        private void loadRecyclerView(View view) {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
            mAdapter = new ChatListAdapter(mRegisteredUser);
            LinearLayoutManager manager = new LinearLayoutManager(view.getContext(),
                    LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mAdapter);
        }

        public void updateList() {
            if (mRecyclerView != null && mRecyclerView.getAdapter() != null) {
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mButtonUpdateUser = (Button) view.findViewById(R.id.button_update_user);
            mButtonChatWith = (Button) view.findViewById(R.id.button_chat_with_user);

            mButtonUpdateUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PepperTalk.getInstance(getActivity())
                            .updateUser("Jon", null, new UserCallback() {
                                @Override
                                public void onSuccess(com.espreccino.peppertalk.User user) {

                                }

                                @Override
                                public void onFail(PepperTalkError error) {

                                }
                            });
                }
            });


            mButtonChatWith.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());

                    alert.setTitle("Enter an email");
                    alert.setMessage("Chat with new user");

                    final EditText input = new EditText(v.getContext());
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            boolean has = false;
                            for (User user : mUsers) {
                                if (value.equals(user.email)) {
                                    has = true;
                                    break;
                                }
                            }
                            if (!has) {
                                User user = new User(value, value);
                                mUsers.add(user);
                            }
                            PepperTalk.getInstance(getActivity())
                                    .chatWithParticipant(value)
                                    .topicId(mTopics.get(0).topicId)
                                    .topicTitle(mTopics.get(0).topicTitle)
                                    .start();
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });

                    alert.show();

                }
            });
        }

        private class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.UserHolder>
                implements PepperTalk.MessageListener {

            String registeredUser;

            ChatListAdapter(String registeredUser) {
                this.registeredUser = registeredUser;
                PepperTalk.getInstance(getActivity())
                        .registerMessageListener(this);
            }

            @Override
            public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.item_user, parent, false);
                return new UserHolder(view);
            }

            @Override
            public void onBindViewHolder(UserHolder holder, int position) {
                holder.loadUser(mUsers.get(position));
            }

            @Override
            public int getItemCount() {
                return mUsers.size();
            }

            @Override
            public void onNewMessage(String userId, String topicId, int unreadCount) {
                boolean userExists = false;
                for (User user : mUsers) {
                    if (user.email.equals(userId)) {
                        userExists = true;
                    }
                }
                if (!userExists) {
                    User user = new User(userId, userId);
                    mUsers.add(user);
                }
                notifyDataSetChanged();
            }

            class UserHolder extends RecyclerView.ViewHolder {

                final View view;
                final TextView textUserName;
                final TextView textUserEmail;
                final TextView textMessageCount;
                User user;
                String id;

                public UserHolder(View itemView) {
                    super(itemView);
                    itemView.setOnClickListener(new UserClickListener());
                    view = itemView.findViewById(R.id.view_user);
                    textUserName = (TextView) itemView.findViewById(R.id.textView_user_name);
                    textUserEmail = (TextView) itemView.findViewById(R.id.textView_user_email);
                    textMessageCount = (TextView) itemView.findViewById(R.id.textView_message_count);
                }

                public void loadUser(Object obj) {
                    if (obj instanceof User) {
                        this.user = (User) obj;
                        this.id = user.email;
                        textUserEmail.setText(user.email);
                        textUserName.setText(user.name);
                        if (user.email.equals(registeredUser)) {
                            textUserEmail.setTextColor(Color.GREEN);
                            view.setBackgroundColor(Color.RED);
                        } else {
                            textUserEmail.setTextColor(Color.BLACK);
                            view.setBackgroundColor(Color.BLUE);
                        }
                        int count = PepperTalk.getInstance(getActivity())
                                .getParticipantUnreadCount(user.email, mTopics.get(0).topicId);
                        textMessageCount.setText(count + "");
                    }
                }

                private class UserClickListener implements View.OnClickListener {
                    @Override
                    public void onClick(View v) {
                        if (id != null) {
                            PepperTalk.getInstance(getActivity())
                                    .chatWithParticipant(id)
                                    .topicId(mTopics.get(0).topicId)
                                    .topicTitle(mTopics.get(0).topicTitle)
                                    .start();
                        }
                    }
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

    private void saveUserName(String userName) {
        getSharedPrefs()
                .edit()
                .putString(PREF_USER_NAME, userName)
                .apply();
    }

    private String getUserName() {
        return getSharedPrefs().getString(PREF_USER_NAME, null);
    }

    private SharedPreferences getSharedPrefs() {
        return getSharedPreferences("com.esp", MODE_PRIVATE);
    }

    private String PREF_USERS = "prefs_users";
    private String PREF_TOPICS = "prefs_topics";

    private void saveDataToSharedPrefs() {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = getSharedPrefs().edit();

        Set<String> set = new HashSet<String>();
        for (User user : mUsers) {
            set.add(gson.toJson(user));
        }
        editor.putStringSet(PREF_USERS, set)
                .apply();

        set.clear();
        for (Topic topic : mTopics) {
            set.add(gson.toJson(topic));
        }
        editor.putStringSet(PREF_TOPICS, set)
                .apply();

    }

    private void loadDataFromPrefs() {
        Gson gson = new Gson();
        mUsers = new ArrayList<User>();
        mTopics = new ArrayList<Topic>();

        SharedPreferences.Editor editor = getSharedPrefs().edit();
        Set<String> set;

        set = getSharedPrefs().getStringSet(PREF_USERS, Collections.EMPTY_SET);
        if (set.isEmpty()) {
            if (mUsers.size() != USERS.length) {
                for (String usr : USERS) {
                    String[] split = usr.split(":");
                    mUsers.add(new User(split[0], split[1]));
                }
            }
        } else {
            for (String s : set) {
                User user = gson.fromJson(s, User.class);
                mUsers.add(user);
            }
        }

        set = getSharedPrefs().getStringSet(PREF_TOPICS, Collections.EMPTY_SET);

        if (set.isEmpty()) {
            if (mTopics.size() != TOPICS.length) {
                for (String topic : TOPICS) {
                    String[] split = topic.split(":");
                    mTopics.add(new Topic(split[0], split[1]));
                }
            }
        } else {
            for (String s : set) {
                Topic topic = gson.fromJson(s, Topic.class);
                mTopics.add(topic);
            }
        }

        String userId = getRegisteredUser();
        if (userId != null) {
            loadUserFragment(getRegisteredUser());
        }
    }
}
