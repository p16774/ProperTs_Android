package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.project3w.newproperts.Helpers.RequestViewHolder;
import com.project3w.newproperts.LoginActivity;
import com.project3w.newproperts.Objects.Request;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.R;

import static com.project3w.newproperts.MainActivity.COMPANY_CODE;

/**
 * Created by Nate on 10/16/17.
 */

public class ManagerMaintenance extends Fragment {

    // class variables
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Activity mActivity;
    RecyclerView requestView;
    FirebaseRecyclerAdapter requestAdapter;
    String companyCode;

    public ManagerMaintenance() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mActivity = getActivity();

        // set our options menu
        setHasOptionsMenu(true);

        if (mUser == null) {
            // send the user back to the login screen
            Intent loginScreen = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginScreen);
            getActivity().finish();
        }

        SharedPreferences mPrefs = mActivity.getSharedPreferences("com.project3w.properts", Context.MODE_PRIVATE);
        companyCode = mPrefs.getString(COMPANY_CODE, null);

        mActivity.setTitle("Tenant Requests");

        return inflater.inflate(R.layout.manager_maintenance, container, false);
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
        requestView = getActivity().findViewById(R.id.manager_request_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        // setup our database references
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query tenantRequestsQuery = firebaseDatabase.getReference().child(companyCode).child("1").child("requests")
                .child("active")
                .orderByChild("complaintDate");

        // setup our RecyclerView to display content
        FirebaseRecyclerOptions<Request> maintenanceOptions =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(tenantRequestsQuery, Request.class)
                        .build();

        requestAdapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(maintenanceOptions) {
            @Override
            public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_request, parent, false);
                return new RequestViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final RequestViewHolder holder, int position, final Request request) {

                // pull our tenant info to populate the "title" field here
                DatabaseReference tenantInfoRef = firebaseDatabase.getReference().child(companyCode).child("1")
                        .child("tenants").child(request.getRequestUser());

                /*// display our progress dialog box
                final ProgressDialog Dialog = new ProgressDialog(mActivity);
                Dialog.setMessage("Doing something...");
                Dialog.show();*/

                // pull in our tenant info and set our fields appropriately
                tenantInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Tenant requestTenant = dataSnapshot.getValue(Tenant.class);

                        if (requestTenant != null) {
                            try {
                                holder.requestTitle.setText(requestTenant.getTenantFirstName() + " " + requestTenant.getTenantLastName());
                                holder.requestStatus.setVisibility(View.GONE);
                                holder.requestDate.setVisibility(View.GONE);
                                holder.managerRequestTitle.setVisibility(View.VISIBLE);
                                holder.managerRequestTitle.setText(request.getRequestTitle());

                                holder.setOnClickListener(new RequestViewHolder.ClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                       // onDisplayRequestListener.displayRequest(request);
                                    }

                                    @Override
                                    public void onItemLongClick(View view, int position) {
                                        //TODO: option to cancel request???
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //Dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        // call our recycler
        requestView.setAdapter(requestAdapter);
        requestView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        requestAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        requestAdapter.stopListening();
    }
}
