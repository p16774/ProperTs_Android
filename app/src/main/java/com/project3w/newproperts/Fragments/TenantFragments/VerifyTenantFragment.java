package com.project3w.newproperts.Fragments.TenantFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.MainActivity;
import com.project3w.newproperts.Objects.AccountVerification;
import com.project3w.newproperts.R;

/**
 * Created by Nate on 10/15/17.
 */

public class VerifyTenantFragment extends Fragment implements View.OnClickListener {

    // class variables
    Activity mActivity;
    EditText lastNameView, verifyCodeView;
    Button verifyAccountBtn;
    FirebaseDataHelper firebaseDataHelper;

    public VerifyTenantFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // create our view
        View view = inflater.inflate(R.layout.activity_verify, container, false);

        // grab our variable references
        mActivity = getActivity();
        lastNameView = view.findViewById(R.id.verify_last_name);
        verifyCodeView = view.findViewById(R.id.verify_code);
        verifyAccountBtn = view.findViewById(R.id.verify_btn);
        firebaseDataHelper = new FirebaseDataHelper(mActivity);

        // assign our click listener
        verifyAccountBtn.setOnClickListener(this);

        mActivity.setTitle("Verify Tenant");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        // grab our entered text
        String lastName = lastNameView.getText().toString().trim();
        String verifyCode = verifyCodeView.getText().toString().trim();

        // verify they have entered data into both fields
        if(lastName.isEmpty() || verifyCode.isEmpty()) {
            Snackbar.make(mActivity.findViewById(android.R.id.content), "You must fill out both fields to continue", Snackbar.LENGTH_LONG).show();
        } else {
            // grab our current userID and assign our data points in FirebaseDataHelper
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userID = currentUser.getUid();
                // validate tenant information and assign data before sending tenant to their home page
                updateNewTenantAccount(verifyCode, userID);
            }
        }
    }

    public void updateNewTenantAccount(final String tenantID, final String userID) {

        // method variables
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference removedNeedAccountRef = firebaseDatabase.getReference().child("needAccount").child(tenantID);

        // set our value listener to get the companyCode to assign the user and tenant correctly
        removedNeedAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AccountVerification userData = dataSnapshot.getValue(AccountVerification.class);
                if (userData != null) {
                    DatabaseReference linkTenantAccountRef = firebaseDatabase.getReference()
                            .child(userData.getCompanyCode())
                            .child("1")
                            .child("tenants").child(tenantID).child("userID");

                    // update our user reference and assign the accessRole
                    firebaseDataHelper.createUserReference(userData.getCompanyCode(), tenantID, "tenant");

                    // try catch block to test that the calls are completing correctly
                    try {
                        // update tenant with current userID
                        linkTenantAccountRef.setValue(userID);

                        // remove the needAccount reference last (just in case)
                        removedNeedAccountRef.removeValue();

                        // send user to the MainActivity
                        Intent sendToMainIntent = new Intent(mActivity, MainActivity.class);
                        //sendToMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(sendToMainIntent);
                        mActivity.finish();

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
}
