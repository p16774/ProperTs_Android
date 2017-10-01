package com.project3w.properts.Helpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project3w.properts.Objects.Tenant;

import java.util.HashMap;
import java.util.UUID;

public class FirebaseDataHelper {

    Activity mActivity;

    public FirebaseDataHelper(Activity activity) {
        mActivity = activity;
    }


    public void saveTenant(Tenant tenant) {

        // generate random number for tenant association and user creation
        // generate unique id for tripId and database reference
        long genericLong = UUID.randomUUID().getLeastSignificantBits();
        String significantLong = genericLong + "";
        String tenantID = significantLong.substring(1);



        // get Firebase Database instances
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tenantDataRef = database.getReference("tenants");

        // create HashMap for updating trips
        HashMap<String, Object> newTenant = new HashMap<>();
        newTenant.put(tenant.getTenantID(), tenant);

        // save the trip
        tenantDataRef.updateChildren(newTenant);

        // create HashMap for updating user with trip
        HashMap<String, Object> updateUser = new HashMap<>();
        updateUser.put(trip.getTripId(), true);

        // update the user
        userDataRef.updateChildren(updateUser);

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
