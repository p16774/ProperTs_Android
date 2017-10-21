package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.Objects.Company;
import com.project3w.newproperts.R;

import static com.project3w.newproperts.MainActivity.COMPANY_CODE;

/**
 * Created by Nate on 10/20/17.
 */

public class CompanyInfo extends Fragment {

    // class variables
    EditText companyNameView, companyAddressView, companyEmailView, companyPhoneView, companyHoursView, companyManagerView;
    Button updateCompanyBtn;
    Company myCompany;
    Activity mActivity;
    FirebaseDataHelper mHelper;
    String companyCode;

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

        setHasOptionsMenu(true);

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

                // we don't care about empty data fields here - they can even remove the company name
                mHelper.updateCompany(myCompany);

                // dismiss the fragment
                onCompanyUpdatedListener.companyUpdated();

            }
        });








    }
}
