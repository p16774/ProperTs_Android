package com.project3w.newproperts.Fragments.TenantFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project3w.newproperts.Helpers.GlideApp;
import com.project3w.newproperts.LoginActivity;
import com.project3w.newproperts.Objects.Company;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.R;

import static com.project3w.newproperts.MainActivity.COMPANY_CODE;

/**
 * Created by Nate on 10/5/17.
 */

public class TenantHome extends Fragment {

    // class variables
    TextView tenantNameView, tenantAddressView, tenantPhoneView, tenantEmailView;
    ImageView companyImageView;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    Tenant currentTenant;
    Activity mActivity;
    String companyCode;

    public TenantHome() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // set options menu
        setHasOptionsMenu(true);

        mActivity.setTitle("Home");

        return inflater.inflate(R.layout.tenant_home, container, false);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.tenant_menu, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // pull in our view references
        tenantNameView = mActivity.findViewById(R.id.disp_tenant_name);
        tenantAddressView = mActivity.findViewById(R.id.disp_tenant_address);
        tenantPhoneView = mActivity.findViewById(R.id.disp_tenant_phone);
        tenantEmailView = mActivity.findViewById(R.id.disp_tenant_email);
        companyImageView = mActivity.findViewById(R.id.company_tenant_image);
        companyImageView.setVisibility(View.GONE);

        // grab our company code from shared preferences
        SharedPreferences mPrefs = mActivity.getSharedPreferences("com.project3w.properts", Context.MODE_PRIVATE);
        companyCode = mPrefs.getString(COMPANY_CODE, "");

        // call user data
        displayUserData();

    }

    public void displayUserData() {

        // pull user data to populate Tenant object
        DatabaseReference userDataRef = firebaseDatabase.getReference().child("users").child(mUser.getUid()).child("tenantID");
        DatabaseReference companyImageRef = firebaseDatabase.getReference().child("companies").child(companyCode);

        companyImageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Company myCompany = dataSnapshot.getValue(Company.class);
                if (myCompany != null) {

                    System.out.println(myCompany);

                    // get our storage reference
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://properts-8db06.appspot.com/");
                    StorageReference imageOpenRef = null;

                    // check for value on open image before pulling file
                    if (!myCompany.getCompanyImagePath().isEmpty()) {
                        imageOpenRef = storageRef.child("companyImages/" + companyCode + "/" + myCompany.getCompanyImagePath());

                        companyImageView.setVisibility(View.VISIBLE);
                        // download and set our imageview
                        GlideApp.with(getActivity())
                                .load(imageOpenRef)
                                .into(companyImageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // STUPID FIREBASE!!!!!!
        // get our data through a mass coding call
        userDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // pull our tenantID
                if (dataSnapshot.getValue() != null) {
                    String tenantID = dataSnapshot.getValue().toString();

                    // STUPID FIREBASE FOR NESTED DATA CALLS!!!!!!!!!!
                    DatabaseReference tenantInfoRef = firebaseDatabase.getReference().child(companyCode).child("1")
                            .child("tenants").child(tenantID);
                    tenantInfoRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // assign our data to a Tenant object
                            currentTenant = dataSnapshot.getValue(Tenant.class);

                            // check for null and assign our data to the views
                            if (currentTenant != null) {
                                tenantNameView.setText(currentTenant.getTenantFirstName() + " " + currentTenant.getTenantLastName());
                                tenantAddressView.setText(currentTenant.getTenantAddress());
                                tenantPhoneView.setText(currentTenant.getTenantPhone());
                                tenantEmailView.setText(currentTenant.getTenantEmail());
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
