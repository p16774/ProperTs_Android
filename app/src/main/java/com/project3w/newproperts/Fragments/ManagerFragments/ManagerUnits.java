package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.project3w.newproperts.Helpers.UnitViewHolder;
import com.project3w.newproperts.LoginActivity;
import com.project3w.newproperts.Objects.Unit;
import com.project3w.newproperts.R;

import static com.project3w.newproperts.MainActivity.COMPANY_CODE;

/**
 * Created by Nate on 10/14/17.
 */

public class ManagerUnits extends Fragment {

    // class variables
    Activity mActivity;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    RecyclerView unitView;
    FirebaseRecyclerAdapter unitAdapter;
    String companyCode;


    public interface AddNewUnitListener {
        void addNewUnit();
    }

    public interface EditUnitListener {
        void editUnit(Unit unit);
    }

    AddNewUnitListener onAddNewUnitListener;
    EditUnitListener onEditUnitListener;

    public ManagerUnits() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // inflate view
        View view = inflater.inflate(R.layout.manager_units, container, false);

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
                onAddNewUnitListener = (AddNewUnitListener) mActivity;
                onEditUnitListener = (EditUnitListener) mActivity;
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.toString() + " must implement AddNewRequestListener");
            }
        }

        mActivity.setTitle("Rental Units List");

        // assign our recycler view
        unitView = view.findViewById(R.id.unit_list);

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
        FloatingActionButton fab = getActivity().findViewById(R.id.unit_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddNewUnitListener.addNewUnit();
            }
        });

        // grab the reference to our RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        // setup our database references
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query tenantRequestsQuery = firebaseDatabase.getReference()
                .child(companyCode)
                .child("1")
                .child("units");

        // setup our RecyclerView to display content
        FirebaseRecyclerOptions<Unit> unitOptions =
                new FirebaseRecyclerOptions.Builder<Unit>()
                        .setQuery(tenantRequestsQuery, Unit.class)
                        .build();

        unitAdapter = new FirebaseRecyclerAdapter<Unit, UnitViewHolder>(unitOptions) {
            @Override
            public UnitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_units, parent, false);
                return new UnitViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UnitViewHolder holder, int position, final Unit model) {

                try {
                    holder.unitAddress.setText(model.getUnitAddress());

                    holder.setOnClickListener(new UnitViewHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            onEditUnitListener.editUnit(model);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // call our recycler
        unitView.setAdapter(unitAdapter);
        unitView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        unitAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        unitAdapter.stopListening();
    }


}
