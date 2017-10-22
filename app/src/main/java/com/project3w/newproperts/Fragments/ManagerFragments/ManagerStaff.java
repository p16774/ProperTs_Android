package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project3w.newproperts.LoginActivity;
import com.project3w.newproperts.R;

import java.util.ArrayList;

/**
 * Created by Nate on 10/19/17.
 */

public class ManagerStaff extends ListFragment {

    // class variables
    Activity mActivity;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public interface ManageStaffListener {
        void callManagerStaff(String staffFunction);
    }

    ManageStaffListener onManageStaffListener;

    public ManagerStaff() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // set our options menu
        setHasOptionsMenu(true);

        // send user to the login screen if they aren't logged in
        if (mUser == null) {
            Intent loginScreen = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginScreen);
            getActivity().finish();
        }else {
            // attach our listener
            try {
                onManageStaffListener = (ManageStaffListener) mActivity;
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.toString() + " must implement ManageStaffListener");
            }
        }

        mActivity.setTitle("Manage Staff");

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.manager_menu, menu);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> displayItems = new ArrayList<>();
        displayItems.add("View Current Staff");
        displayItems.add("View Previous Staff");


        // create our adapter and set it to our listview
        ArrayAdapter<String> managerOptionsAdapter = new ArrayAdapter<>(getActivity(), R.layout.manager_items, displayItems);
        setListAdapter(managerOptionsAdapter);

        // set a background to the page
        getListView().setBackground(ContextCompat.getDrawable(mActivity, R.drawable.home_bg));

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // call the appropriate flag to transition to the right fragment
        switch (position) {
            case 0:
                onManageStaffListener.callManagerStaff("current");
                break;
            case 1:
                onManageStaffListener.callManagerStaff("previous");
                break;
        }

        super.onListItemClick(l, v, position, id);
    }
}
