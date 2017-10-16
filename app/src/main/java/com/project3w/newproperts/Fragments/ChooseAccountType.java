package com.project3w.newproperts.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.project3w.newproperts.R;

/**
 * Created by Nate on 10/12/17.
 */

public class ChooseAccountType extends Fragment implements View.OnClickListener {

    // class variables
    Button managerBtn, tenantBtn, staffBtn;
    Activity mActivity;

    public ChooseAccountType() {
    }

    public interface CreateAccountListener {
        void createNewAccount(String accountType);
    }

    CreateAccountListener onCreateAccountListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_type, container, false);

        mActivity = getActivity();

        // pull in our button references to send user to correct fragment
        tenantBtn = view.findViewById(R.id.create_tenant_btn);
        managerBtn = view.findViewById(R.id.create_manager_btn);
        staffBtn = view.findViewById(R.id.create_staff_btn);

        try {
            onCreateAccountListener = (CreateAccountListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must fully implement CreateAccountListener");
        }

        mActivity.setTitle("Choose Account Type");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // attach our onclick listeners
        tenantBtn.setOnClickListener(this);
        managerBtn.setOnClickListener(this);
        staffBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int btnID = v.getId();
        System.out.println("BUTTON ID IS: " + btnID);
        switch (btnID) {
            case R.id.create_tenant_btn:
                onCreateAccountListener.createNewAccount("tenant");
                break;
            case R.id.create_manager_btn:
                onCreateAccountListener.createNewAccount("manager");
                break;
            case R.id.create_staff_btn:
                onCreateAccountListener.createNewAccount("staff");
                break;
        }
    }
}
