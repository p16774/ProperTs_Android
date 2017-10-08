package com.project3w.properts.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project3w.properts.Helpers.RequestViewHolder;
import com.project3w.properts.LoginActivity;
import com.project3w.properts.Objects.MaintenanceRequest;
import com.project3w.properts.R;

/**
 * Created by Nate on 10/7/17.
 */

public class TenantMaintenance extends Fragment {

    // class variables
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    RecyclerView requestView;
    FirebaseRecyclerAdapter requestAdapter;

    public TenantMaintenance() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser == null) {
            // send the user back to the login screen
            Intent loginScreen = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginScreen);
            getActivity().finish();
        }

        return inflater.inflate(R.layout.tenant_maintenance, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // grab the reference to our RecyclerView
        requestView = getActivity().findViewById(R.id.request_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        // setup our database references
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query tenantRequestsQuery = firebaseDatabase.getReference("requests")
                .child(mUser.getUid())
                .orderByChild("maintenanceDate");

        // setup our RecyclerView to display content
        FirebaseRecyclerOptions<MaintenanceRequest> maintenanceOptions =
                new FirebaseRecyclerOptions.Builder<MaintenanceRequest>()
                .setQuery(tenantRequestsQuery, MaintenanceRequest.class)
                .build();

        requestAdapter = new FirebaseRecyclerAdapter<MaintenanceRequest, RequestViewHolder>(maintenanceOptions) {
            @Override
            public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_request, parent, false);
                return new RequestViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(RequestViewHolder holder, int position, MaintenanceRequest model) {

                try {
                    holder.requestTitle.setText(model.getRequestTitle());
                    holder.requestStatus.setText(model.getRequestStatus());
                    holder.requestDate.setText(model.getRequestDate());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
