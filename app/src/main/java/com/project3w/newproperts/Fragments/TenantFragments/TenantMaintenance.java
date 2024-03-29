package com.project3w.newproperts.Fragments.TenantFragments;

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
import static com.project3w.newproperts.MainActivity.TENANT_ID;

/**
 * Created by Nate on 10/7/17.
 */

public class TenantMaintenance extends Fragment {

    // class variables
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Activity mActivity;
    RecyclerView requestView;
    FirebaseRecyclerAdapter requestAdapter;
    String companyCode, tenantID;

    public interface AddNewRequestListener {
        void addNewRequest();
    }

    public interface DisplayRequestListener {
        void displayRequest(Request request);
    }

    AddNewRequestListener onAddRequestListener;
    DisplayRequestListener onDisplayRequestListener;

    public TenantMaintenance() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mActivity = getActivity();

        // set our options menus
        setHasOptionsMenu(true);

        if (mUser == null) {
            // send the user back to the login screen
            Intent loginScreen = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginScreen);
            getActivity().finish();
        } else {
            // attach our listener
            try {
                onAddRequestListener = (AddNewRequestListener) mActivity;
                onDisplayRequestListener = (DisplayRequestListener) mActivity;
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.toString() + " must implement AddNewRequestListener");
            }
        }

        SharedPreferences mPrefs = mActivity.getSharedPreferences("com.project3w.properts", Context.MODE_PRIVATE);
        companyCode = mPrefs.getString(COMPANY_CODE, "");
        tenantID = mPrefs.getString(TENANT_ID, "");

        mActivity.setTitle("Request List");

        return inflater.inflate(R.layout.tenant_maintenance, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.tenant_menu, menu);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setup our fab to add new requests
        final FloatingActionButton fab = getActivity().findViewById(R.id.maintenance_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddRequestListener.addNewRequest();
            }
        });

        // grab the reference to our RecyclerView
        requestView = getActivity().findViewById(R.id.request_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        // setup our database references
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query tenantRequestsQuery = firebaseDatabase.getReference().child(companyCode).child("1").child("requests")
                .child(mUser.getUid())
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
            protected void onBindViewHolder(RequestViewHolder holder, int position, final Request model) {

                try {
                    holder.requestTitle.setText(model.getRequestTitle());
                    holder.requestStatus.setText(model.getRequestStatus());
                    holder.requestDate.setText(model.getRequestDate());

                    holder.setOnClickListener(new RequestViewHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            onDisplayRequestListener.displayRequest(model);
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
        };

        // call our recycler
        requestView.setAdapter(requestAdapter);
        requestView.setLayoutManager(layoutManager);

        // disable the add fab if the tenant is not active
        DatabaseReference tenantData = firebaseDatabase.getReference().child(companyCode).child("1").child("tenants").child(tenantID);
        tenantData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tenant tenant = dataSnapshot.getValue(Tenant.class);
                if(tenant != null){
                    if(!tenant.getTenantStatus()) {
                        fab.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
