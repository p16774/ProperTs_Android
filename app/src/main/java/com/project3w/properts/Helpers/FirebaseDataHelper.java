package com.project3w.properts.Helpers;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project3w.properts.Objects.AccountVerification;
import com.project3w.properts.Objects.Tenant;

import java.util.HashMap;

public class FirebaseDataHelper {

    // class variables
    private Activity mActivity;

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
            tenant.setUserID(""); // added to prevent iOS code from crashing

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

    public boolean updateNewTenantAccount(String tenantID, String userID) {

        // method variables
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference removedNeedAccountRef = firebaseDatabase.getReference("needAccount").child(tenantID);
        DatabaseReference linkTenantAccountRef = firebaseDatabase.getReference("tenants").child(tenantID).child("userID");
        DatabaseReference userRoleAccessRef = firebaseDatabase.getReference("users").child(userID);

        // try catch block to test that the calls are completing correctly
        try {
            // remove the needAccount reference
            removedNeedAccountRef.removeValue();

            // update tenant with current userID
            linkTenantAccountRef.setValue(userID);

            // set their access role and tenant data ID
            userRoleAccessRef.child("role").setValue("tenant");
            userRoleAccessRef.child("tenantID").setValue(tenantID);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
