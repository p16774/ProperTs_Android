package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project3w.newproperts.Helpers.ComplaintViewHolder;
import com.project3w.newproperts.LoginActivity;
import com.project3w.newproperts.Objects.Complaint;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.R;

import static com.project3w.newproperts.MainActivity.COMPANY_CODE;

/**
 * Created by Nate on 10/16/17.
 */

public class ComplaintsView extends Fragment {

    // class variables
    Activity mActivity;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    RecyclerView complaintView;
    FirebaseRecyclerAdapter complaintAdapter;
    String companyCode, complaintType;

    public static final String COMPLAINT_TYPE = "com.project3w.properts.COMPLAINT_TYPE";

    public interface ComplaintAcknowledgementListener {
        void displayComplaint(Complaint complaint, Tenant tenant, Boolean isClosed);
    }

    ComplaintAcknowledgementListener onComplaintAcknowledgementListener;

    public ComplaintsView newInstance(String complaintType) {

        ComplaintsView myFragment = new ComplaintsView();
        Bundle args = new Bundle();
        args.putString(COMPLAINT_TYPE, complaintType);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_complaints, container, false);

        mActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        complaintView = view.findViewById(R.id.manager_complaint_list);
        complaintType = getArguments().getString(COMPLAINT_TYPE, "active");

        // set our options menu
        setHasOptionsMenu(true);

        // send user to the login screen if they aren't logged in
        if (mUser == null) {
            Intent loginScreen = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginScreen);
            getActivity().finish();
        } else {
            // grab our company code from shared preferences
            SharedPreferences mPrefs = mActivity.getSharedPreferences("com.project3w.properts", Context.MODE_PRIVATE);
            companyCode = mPrefs.getString(COMPANY_CODE, null);

            // attach our listener
            try {
                onComplaintAcknowledgementListener = (ComplaintAcknowledgementListener) mActivity;
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.toString() + " must implement ComplaintAcknowledgementListener");
            }
        }

        mActivity.setTitle("Tenant Complaints");

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

        // grab the reference to our RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        // setup our database references
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query tenantComplaintQuery = firebaseDatabase.getReference()
                .child(companyCode).child("1")
                .child("complaints")
                .child(complaintType);

        // setup our RecyclerView to display content
        FirebaseRecyclerOptions<Complaint> complaintOptions =
                new FirebaseRecyclerOptions.Builder<Complaint>()
                        .setQuery(tenantComplaintQuery, Complaint.class)
                        .build();

        complaintAdapter = new FirebaseRecyclerAdapter<Complaint, ComplaintViewHolder>(complaintOptions) {
            @Override
            public ComplaintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_complaint, parent, false);
                return new ComplaintViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final ComplaintViewHolder holder, int position, final Complaint complaint) {

                // pull our tenant info to populate the "title" field here
                DatabaseReference tenantInfoRef = firebaseDatabase.getReference().child(companyCode).child("1")
                        .child("tenants").child(complaint.getComplaintUser());

                // add our single event listener
                tenantInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Tenant complaintTenant = dataSnapshot.getValue(Tenant.class);

                        if (complaintTenant != null) {
                            try {
                                holder.complaintTitle.setText(complaintTenant.getTenantFirstName() + " " + complaintTenant.getTenantLastName());
                                holder.complaintStatus.setVisibility(View.GONE);
                                holder.complaintDate.setVisibility(View.GONE);
                                holder.managerComplaintTitle.setVisibility(View.VISIBLE);
                                holder.managerComplaintTitle.setText(complaint.getComplaintTitle());

                                // set our on click listener
                                holder.setOnClickListener(new ComplaintViewHolder.ClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        // send our manager to the complaint view where they can acknowledge and respond with comment
                                        Boolean isClosed = false;
                                        if (complaintType.equals("closed")) {
                                            isClosed = true;
                                        }
                                        onComplaintAcknowledgementListener.displayComplaint(complaint, complaintTenant, isClosed);
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        // call our recycler
        complaintView.setAdapter(complaintAdapter);
        complaintView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        complaintAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        complaintAdapter.stopListening();
    }
}