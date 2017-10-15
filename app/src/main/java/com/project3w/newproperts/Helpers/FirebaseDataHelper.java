package com.project3w.newproperts.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.google.android.gms.common.data.Freezable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project3w.newproperts.Objects.AccountVerification;
import com.project3w.newproperts.Objects.Company;
import com.project3w.newproperts.Objects.Complaint;
import com.project3w.newproperts.Objects.Request;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.Objects.Unit;
import com.project3w.newproperts.Objects.User;

import java.io.File;
import java.util.HashMap;

import static com.project3w.newproperts.MainActivity.COMPANY_CODE;

public class FirebaseDataHelper {

    // class variables
    private Activity mActivity;
    private String companyCode;
    private FirebaseDatabase firebaseDatabase;

    public FirebaseDataHelper(Activity activity) {
        mActivity = activity;
        SharedPreferences mPrefs = mActivity.getSharedPreferences("com.project3w.properts", Context.MODE_PRIVATE);
        companyCode = mPrefs.getString(COMPANY_CODE, null);
        firebaseDatabase = FirebaseDatabase.getInstance();
    }


    public void saveTenant(Tenant tenant, boolean newAccount ) {

        // get Firebase Database instances
        DatabaseReference tenantDataRef = firebaseDatabase.getReference().child(companyCode).child("1").child("tenants");
        DatabaseReference needAccountRef = firebaseDatabase.getReference("needAccount");
        DatabaseReference unitTenantRef = firebaseDatabase.getReference().child(companyCode).child("1")
                                                    .child("currentTenants").child(tenant.getTenantAddress());

        // concatenate tenant message
        String tenantMessage = "Tenant: " + tenant.getTenantFirstName() + " " + tenant.getTenantLastName() + " updated successfully!";

        // check for new account and run steps necessary for new tenant creation
        if (newAccount) {
            // assign phone number to the tenantID of tenant object
            String tenantID = tenant.getTenantPhone();
            tenant.setTenantID(tenantID);
            tenant.setUserID(""); // added to prevent iOS code from crashing

            // create AccountVerification object
            AccountVerification accountVerification = new AccountVerification(tenant.getTenantLastName(), tenant.getTenantAddress());

            // create the account verification map
            HashMap<String, Object> needAccount = new HashMap<>();
            needAccount.put(tenantID, accountVerification);

            // update database with account creation needs
            needAccountRef.updateChildren(needAccount);

            // concatenate tenant message
            tenantMessage = "Tenant: " + tenant.getTenantFirstName() + " " + tenant.getTenantLastName() + " created successfully!";
        }

        // create the currentTenant value
        unitTenantRef.setValue(tenant.getTenantID());

        // validate for null on userID - this is a check should the manager update something in the account before the user
        // creates their user account and the tenant object gets updated
        if(tenant.getUserID() == null) {
            tenant.setUserID("");
        }

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

        if (currentUser != null) {

            // get our location reference
            DatabaseReference newRequestRef = firebaseDatabase.getReference("requests").child(currentUser.getUid());

            // create our key to update
            String requestKey = newRequestRef.push().getKey();

            // grab our StorageReference
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://properts-e2eaf.appspot.com");
            StorageReference saveLocationRef = storageRef.child("requestImages/" + requestKey);

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

            // update data
            newRequestRef.child(requestKey).setValue(request);
            return true;
        }

        return false;
    }

    public boolean submitComplaint(Complaint complaint) {

        // get firebase database instance and reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            // get our location reference
            DatabaseReference newComplaintRef = firebaseDatabase.getReference("complaints").child(currentUser.getUid());

            // create our key to update
            String complaintKey = newComplaintRef.push().getKey();

            // add in our key and empty closed image path and update our image path to Firebase Storage location
            complaint.setComplaintID(complaintKey);

            // update data
            newComplaintRef.child(complaintKey).setValue(complaint);
            return true;
        }

        return false;
    }

    public void createUserReference(String companyCode, String tenantID, String accessType) {
        // get our current userID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            // get our firebase reference
            String userID = mUser.getUid();
            DatabaseReference newUserRef = firebaseDatabase.getReference().child("users").child(userID);
            DatabaseReference companyUserRef = firebaseDatabase.getReference().child(companyCode).child("1").child("users").child(userID);

            // create the user and the company/user references
            User createUser = new User(companyCode, "1", tenantID, accessType);
            newUserRef.setValue(createUser);
            companyUserRef.setValue(createUser);
        }
    }

    public void createCompany(String companyName) {

        // create our company reference and get firebase key
        DatabaseReference createCompanyRef = firebaseDatabase.getReference().child("companies");
        String companyCode = createCompanyRef.push().getKey();

        // assign values to our company
        Company newCompany = new Company(companyCode, companyName);
        createCompanyRef.child(companyCode).setValue(newCompany);

        // create our manager user inside our new company/property
        createUserReference(companyCode, "", "manager");
    }

    public void setSharedCompanyCode() {
        // get our user data
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        // validate for null
        if (mUser != null) {
            // get firebase references
            DatabaseReference companyCodeRef = firebaseDatabase.getReference().child("users").child(mUser.getUid());
            companyCodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    if(currentUser != null) {
                        SharedPreferences mPrefs = mActivity.getSharedPreferences("com.project3w.properts", Context.MODE_PRIVATE);
                        mPrefs.edit().putString(COMPANY_CODE, currentUser.getCompanyCode()).apply();
                        companyCode = mPrefs.getString(COMPANY_CODE, "");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void createNewUnit(Unit unit, boolean isNew) {
        String unitKey;
        String unitMessage = "Unit: " + unit.getUnitAddress() + " updated successfully!";
        if (!companyCode.isEmpty()) {
            DatabaseReference unitCreateRef = firebaseDatabase.getReference().child(companyCode).child("1").child("units");
            if(isNew) {
                unitKey = unitCreateRef.push().getKey();
                unit.setUnitID(unitKey);
                unitMessage = "Unit: " + unit.getUnitAddress() + " created successfully!";
            } else {
                unitKey = unit.getUnitID();
            }
            unitCreateRef.child(unitKey).setValue(unit);
        }

        // show success message
        Snackbar.make(mActivity.findViewById(android.R.id.content), unitMessage, Snackbar.LENGTH_LONG).show();
    }

    public void deleteSelectedUnit(Unit unit) {
        String deleteUnitKey = unit.getUnitID();
        DatabaseReference unitDeleteRef = firebaseDatabase.getReference().child(companyCode).child("1").child("units").child(deleteUnitKey);
        unitDeleteRef.removeValue();
    }

}
