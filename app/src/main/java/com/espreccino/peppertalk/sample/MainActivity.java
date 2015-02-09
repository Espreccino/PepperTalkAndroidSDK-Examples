package com.espreccino.peppertalk.sample;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.espreccino.peppertalk.PepperTalk;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements LoginFragment.LoginFragmentListener,
        PepperTalk.ConnectionListener {

    private final String TAG = "MainActivity.class";

    static final String[] USERS = {"Jon:jon_android@getpeppertalk.com",
            "Ben:ben_android@getpeppertalk.com"};

    static final String[] TOPICS = {"1001:Lets Ride!",
            "1002:Lets Eat!"};

    static List<User> mUsers = new ArrayList<User>();
    static List<Topic> mTopics = new ArrayList<Topic>();
    private final static String PREF_USER = "com.espreccino.peppertalk.sample_user_login";
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog = new ProgressDialog(this);
        addSampleData();
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

    private void addSampleData() {
        if (mUsers.size() != USERS.length) {
            for (String usr : USERS) {
                String[] split = usr.split(":");
                mUsers.add(new User(split[0], split[1]));
            }
        }

        if (mTopics.size() != TOPICS.length) {
            for (String topic : TOPICS) {
                String[] split = topic.split(":");
                mTopics.add(new Topic(split[0], split[1]));
            }
        }
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
        if (userId != null) {
            getSupportActionBar().show();
            setTitle("PepperTalk");
            initPepperTalk(userId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, UsersFragment.getInstance(getRegisteredUser()))
                    .commit();
        }
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
                .init(clientId,
                        clientSecret,
                        userId)
                .connectionListener(this)
                .connect();
    }

    /**
     * User fragment
     */
    public static class UsersFragment extends Fragment {

        RecyclerView mRecyclerView;
        ChatListAdapter mAdapter;
        String mRegisteredUser;

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
            mAdapter = new ChatListAdapter();
            LinearLayoutManager manager = new LinearLayoutManager(view.getContext(),
                    LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mAdapter);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }

        private class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.UserHolder>
                implements PepperTalk.MessageListener {

            ChatListAdapter() {
                PepperTalk.getInstance(getActivity())
                        .setMessageListener(this);
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
                notifyDataSetChanged();
            }

            class UserHolder extends RecyclerView.ViewHolder {

                final View view;
                final TextView textUserName;
                final TextView textUserEmail;
                final TextView textMessageCount;
                User user;

                public UserHolder(View itemView) {
                    super(itemView);
                    itemView.setOnClickListener(new UserClickListener());
                    view = itemView.findViewById(R.id.view_user);
                    textUserName = (TextView) itemView.findViewById(R.id.textView_user_name);
                    textUserEmail = (TextView) itemView.findViewById(R.id.textView_user_email);
                    textMessageCount = (TextView) itemView.findViewById(R.id.textView_message_count);
                }

                public void loadUser(User user) {
                    this.user = user;
                    if (user.email.equals(mRegisteredUser)) {
                        view.setBackgroundColor(Color.GREEN);
                        textUserName.setTypeface(null, Typeface.BOLD_ITALIC);
                    }
                    textUserEmail.setText(user.email);
                    textUserName.setText(user.name);
                    int count = PepperTalk.getInstance(getActivity())
                            .getParticipantUnreadCount(user.email, mTopics.get(0).topicId);
                    if (count > 0) {
                        textMessageCount.setText(count + "");
                    } else {
                        textMessageCount.setText("");
                    }
                }

                private class UserClickListener implements View.OnClickListener {
                    @Override
                    public void onClick(View v) {
                        if (user != null) {
                            PepperTalk.getInstance(getActivity())
                                    .chatWithParticipant(user.email)
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

    private SharedPreferences getSharedPrefs() {
        return getSharedPreferences("com.esp", MODE_PRIVATE);
    }

}
