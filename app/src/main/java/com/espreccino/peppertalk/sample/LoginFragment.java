package com.espreccino.peppertalk.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by imran on 08/02/15 3:32 PM in com.espreccino.peppertalk.sample.
 */
public class LoginFragment extends ListFragment {

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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1);
        arrayAdapter.addAll(MainActivity.mUsers);
        getListView().setAdapter(arrayAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mLoginFragmentListener != null) {
            mLoginFragmentListener.onUserSelected(MainActivity.mUsers[position]);
        }
    }
}
