package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.Helpers.GlideApp;
import com.project3w.newproperts.Objects.Company;
import com.project3w.newproperts.R;

import static android.app.Activity.RESULT_OK;
import static com.project3w.newproperts.MainActivity.COMPANY_CODE;

/**
 * Created by Nate on 10/20/17.
 */

public class CompanyInfo extends Fragment {

    // class variables
    EditText companyNameView, companyAddressView, companyEmailView, companyPhoneView, companyHoursView, companyManagerView;
    ImageView companyImageView;
    Button updateCompanyBtn;
    Company myCompany;
    String mCurrentPhotoPath;
    Activity mActivity;
    FirebaseDataHelper mHelper;
    String companyCode;
    int wtf = 0;

    public static final int RESULT_LOAD_IMG = 0x1001;

    public interface CompanyUpdatedListener {
        void companyUpdated();
    }

    CompanyUpdatedListener onCompanyUpdatedListener;

    public CompanyInfo() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_company, container, false);

        mActivity = getActivity();
        mHelper = new FirebaseDataHelper(mActivity);
        mCurrentPhotoPath = "";

        setHasOptionsMenu(true);

        companyImageView = view.findViewById(R.id.company_image);
        companyImageView.setVisibility(View.GONE);
        companyNameView = view.findViewById(R.id.company_name);
        companyAddressView = view.findViewById(R.id.company_address);
        companyEmailView = view.findViewById(R.id.companay_email);
        companyPhoneView = view.findViewById(R.id.company_phone);
        companyHoursView = view.findViewById(R.id.company_hours);
        companyManagerView = view.findViewById(R.id.company_manager_name);
        updateCompanyBtn = view.findViewById(R.id.company_update_btn);


        SharedPreferences mPrefs = mActivity.getSharedPreferences("com.project3w.properts", Context.MODE_PRIVATE);
        companyCode = mPrefs.getString(COMPANY_CODE, null);

        // attach our listener
        try {
            onCompanyUpdatedListener = (CompanyUpdatedListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement CompanyUpdatedListener");
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.company_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                mActivity.finish();
                break;
            case R.id.action_select_picture:
                pickImageFromGallery();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference companyInfoRef = firebaseDatabase.getReference().child("companies").child(companyCode);
        companyInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myCompany = dataSnapshot.getValue(Company.class);
                if(myCompany != null) {
                    companyNameView.setText(myCompany.getCompanyName());
                    companyAddressView.setText(myCompany.getCompanyAddress());
                    companyEmailView.setText(myCompany.getCompanyEmail());
                    companyPhoneView.setText(myCompany.getCompanyPhone());
                    companyHoursView.setText(myCompany.getCompanyHours());
                    companyManagerView.setText(myCompany.getManagerName());

                    // get our storage reference
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://properts-8db06.appspot.com/");
                    StorageReference imageOpenRef = null;

                    // check for value on open image before pulling file
                    if (!myCompany.getCompanyImagePath().isEmpty() && mCurrentPhotoPath.isEmpty()) {
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

        updateCompanyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create string variables
                String companyName, companyAddress, companyEmail, companyPhone, companyHours, managerName;

                // assign the data entered to variables
                companyName = companyNameView.getText().toString().trim();
                companyAddress = companyAddressView.getText().toString().trim();
                companyEmail = companyEmailView.getText().toString().trim();
                companyPhone = companyPhoneView.getText().toString().trim();
                companyHours = companyHoursView.getText().toString().trim();
                managerName = companyManagerView.getText().toString().trim();

                // update our company object
                myCompany.setCompanyName(companyName);
                myCompany.setCompanyAddress(companyAddress);
                myCompany.setCompanyEmail(companyEmail);
                myCompany.setCompanyPhone(companyPhone);
                myCompany.setCompanyHours(companyHours);
                myCompany.setManagerName(managerName);
                myCompany.setCompanyImagePath(mCurrentPhotoPath);

                // we don't care about empty data fields here - they can even remove the company name
                mHelper.updateCompany(myCompany);

                // dismiss the fragment
                onCompanyUpdatedListener.companyUpdated();

            }
        });
    }

    private void pickImageFromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = mActivity.getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mCurrentPhotoPath = cursor.getString(columnIndex);
                    cursor.close();

                    // Set the Image in ImageView after decoding the String
                    companyImageView.setVisibility(View.VISIBLE);
                    companyImageView.setImageURI(selectedImage);
                }

            } else {
                Snackbar.make(mActivity.findViewById(android.R.id.content), "No Image Selected", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
