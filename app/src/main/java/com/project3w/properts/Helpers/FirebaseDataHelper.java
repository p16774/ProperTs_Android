package com.project3w.properts.Helpers;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project3w.properts.Objects.AccountVerification;
import com.project3w.properts.Objects.Tenant;
import com.project3w.properts.R;

import java.util.HashMap;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class FirebaseDataHelper {

    // class variables
    Activity mActivity;
    FirebaseUser mUser;
    FirebaseAuth mAuth;

    public FirebaseDataHelper(Activity activity) {
        mActivity = activity;
    }


    public void saveTenant(Tenant tenant, boolean newAccount ) {

        // get Firebase Database instances
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tenantDataRef = database.getReference("tenants");
        DatabaseReference needAccountRef = database.getReference("needAccount");
        DatabaseReference unitTenantRef = database.getReference("currentTenants").child(tenant.getTenantAddress());

        // concatenate tenant message
        String tenantMessage = "Tenant: " + tenant.getTenantName() + " updated successfully!";

        // check for new account and run steps necessary for new tenant creation
        if (newAccount) {
            // assign phone number to the tenantID of tenant object
            String tenantID = tenant.getTenantPhone();
            tenant.setTenantID(tenantID);

            // create AccountVerification object
            AccountVerification accountVerification = new AccountVerification(tenant.getTenantName(), tenant.getTenantAddress());

            // create the account verification map
            HashMap<String, Object> needAccount = new HashMap<>();
            needAccount.put(tenantID, accountVerification);

            // update database with account creation needs
            needAccountRef.updateChildren(needAccount);

            // concatenate tenant message
            tenantMessage = "Tenant: " + tenant.getTenantName() + " created successfully!";
        }

        // create the currentTenant value
        unitTenantRef.setValue(tenant.getTenantID());

        // create HashMap for updating tenants
        HashMap<String, Object> newTenant = new HashMap<>();
        newTenant.put(tenant.getTenantID(), tenant);

        // save the tenant
        tenantDataRef.updateChildren(newTenant);

        // show success message
        Snackbar.make(mActivity.findViewById(android.R.id.content), tenantMessage, Snackbar.LENGTH_LONG).show();
    }

    public boolean verifyAccount(final String tenantID) {

        //TODO: add a progress indication to show the user we are verifying their account

        // sign in user anonymously
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            mUser = mAuth.getCurrentUser();

                            // pull data and verify id
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference tenantVerifyRef = firebaseDatabase.getReference("needAccount").child(tenantID);

                            // verify the tenantID and display information
                            tenantVerifyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // assign data from database pull
                                    AccountVerification av = dataSnapshot.getValue(AccountVerification.class);
                                    //TODO: validate for null
                                    final String tenantName = av.getTenantName();
                                    String tenantAddress = av.getTenantAddress();

                                    // Use the Builder class for convenient dialog construction
                                    new MaterialDialog.Builder(mActivity)
                                            .title("Confirm Account")
                                            .content("Please confirm that you are " + tenantName + "\nand you are moving into " + tenantAddress + ".")
                                            .positiveText("Confirm")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    Snackbar.make(mActivity.findViewById(android.R.id.content), tenantName, Snackbar.LENGTH_LONG).show();
                                                }
                                            })
                                            .positiveColorRes(R.color.colorBlack)
                                            .negativeText("Deny")
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .negativeColorRes(R.color.colorGrey)
                                            .cancelable(false)
                                            .build()
                                            .show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });

        return true;
    }
}
