package com.espreccino.peppertalk.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by imran on 08/02/15 3:32 PM in com.espreccino.peppertalk.sample.
 */
public class LoginFragment extends ListFragment {

    private List<User> mUsers;

    public static LoginFragment getInstance(List<User> users) {
        LoginFragment fragment = new LoginFragment();
        fragment.mUsers = users;
        return fragment;
    }

    public LoginFragment(){}

    public interface LoginFragmentListener {
        public void onUserSelected(String userId);
    }

    LoginFragmentListener mLoginFragmentListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof LoginFragmentListener) {
            mLoginFragmentListener = (LoginFragmentListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserAdapter adapter = new UserAdapter();
        getListView().setAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mLoginFragmentListener != null) {
            mLoginFragmentListener.onUserSelected(mUsers.get(position).email);
        }
    }

    private class UserAdapter extends BaseAdapter {

        UserAdapter() {
        }

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
                textMessageCount.setVisibility(View.INVISIBLE);
            }
        }
    }
}
