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
 * Created by Nate on 10/22/17.
 */

public class ManagerTenants extends ListFragment {

    // class variables
    Activity mActivity;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public interface TenantStatusListener {
        void callTenantsView(Boolean status);
    }

    TenantStatusListener onTenantStatusListener;

    public ManagerTenants() {
        // empty constructor
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
                onTenantStatusListener = (TenantStatusListener) mActivity;
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.toString() + " must implement TenantStatusListener");
            }
        }

        mActivity.setTitle("Tenant Status");

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
        displayItems.add("Current Tenants");
        displayItems.add("Archived Tenants");


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
                onTenantStatusListener.callTenantsView(true);
                break;
            case 1:
                onTenantStatusListener.callTenantsView(false);
                break;
        }

        super.onListItemClick(l, v, position, id);
    }
}
