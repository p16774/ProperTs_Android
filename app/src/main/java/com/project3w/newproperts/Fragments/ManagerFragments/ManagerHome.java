package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.project3w.newproperts.LoginActivity;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Nate on 9/20/17.
 */

public class ManagerHome extends ListFragment {

    // class variables
    Activity mActivity;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public interface MenuOptionSelectedListener {
        void openMenuOption(String menuOption);
    }

    MenuOptionSelectedListener onMenuOptionSelectedListener;

    public ManagerHome() {
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
                onMenuOptionSelectedListener = (MenuOptionSelectedListener) mActivity;
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.toString() + " must implement MenuOptionSelectedListener");
            }
        }

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
        displayItems.add("Manage Rental Units");
        displayItems.add("Update Company Info");
        displayItems.add("Manage Staff Members");
        displayItems.add("Add a New Tenant");

        // create our adapter and set it to our listview
        ArrayAdapter<String> managerOptionsAdapter = new ArrayAdapter<>(getActivity(), R.layout.manager_items, displayItems);
        setListAdapter(managerOptionsAdapter);

        // set a background to the page
        getListView().setBackground(ContextCompat.getDrawable(mActivity, R.drawable.manager_bg));

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // call the appropriate flag to transition to the right fragment
        switch (position) {
            case 0:
                onMenuOptionSelectedListener.openMenuOption("units");
                break;
        }

        super.onListItemClick(l, v, position, id);
    }

    /*private void getCurrentTenants() {

        // get our database reference
        DatabaseReference currentTenantRef = firebaseDatabase.getReference("currentTenants");
        currentTenantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String,String>> t = new GenericTypeIndicator<HashMap<String, String>>() {};
                HashMap<String,String> tenantsMap = dataSnapshot.getValue(t);

                // create new arraylist
                final ArrayList<String> tenantList = new ArrayList<>();

                // remove any vacants from the list
                try {
                    // convert hashmap to arraylist to iterate over
                    tenantList.addAll(tenantsMap.values());
                    tenantList.removeAll(Collections.singleton("vacant"));

                    // if we still have a list display it
                    if (tenantList.size() > 0) {
                        final ArrayList<String> tenantNames = new ArrayList<>();
                        for (String tenantID : tenantList) {
                            DatabaseReference tenantData = firebaseDatabase.getReference("tenants").child(tenantID);
                            tenantData.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Tenant tenant = dataSnapshot.getValue(Tenant.class);
                                    tenantNames.add(tenant.getTenantName());
                                    getSpinnerData(tenantSpinner, tenantNames);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                } catch (NullPointerException e) {
                    Log.d("NULL EXCEPTION", "Database Reference Error in HashMap");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

  /*private void getSpinnerData(Spinner spinner, ArrayList<String> list) {

        // setup our data selections
        // TODO: Firebase pull of all active tenants to populate spinner

        // assign the listener
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, list);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

    }*/


}
