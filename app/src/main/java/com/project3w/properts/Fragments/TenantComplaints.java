package com.project3w.properts.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project3w.properts.Helpers.ComplaintViewHolder;
import com.project3w.properts.LoginActivity;
import com.project3w.properts.Objects.Complaint;
import com.project3w.properts.R;

/**
 * Created by Nate on 10/11/17.
 */

public class TenantComplaints extends Fragment {

    // class variables
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Activity mActivity;
    RecyclerView complaintView;
    FirebaseRecyclerAdapter complaintAdapter;

    public interface AddNewComplaintListener {
        void addNewComplaint();
    }

    AddNewComplaintListener onAddNewComplaintListener;

    public TenantComplaints() {
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
                onAddNewComplaintListener = (AddNewComplaintListener) mActivity;
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.toString() + " must implement AddNewRequestListener");
            }
        }

        return inflater.inflate(R.layout.tenant_complaints, container, false);
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
        FloatingActionButton fab = getActivity().findViewById(R.id.complaint_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddNewComplaintListener.addNewComplaint();
            }
        });

        // grab the reference to our RecyclerView
        complaintView = getActivity().findViewById(R.id.complaint_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        // setup our database references
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query tenantComplaintQuery = firebaseDatabase.getReference("complaints")
                .child(mUser.getUid())
                .orderByChild("complaintDate");

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
            protected void onBindViewHolder(ComplaintViewHolder holder, int position, final Complaint complaint) {

                try {
                    holder.complaintTitle.setText(complaint.getComplaintTitle());
                    holder.complaintStatus.setText(complaint.getComplaintStatus());
                    holder.complaintDate.setText(complaint.getComplaintDate());

                    holder.setOnClickListener(new ComplaintViewHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            new MaterialDialog.Builder(getActivity())
                                    .title("Complaint: " + complaint.getComplaintID())
                                    .content("Submitted: " + complaint.getComplaintDate() +
                                            "\nStatus: " + complaint.getComplaintStatus() +
                                            "\n\n" + complaint.getComplaintContent())
                                    .positiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .positiveColorRes(R.color.colorBlack)
                                    .cancelable(true)
                                    .build()
                                    .show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
