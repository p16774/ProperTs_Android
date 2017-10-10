package com.project3w.properts.Helpers;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project3w.properts.Objects.AccountVerification;
import com.project3w.properts.Objects.Request;
import com.project3w.properts.Objects.Tenant;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    public boolean submitMaintenanceRequest(Request request) {

        // get firebase database instance and reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        if (currentUser != null) {

            // get our location reference
            DatabaseReference newRequestRef = firebaseDatabase.getReference("requests").child(currentUser.getUid());

            // create our key to update
            String requestKey = newRequestRef.push().getKey();

            // grab our StorageReference
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://properts-e2eaf.appspot.com");
            StorageReference saveLocationRef = storageRef.child("requestImages/" + currentUser.getUid() + "/" + requestKey);

            // get our Uri File reference
            Uri imageUri = Uri.fromFile(new File(request.getRequestOpenImagePath()));

            // grab our image reference and Uri for File
            StorageReference imageRef = saveLocationRef.child(imageUri.getLastPathSegment());

            // register UploadTask and putFile
            UploadTask uploadTask = imageRef.putFile(imageUri);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Snackbar.make(mActivity.findViewById(android.R.id.content), "Image Upload Failed!!", Snackbar.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Snackbar.make(mActivity.findViewById(android.R.id.content), "Image Successfully Uploaded", Snackbar.LENGTH_LONG).show();
                }
            });

            // add in our key and empty closed image path and update our image path to Firebase Storage location
            request.setRequestID(requestKey);
            request.setRequestClosedImagePath("");
            request.setRequestOpenImagePath(imageUri.getLastPathSegment());

            // create our map to updateChildren
            Map<String, Object> requestData = request.toMap();
            requestData.put(requestKey, request);
            // update data
            newRequestRef.child(requestKey).setValue(requestData);
            return true;
        }

        return false;
    }
}
