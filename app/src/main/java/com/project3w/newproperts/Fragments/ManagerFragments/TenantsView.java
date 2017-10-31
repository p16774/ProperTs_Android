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
import com.project3w.newproperts.Helpers.TenantViewHolder;
import com.project3w.newproperts.LoginActivity;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.R;

import static com.project3w.newproperts.MainActivity.COMPANY_CODE;

/**
 * Created by Nate on 10/15/17.
 */

public class TenantsView extends Fragment {

    // class variables
    Activity mActivity;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    RecyclerView tenantView;
    FirebaseRecyclerAdapter tenantAdapter;
    String companyCode;
    Boolean tenantStatus;

    public static final String TENANT_STATUS = "com.project3w.properts.TENANT_STATUS";

    public interface EditTenantFragmentListener {
        void editTenant(Tenant tenant, Boolean status);
    }

    EditTenantFragmentListener onEditTenantFragmentListener;
    // call in our menu selector from the home screen that direct opens our add tenant fragment to reuse code
    ManagerHome.MenuOptionSelectedListener onMenuOptionSelectedListener;


    public TenantsView newInstance(Boolean status) {

        TenantsView myFragment = new TenantsView();
        Bundle args = new Bundle();
        args.putBoolean(TENANT_STATUS,status);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        tenantStatus = getArguments().getBoolean(TENANT_STATUS, true);

        // inflate view
        View view = inflater.inflate(R.layout.manager_tenants, container, false);

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
                onMenuOptionSelectedListener = (ManagerHome.MenuOptionSelectedListener) mActivity;
                onEditTenantFragmentListener = (EditTenantFragmentListener) mActivity;
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.toString() + " must implement ManagerHome.MenuOptionSelectedListener");
            }
        }

        mActivity.setTitle("Tenants List");

        // assign our recycler view
        tenantView = view.findViewById(R.id.tenant_list);

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
        FloatingActionButton fab = getActivity().findViewById(R.id.tenant_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuOptionSelectedListener.openMenuOption("tenants");
            }
        });
        if(!tenantStatus) {
            fab.setVisibility(View.GONE);
        }

        // grab the reference to our RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //layoutManager.setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);

        // setup our database references
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query tenantRequestsQuery = firebaseDatabase.getReference()
                .child(companyCode)
                .child("1")
                .child("tenants")
                .orderByChild("tenantStatus")
                .equalTo(tenantStatus);

        // setup our RecyclerView to display content
        FirebaseRecyclerOptions<Tenant> tenantOptions =
                new FirebaseRecyclerOptions.Builder<Tenant>()
                        .setQuery(tenantRequestsQuery, Tenant.class)
                        .build();

        tenantAdapter = new FirebaseRecyclerAdapter<Tenant, TenantViewHolder>(tenantOptions) {
            @Override
            public TenantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_tenants, parent, false);
                return new TenantViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(TenantViewHolder holder, int position, final Tenant model) {

                try {
                    String fullName = model.getTenantFirstName() + " " + model.getTenantLastName();
                    holder.tenantName.setText(fullName);
                    holder.tenantAddress.setText(model.getTenantAddress());

                    holder.setOnClickListener(new TenantViewHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            onEditTenantFragmentListener.editTenant(model, tenantStatus);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // call our recycler
        tenantView.setAdapter(tenantAdapter);
        tenantView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        tenantAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        tenantAdapter.stopListening();
    }

}
