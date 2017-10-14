package com.project3w.newproperts.Fragments;

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

import com.google.firebase.database.FirebaseDatabase;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.MainActivity;
import com.project3w.newproperts.R;

/**
 * Created by Nate on 10/12/17.
 */

public class NewCompanyCreation extends Fragment implements View.OnClickListener {

    // class variables
    EditText companyNameView;
    Button companyCreateBtn;
    FirebaseDataHelper firebaseDataHelper;

    public NewCompanyCreation() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.company_creation, container, false);

        // assign our views
        companyNameView = view.findViewById(R.id.creation_company_name);
        companyCreateBtn = view.findViewById(R.id.creation_create_btn);

        firebaseDataHelper = new FirebaseDataHelper(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // assign the onclick listener
        companyCreateBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // verify text entry before creation
        if (companyNameView.getText().toString().trim().equals("")) {
            // display snackbar
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Company Name is Required", Snackbar.LENGTH_LONG).show();
        } else {
            String companyName = companyNameView.getText().toString();
            firebaseDataHelper.createCompany(companyName);

            // send the new manager to their property
            Intent managerHome = new Intent(getActivity().getApplicationContext(), MainActivity.class);
            startActivity(managerHome);
            getActivity().finish();
        }
    }
}