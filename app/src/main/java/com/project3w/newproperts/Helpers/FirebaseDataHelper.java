package com.project3w.newproperts.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

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
import com.project3w.newproperts.Objects.Message;
import com.project3w.newproperts.Objects.StaffVerification;
import com.project3w.newproperts.Objects.TenantVerification;
import com.project3w.newproperts.Objects.Company;
import com.project3w.newproperts.Objects.Complaint;
import com.project3w.newproperts.Objects.Request;
import com.project3w.newproperts.Objects.Staff;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.Objects.Unit;
import com.project3w.newproperts.Objects.User;

import java.io.File;
import java.util.HashMap;

import static com.project3w.newproperts.MainActivity.COMPANY_CODE;
import static com.project3w.newproperts.MainActivity.TENANT_ID;

public class FirebaseDataHelper {

    // class variables
    private Activity mActivity;
    private String companyCode, globalTenantID;
    private FirebaseDatabase firebaseDatabase;

    public FirebaseDataHelper(Activity activity) {
        mActivity = activity;
        SharedPreferences mPrefs = mActivity.getSharedPreferences("com.project3w.properts", Context.MODE_PRIVATE);
        companyCode = mPrefs.getString(COMPANY_CODE, "");
        globalTenantID = mPrefs.getString(TENANT_ID, "");
        firebaseDatabase = FirebaseDatabase.getInstance();
    }


    public void saveTenant(Tenant tenant, boolean newAccount ) {

        // get Firebase Database instances
        DatabaseReference tenantDataRef = firebaseDatabase.getReference().child(companyCode).child("1").child("tenants");
        DatabaseReference needAccountRef = firebaseDatabase.getReference("needsAccount");

        // concatenate tenant message
        String tenantMessage = "Tenant: " + tenant.getTenantFirstName() + " " + tenant.getTenantLastName() + " updated successfully!";

        // check for new account and run steps necessary for new tenant creation
        if (newAccount) {
            // assign phone number to the globalTenantID of tenant object
            String tenantID = tenant.getTenantPhone();
            tenant.setTenantID(tenantID);
            tenant.setUserID(""); // added to prevent iOS code from crashing

            // create TenantVerification object
            TenantVerification tenantVerification = new TenantVerification(tenant.getTenantLastName(), tenant.getTenantAddress(), companyCode);

            // create the account verification map
            HashMap<String, Object> needAccount = new HashMap<>();
            needAccount.put(tenantID, tenantVerification);

            // update database with account creation needs
            needAccountRef.updateChildren(needAccount);

            // concatenate tenant message
            tenantMessage = "Tenant: " + tenant.getTenantFirstName() + " " + tenant.getTenantLastName() + " created successfully!";
        }

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
        Snackbar.make(mActivity.findViewById(android.R.id.content), tenantMessage, Snackbar.LENGTH_SHORT).show();
    }

    public boolean submitMaintenanceRequest(Request request) {

        // get firebase database instance and reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String requestType;

        // get status and assign requestType appropriately
        if (request.getRequestUrgency().equals("Critical")) {
            requestType = "critical";
        } else {
            requestType = "new";
        }

        if (currentUser != null) {

            // get our location reference
            DatabaseReference newRequestRef = firebaseDatabase.getReference().child(companyCode).child("1")
                    .child("requests").child(currentUser.getUid());

            // create our key to update
            String requestKey = newRequestRef.push().getKey();

            // create duplicate record for manager/maintenance staff to see
            DatabaseReference duplicateRequestForManagerRef = firebaseDatabase.getReference().child(companyCode).child("1")
                    .child("requests").child(requestType);

            // since this field is optional, we need to check for null first before submitting and uploading an image
            if(!request.getRequestOpenImagePath().isEmpty()) {
                // grab our StorageReference
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://properts-8db06.appspot.com/");
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
                        Snackbar.make(mActivity.findViewById(android.R.id.content), "Image Upload Failed!!", Snackbar.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Snackbar.make(mActivity.findViewById(android.R.id.content), "Image Successfully Uploaded", Snackbar.LENGTH_SHORT).show();
                    }
                });

                // add in our key and empty closed image path and update our image path to Firebase Storage location
                request.setRequestClosedImagePath("");
                request.setRequestOpenImagePath(imageUri.getLastPathSegment());

            }

            // add the requestID and current tenantID to the request for manager data
            request.setRequestID(requestKey);
            request.setRequestUser(globalTenantID);

