package com.project3w.properts.Helpers;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project3w.properts.Objects.AccountVerification;
import com.project3w.properts.Objects.Tenant;

import java.util.HashMap;

public class FirebaseDataHelper {

    private Activity mActivity;

    public FirebaseDataHelper(Activity activity) {
        mActivity = activity;
    }


    public void saveTenant(Tenant tenant) {

        // assign phone number to the tenantID of tenant object
        String tenantID = tenant.getTenantPhone();
        tenant.setTenantID(tenantID);

        // create AccountVerification object
        AccountVerification accountVerification = new AccountVerification(tenant.getTenantName(), tenant.getTenantAddress());

        // get Firebase Database instances
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tenantDataRef = database.getReference("tenants");
        DatabaseReference needAccountRef = database.getReference("needAccount");

        // create the account verification map
        HashMap<String, Object> needAccount = new HashMap<>();
        needAccount.put(tenantID, accountVerification);

        // update database with account creation needs
        needAccountRef.updateChildren(needAccount);

        // create HashMap for updating tenants
        HashMap<String, Object> newTenant = new HashMap<>();
        newTenant.put(tenant.getTenantID(), tenant);

        // save the tenant
        tenantDataRef.updateChildren(newTenant);

        // concatenate tenant message
        String tenantMessage = "Tenant: " + tenant.getTenantName() + " created successfully!";

        // show success message
        Snackbar.make(mActivity.findViewById(android.R.id.content), tenantMessage, Snackbar.LENGTH_LONG).show();
    }

    /*public void saveTripItem(String tripId, TripItem tripItem) {

        // get our firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tripItemsRef = database.getReference("tripitems/" + tripId);

        // grab our StorageReference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://wharrynathantripjournal.appspot.com");
        StorageReference saveLocationRef = storageRef.child("tripimages/" + tripId);

        // get our Uri File reference
        Uri imageUri = Uri.fromFile(new File(tripItem.getItemImageUri()));

        // set our image name and tripId
        tripItem.setImageName(imageUri.getLastPathSegment());
        tripItem.setTripId(tripId);

        // push in our trip item
        tripItemsRef.push().setValue(tripItem);

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


    }*/
}
