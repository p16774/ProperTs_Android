package com.project3w.newproperts.Fragments.StaffFragments;

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
import com.project3w.newproperts.Objects.StaffVerification;
import com.project3w.newproperts.Objects.TenantVerification;
import com.project3w.newproperts.R;

import static com.project3w.newproperts.Fragments.TenantFragments.VerifyTenantFragment.ACCESS_TYPE;

/**
 * Created by Nate on 10/20/17.
 */

public class VerifyStaffFragment extends Fragment {

    // class variables
    Activity mActivity;
    EditText staffNameView, verifyCodeView;
    Button verifyAccountBtn;
    FirebaseDataHelper firebaseDataHelper;
    FirebaseAuth mAuth;

    public VerifyStaffFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // create our view
        View view = inflater.inflate(R.layout.activity_verify, container, false);

        // grab our variable references
        mActivity = getActivity();
        staffNameView = view.findViewById(R.id.verify_last_name);
        verifyCodeView = view.findViewById(R.id.verify_code);
        verifyAccountBtn = view.findViewById(R.id.verify_btn);
        firebaseDataHelper = new FirebaseDataHelper(mActivity);

        // hide the name - it's not needed
        staffNameView.setVisibility(View.GONE);

        mActivity.setTitle("Verify Staff");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // assign our click listener
        verifyAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // grab our entered text
                String verifyCode = verifyCodeView.getText().toString().trim();

                // verify they have entered data into both fields
                if(verifyCode.isEmpty()) {
                    Snackbar.make(mActivity.findViewById(android.R.id.content), "You must enter a verification code to continue", Snackbar.LENGTH_SHORT).show();
                } else {
                    // grab our current userID and assign our data points in FirebaseDataHelper
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String userID = currentUser.getUid();
                        // validate tenant information and assign data before sending tenant to their home page
                        updateNewStaffAccount(verifyCode, userID);
                    }
                }
            }
        });
    }

    public void updateNewStaffAccount(final String staffID, final String userID) {

        // method variables
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference removedNeedAccountRef = firebaseDatabase.getReference().child("needsAccount").child(staffID);

        // set our value listener to get the companyCode to assign the user and staff correctly
        removedNeedAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StaffVerification userData = dataSnapshot.getValue(StaffVerification.class);
                if (userData != null) {
                    DatabaseReference linkStaffAccountRef = firebaseDatabase.getReference()
                            .child(userData.getCompanyCode())
                            .child("1")
                            .child("staff").child(staffID).child("userID");

                    // update our user reference and assign the accessRole
                    firebaseDataHelper.updateUserReference(userData.getCompanyCode(), staffID, "staff");

                    // try catch block to test that the calls are completing correctly
                    try {
                        // update staff with current userID
                        linkStaffAccountRef.setValue(userID);

                        // remove the needAccount reference last (just in case)
                        removedNeedAccountRef.removeValue();

                        // send user to the MainActivity
                        Intent sendToMainIntent = new Intent(mActivity, MainActivity.class);
                        sendToMainIntent.putExtra(ACCESS_TYPE, "staff");
                        startActivity(sendToMainIntent);
                        mActivity.finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Snackbar.make(mActivity.findViewById(android.R.id.content), "Invalid Verification Code", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        // close this activity to force the user to login
        mActivity.finish();
    }
}