            // update data
            newRequestRef.child(requestKey).setValue(request);
            duplicateRequestForManagerRef.child(requestKey).setValue(request);
            return true;
        }

        return false;
    }

    public boolean submitComplaint(Complaint complaint) {

        // get firebase database instance and reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            // get our location reference
            DatabaseReference newComplaintRef = firebaseDatabase.getReference().child(companyCode).child("1")
                    .child("complaints").child(currentUser.getUid());

            // create our key to update
            String complaintKey = newComplaintRef.push().getKey();

            // create our duplicate record for our manager
            DatabaseReference duplicateComplaintForManagerRef = firebaseDatabase.getReference().child(companyCode).child("1")
                    .child("complaints").child("active");

            // add in our key and user
            complaint.setComplaintID(complaintKey);
            complaint.setComplaintUser(globalTenantID);

            // update data
            newComplaintRef.child(complaintKey).setValue(complaint);
            duplicateComplaintForManagerRef.child(complaintKey).setValue(complaint);
            return true;
        }

        return false;
    }

    public void createUserReference(String accessType) {
        // get our current userID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            // get our firebase reference
            String userID = mUser.getUid();
            DatabaseReference newUserRef = firebaseDatabase.getReference().child("users").child(userID);

            // create the user and the company/user references
            User createUser = new User("", "1", "", accessType);
            newUserRef.setValue(createUser);
        }
    }

    public void updateUserReference(String companyCode, String tenantID, String accessType) {
        // get our current userID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            // get our firebase reference
            String userID = mUser.getUid();
            DatabaseReference newUserRef = firebaseDatabase.getReference().child("users").child(userID);
            DatabaseReference companyUserRef = firebaseDatabase.getReference().child(companyCode).child("1").child("users").child(userID);

            if (accessType.equals("tenant")) {
                DatabaseReference tenantLinkRef = firebaseDatabase.getReference().child(companyCode).child("1").child("tenants").child(tenantID).child("userID");
                tenantLinkRef.setValue(userID);
            }

            if (accessType.equals("staff")) {
                DatabaseReference staffLinkRef = firebaseDatabase.getReference().child(companyCode).child("1").child("staff").child(tenantID).child("userID");
                staffLinkRef.setValue(userID);
            }

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
        updateUserReference(companyCode, "", "manager");
    }

    public void updateCompany(Company company) {
        // create our company reference and get firebase key
        DatabaseReference updateCompanyRef = firebaseDatabase.getReference().child("companies").child(companyCode);

        // since this field is optional, we need to check for null first before submitting and uploading an image
        if(!company.getCompanyImagePath().isEmpty()) {
            // grab our StorageReference
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://properts-8db06.appspot.com/");
            StorageReference saveLocationRef = storageRef.child("companyImages/" + companyCode);

            // get our Uri File reference
            Uri imageUri = Uri.fromFile(new File(company.getCompanyImagePath()));

            // grab our image reference and Uri for File
            StorageReference imageRef = saveLocationRef.child(imageUri.getLastPathSegment());

            // register UploadTask and putFile
            UploadTask uploadTask = imageRef.putFile(imageUri);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Snackbar.make(mActivity.findViewById(android.R.id.content), "Image Upload Failed!!", Snackbar.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Snackbar.make(mActivity.findViewById(android.R.id.content), "Image Successfully Uploaded", Snackbar.LENGTH_SHORT).show();
                }
            });

            // add in our key and empty closed image path and update our image path to Firebase Storage location
            company.setCompanyImagePath(imageUri.getLastPathSegment());
        }

        // update our company
        updateCompanyRef.setValue(company);
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
                        SharedPreferences.Editor mPrefsEditor = mPrefs.edit();
                        mPrefsEditor.putString(COMPANY_CODE, currentUser.getCompanyCode());
                        mPrefsEditor.putString(TENANT_ID, currentUser.getTenantID());
                        mPrefsEditor.apply();
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

        // strip all punctuation from the address due to a firebase limitation in a key reference
        String strippedAddress = unit.getUnitAddress().replaceAll("[^\\w\\s]","");
        unit.setUnitAddress(strippedAddress);

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
        Snackbar.make(mActivity.findViewById(android.R.id.content), unitMessage, Snackbar.LENGTH_SHORT).show();
    }

    public void deleteSelectedUnit(Unit unit) {
        String deleteUnitKey = unit.getUnitID();
        DatabaseReference unitDeleteRef = firebaseDatabase.getReference().child(companyCode).child("1").child("units").child(deleteUnitKey);
        unitDeleteRef.removeValue();
        Snackbar.make(mActivity.findViewById(android.R.id.content), "Unit " + unit.getUnitAddress() + " deleted successfully!", Snackbar.LENGTH_SHORT).show();
    }

    public void acknowledgeComplaint(Complaint complaint, Tenant tenant) {
        DatabaseReference complaintRef = firebaseDatabase.getReference().child(companyCode).child("1").child("complaints")
                .child(tenant.getUserID()).child(complaint.getComplaintID());
        DatabaseReference managerComplaintRef = firebaseDatabase.getReference().child(companyCode).child("1").child("complaints")
                .child("active").child(complaint.getComplaintID());
        DatabaseReference managerAcknowledgedRef = firebaseDatabase.getReference().child(companyCode).child("1").child("complaints")
                .child("closed").child(complaint.getComplaintID());

        // update, remove, and create our complaint objects
        complaintRef.setValue(complaint);
        managerComplaintRef.removeValue();
        managerAcknowledgedRef.setValue(complaint);
    }

    public void updateRequest(Request request, Tenant tenant, String requestTo, String requestFrom) {
        // set our database references
        DatabaseReference rootRef = firebaseDatabase.getReference().child(companyCode).child("1").child("requests");
        DatabaseReference userRequestRef = rootRef.child(tenant.getUserID()).child(request.getRequestID());

        if(!requestTo.equals(requestFrom)) {
            // remove our old value since we are moving the request to a new category
            DatabaseReference managerRequestRef = rootRef.child(requestFrom).child(request.getRequestID());
            managerRequestRef.removeValue();
        }

        DatabaseReference newManagerRequestRef = rootRef.child(requestTo).child(request.getRequestID());

        // update the request for the manager and the user
        userRequestRef.setValue(request);
        newManagerRequestRef.setValue(request);

        Snackbar.make(mActivity.findViewById(android.R.id.content), "Request Updated Successfully!", Snackbar.LENGTH_SHORT).show();

    }

    public void closeRequest(Request request, Tenant tenant, String requestType) {
        // set our database references
        DatabaseReference rootRef = firebaseDatabase.getReference().child(companyCode).child("1").child("requests");
        DatabaseReference userRequestRef = rootRef.child(tenant.getUserID()).child(request.getRequestID());
        DatabaseReference newManagerRequestRef = rootRef.child(requestType).child(request.getRequestID());
        DatabaseReference closedRequestRef = rootRef.child("closed").child(request.getRequestID());

        // update status
        request.setRequestStatus("Completed");

        // update the request for the manager and the user
        userRequestRef.setValue(request);
        closedRequestRef.setValue(request);
        newManagerRequestRef.removeValue();

        Snackbar.make(mActivity.findViewById(android.R.id.content), "Request Closed Successfully!", Snackbar.LENGTH_SHORT).show();

    }

    public void createStaffMember(Staff staffMember, Boolean isNew) {
        DatabaseReference staffRef = firebaseDatabase.getReference().child(companyCode).child("1").child("staff");

        if(isNew) {
            // create our key
            String staffKey = staffMember.getStaffPhone();
            staffMember.setStaffID(staffKey);
            staffMember.setStaffStatus(true);

            DatabaseReference staffNeedsAccount = firebaseDatabase.getReference().child("needsAccount");
            StaffVerification staffVerification = new StaffVerification(staffMember.getStaffName(), staffKey, companyCode);
            staffNeedsAccount.child(staffKey).setValue(staffVerification);
        }

        staffRef.child(staffMember.getStaffID()).setValue(staffMember);
    }

    public void archiveStaff(Staff staffMember) {
        DatabaseReference staffRef = firebaseDatabase.getReference().child(companyCode).child("1").child("staff");

        // set our status to false
        staffMember.setStaffStatus(false);

        // update our staff value
        staffRef.child(staffMember.getStaffID()).setValue(staffMember);
    }

    public void sendTenantMessage(final Message message) {
        // get our user data
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        // validate for null
        if (mUser != null) {
            String userID = mUser.getUid();
            DatabaseReference userDataRef = firebaseDatabase.getReference().child("users").child(userID);
            userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    if(currentUser != null) {
                        String tenantID = currentUser.getTenantID();
                        DatabaseReference tenantMessageRef = firebaseDatabase.getReference().child(companyCode).child("1")
                                .child("messages").child(tenantID).child("" + message.getMessageDate());
                        tenantMessageRef.setValue(message);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    public void sendManagerMessage(final Message message, String tenantID) {
        // send our message to the tenant
        DatabaseReference tenantMessageRef = firebaseDatabase.getReference().child(companyCode).child("1")
                .child("messages").child(tenantID).child("" + message.getMessageDate());
        tenantMessageRef.setValue(message);
    }

    public void archiveTenant(Tenant tenant) {

        // get Firebase Database instances
        DatabaseReference tenantDataRef = firebaseDatabase.getReference().child(companyCode).child("1").child("tenants");

        // concatenate tenant message
        String tenantMessage = "Tenant: " + tenant.getTenantFirstName() + " " + tenant.getTenantLastName() + " archived successfully!";

        // set tenantStatus to false to make them archived
        tenant.setTenantStatus(false);

        // create HashMap for updating tenants
        HashMap<String, Object> newTenant = new HashMap<>();
        newTenant.put(tenant.getTenantID(), tenant);

        // save the tenant
        tenantDataRef.updateChildren(newTenant);

        // show success message
        Snackbar.make(mActivity.findViewById(android.R.id.content), tenantMessage, Snackbar.LENGTH_SHORT).show();
    }

}
