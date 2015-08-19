package com.espreccino.peppertalk.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.espreccino.peppertalk.JSONCallback;
import com.espreccino.peppertalk.PepperTalk;
import com.espreccino.peppertalk.PepperTalkError;
import com.espreccino.peppertalk.UserCallback;
import com.espreccino.peppertalk.sample.model.Group;
import com.espreccino.peppertalk.sample.model.Topic;
import com.espreccino.peppertalk.sample.model.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * ${CLASS_NAME} com.espreccino.sample.
 */
public class UsersFragment extends Fragment {

    static List<User> mUsers = new ArrayList<User>();
    static List<Topic> mTopics = new ArrayList<Topic>();
    static List<Group> mGroups = new ArrayList<Group>();

    RecyclerView mRecyclerView;
    ChatListAdapter mAdapter;
    String mRegisteredUser;
    String mUserName;
    Button mButtonUpdateUser, mButtonChatWith;
    PepperTalk mPepperTalk;

    public static UsersFragment getInstance(String registeredUserId, String userName) {
        UsersFragment fragment = new UsersFragment();
        fragment.mRegisteredUser = registeredUserId;
        fragment.mUserName = userName;
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
        mPepperTalk = PepperTalk.getInstance(view.getContext());
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

        final String image = "http://i.imgur.com/p81Eh87.jpg";

        mButtonUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPepperTalk
                        .updateUser(mUserName, image, new UserCallback() {
                            @Override
                            public void onSuccess(com.espreccino.peppertalk.User user) {
                                Log.d("Sample", user.getName());
                            }

                            @Override
                            public void onFail(PepperTalkError error) {
                                error.printStackTrace();
                            }
                        });
            }
        });


        view.findViewById(R.id.button_send_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("hello", "its me!");
                    mPepperTalk
                            .sendCustomData("i@pepper.com",
                                    "Geo data",
                                    "topicId",
                                    "topicTitle",
                                    object, new JSONCallback() {
                                        @Override
                                        public void onSuccess(JSONObject jsonObject) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getActivity(),
                                                            "Custom Data",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFail(PepperTalkError error) {

                                        }
                                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                        mPepperTalk
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

    public void removeGroup(Group group) {
        boolean has = false;
        for (int i = 0; i < mGroups.size(); i++) {
            Group g = mGroups.get(i);
            if (g.id.equals(group.id)) {
                has = true;
                g.name = group.name;
                break;
            }
        }
        if (has) {
            mGroups.remove(group);
        }
    }

    public void addOrUpdateGroup(Group group) {
        boolean has = false;
        for (int i = 0; i < mGroups.size(); i++) {
            Group g = mGroups.get(i);
            if (g.id.equals(group.id)) {
                has = true;
                g.name = group.name;
                break;
            }
        }
        if (!has) {
            mGroups.add(group);
        }

        updateList();
    }


    private String PREF_USERS = "prefs_users";
    private String PREF_TOPICS = "prefs_topics";
    private String PREF_GROUPS = "prefs_groups";

    public void saveDataToSharedPrefs(SharedPreferences preferences) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = preferences.edit();

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

        set.clear();
        for (Group group : mGroups) {
            set.add(gson.toJson(group));
        }
        editor.putStringSet(PREF_GROUPS, set)
                .apply();
    }

    public void loadDataFromPrefs(SharedPreferences preferences) {
        Gson gson = new Gson();
        mUsers = new ArrayList<User>();
        mGroups = new ArrayList<Group>();
        mTopics = new ArrayList<Topic>();

        SharedPreferences.Editor editor = preferences.edit();
        Set<String> set;

        set = preferences.getStringSet(PREF_USERS, Collections.EMPTY_SET);
        if (set.isEmpty()) {
            if (mUsers.size() != MainActivity.USERS.length) {
                for (String usr : MainActivity.USERS) {
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

        set = preferences.getStringSet(PREF_TOPICS, Collections.EMPTY_SET);

        if (set.isEmpty()) {
            if (mTopics.size() != MainActivity.TOPICS.length) {
                for (String topic : MainActivity.TOPICS) {
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

        set = preferences.getStringSet(PREF_GROUPS, Collections.EMPTY_SET);

        if (!set.isEmpty()) {
            for (String s : set) {
                Group group = gson.fromJson(s, Group.class);
                mGroups.add(group);
            }
        }
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
            if (position <= mUsers.size() - 1) {
                holder.loadUser(mUsers.get(position));
            } else if (position < mUsers.size() + mGroups.size()) {
                holder.loadUser(mGroups.get(position - mUsers.size()));
            }
        }

        @Override
        public int getItemCount() {
            return mUsers.size() + mGroups.size();
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
            final ImageButton imageButtonMore;
            User user;
            Group group;
            String id;
            PopupMenu popupMenu;

            public UserHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new UserClickListener());
                view = itemView.findViewById(R.id.view_user);
                textUserName = (TextView) itemView.findViewById(R.id.textView_user_name);
                textUserEmail = (TextView) itemView.findViewById(R.id.textView_user_email);
                textMessageCount = (TextView) itemView.findViewById(R.id.textView_message_count);
                imageButtonMore = (ImageButton) itemView.findViewById(R.id.imageButton_more);
                popupMenu = new PopupMenu(itemView.getContext(), imageButtonMore);
                popupMenu.inflate(R.menu.menu_user_item);
                imageButtonMore.setOnClickListener(new OnMoreOptionsListener());
            }

            public void loadUser(Object obj) {
                if (obj instanceof User) {
                    this.user = (User) obj;
                    this.id = user.email;
                    loadMenu(this.id);
                    textUserEmail.setText(user.email);
                    textUserName.setText(user.name);
                    if (user.email.equals(registeredUser)) {
                        textUserEmail.setTextColor(Color.GREEN);
                        view.setBackgroundColor(Color.RED);
                    } else {
                        textUserEmail.setTextColor(Color.BLACK);
                        view.setBackgroundColor(Color.BLUE);
                    }
                    if (mTopics.size() > 0) {
                        int count = PepperTalk.getInstance(getActivity())
                                .getParticipantUnreadCount(user.email, mTopics.get(0).topicId);
                        textMessageCount.setText(count + "");
                    }

                } else if (obj instanceof Group) {
                    this.group = (Group) obj;
                    this.id = group.id;
                    textUserEmail.setText(group.id);
                    textUserName.setText(group.name);
                    int count = mPepperTalk.getParticipantUnreadCount(group.id, mTopics.get(0).topicId);
                    textMessageCount.setText(count + "");
                }
            }

            private void loadMenu(String userId) {
                Menu menu = popupMenu.getMenu();
                com.espreccino.peppertalk.User user = mPepperTalk.getRegisteredUser();

                if (mPepperTalk.isParticipantBlocked(userId)) {
                    menu.findItem(R.id.action_block).setVisible(false);
                    menu.findItem(R.id.action_unblock).setVisible(true);
                } else {
                    menu.findItem(R.id.action_block).setVisible(true);
                    menu.findItem(R.id.action_unblock).setVisible(false);
                }

                if (mPepperTalk.isParticipantMuted(userId)) {
                    menu.findItem(R.id.action_mute).setVisible(false);
                    menu.findItem(R.id.action_un_mute).setVisible(true);
                } else {
                    menu.findItem(R.id.action_mute).setVisible(true);
                    menu.findItem(R.id.action_un_mute).setVisible(false);
                }
                popupMenu.setOnMenuItemClickListener(new MenuItemClickListener(userId));
            }

            private class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

                String userId;

                MenuItemClickListener(String userId) {
                    this.userId = userId;
                }

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.action_block: {
                            mPepperTalk.blockParticipant(userId, new UserBlockCallback());
                            break;
                        }
                        case R.id.action_unblock: {
                            mPepperTalk.unblockParticipant(userId, new UserBlockCallback());
                            break;
                        }
                        case R.id.action_mute: {
                            mPepperTalk.muteParticipant(userId, new UserMuteCallback());
                            break;
                        }
                        case R.id.action_un_mute: {
                            mPepperTalk.unMuteParticipant(userId, new UserMuteCallback());
                            break;
                        }
                    }
                    return true;
                }
            }

            private class UserBlockCallback extends com.espreccino.peppertalk.UserCallback {

                @Override
                public void onSuccess(com.espreccino.peppertalk.User user) {
                    loadMenu(UserHolder.this.user.email);
                }

                @Override
                public void onFail(PepperTalkError error) {
                    Timber.e(error.getMessage(), error);
                }
            }

            private class UserMuteCallback extends com.espreccino.peppertalk.UserCallback {

                @Override
                public void onSuccess(com.espreccino.peppertalk.User user) {
                    loadMenu(UserHolder.this.user.email);
                }

                @Override
                public void onFail(PepperTalkError error) {
                    Timber.e(error.getMessage(), error);
                }
            }

            private class OnMoreOptionsListener implements View.OnClickListener {

                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            }

            private class UserClickListener implements View.OnClickListener {
                @Override
                public void onClick(View v) {
                    if (id != null) {
                        if (id.equals(mRegisteredUser)) {
                            mPepperTalk.showAllConversations();
                        } else {
                            mPepperTalk
                                    .chatWithParticipant(id)
                                    .activity(getActivity())
                                    .populateMessage("Hello")
                                    .topicId(mTopics.get(0).topicId)
                                    .topicTitle(mTopics.get(0).topicTitle)
                                    .start();
                        }
                    }
                }
            }
        }
    }
}
