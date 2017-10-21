package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project3w.newproperts.Helpers.StaffViewHolder;
import com.project3w.newproperts.LoginActivity;
import com.project3w.newproperts.Objects.Staff;
import com.project3w.newproperts.R;

import static com.project3w.newproperts.MainActivity.COMPANY_CODE;

/**
 * Created by Nate on 10/19/17.
 */

public class StaffView extends Fragment {

    // class variables
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Activity mActivity;
    RecyclerView staffView;
    FirebaseRecyclerAdapter staffAdapter;
    String companyCode, staffFunction;
    Boolean isCurrent;

    public static final String STAFF_TYPE = "com.project3w.properts.STAFF_TYPE";

    public interface DisplayStaffListener {
        void displayStaff(Staff staff, Boolean isCurrent);
        void addNewStaff();
    }

    DisplayStaffListener onDisplayStaffListener;

    public StaffView newInstance(String staffFunction) {

        StaffView myFragment = new StaffView();
        Bundle args = new Bundle();
        args.putString(STAFF_TYPE, staffFunction);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.manager_staff, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mActivity = getActivity();

        staffFunction = getArguments().getString(STAFF_TYPE, "current");
        isCurrent = staffFunction.equals("current");

        // set our options menu
        setHasOptionsMenu(true);

        if (mUser == null) {
            // send the user back to the login screen
            Intent loginScreen = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginScreen);
            getActivity().finish();
        } else {

            SharedPreferences mPrefs = mActivity.getSharedPreferences("com.project3w.properts", Context.MODE_PRIVATE);
            companyCode = mPrefs.getString(COMPANY_CODE, null);

            // attach our listener
            try {
                onDisplayStaffListener = (DisplayStaffListener) mActivity;
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.toString() + " must implement DisplayStaffListener");
            }

        }

        mActivity.setTitle("Staff List");
        staffView = view.findViewById(R.id.staff_list);

        return view;
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

        // setup our fab to add new requests
        FloatingActionButton fab = getActivity().findViewById(R.id.staff_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDisplayStaffListener.addNewStaff();
            }
        });

        // grab the reference to our RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        // setup our database references
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query tenantRequestsQuery = firebaseDatabase.getReference().child(companyCode).child("1").child("staff")
                .child(staffFunction)
                .orderByChild("staffName");

        // setup our RecyclerView to display content
        FirebaseRecyclerOptions<Staff> staffOptions =
                new FirebaseRecyclerOptions.Builder<Staff>()
                        .setQuery(tenantRequestsQuery, Staff.class)
                        .build();

        staffAdapter = new FirebaseRecyclerAdapter<Staff, StaffViewHolder>(staffOptions) {
            @Override
            public StaffViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_staff, parent, false);
                return new StaffViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final StaffViewHolder holder, int position, final Staff staff) {

                // set our holder options for the view
                holder.staffName.setText(staff.getStaffName());

                holder.setOnClickListener(new StaffViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        onDisplayStaffListener.displayStaff(staff, isCurrent);
                    }
                });
            }
        };

        // call our recycler
        staffView.setAdapter(staffAdapter);
        staffView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        staffAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        staffAdapter.stopListening();
    }
}
