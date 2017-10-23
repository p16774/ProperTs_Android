package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.Objects.Staff;
import com.project3w.newproperts.R;

/**
 * Created by Nate on 10/19/17.
 */

public class StaffFragment extends Fragment {

    // class variables
    // class variables
    EditText staffFullNameView, staffEmailAddressView, staffPhoneNumberView;
    Button staffAddBtn, staffArchiveBtn;
    Activity mActivity;
    Staff staffMember;
    Boolean isCurrent = false;
    FirebaseDataHelper mHelper;

    public static final String STAFF_INFO = "com.project3w.properts.STAFF_INFO";
    public static final String STAFF_STATUS = "com.project3w.properts.STAFF_STATUS";

    public interface StaffFunctionListener {
        void dismissStaffFragment();
    }

    StaffFunctionListener onStaffFunctionListener;

    public StaffFragment() {
    }

    public StaffFragment newInstance(Staff staff, Boolean isCurrent) {

        StaffFragment myFragment = new StaffFragment();
        Bundle args = new Bundle();
        args.putSerializable(STAFF_INFO, staff);
        args.putBoolean(STAFF_STATUS, isCurrent);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_staff, container, false);

        mActivity = getActivity();
        staffMember = (Staff) getArguments().getSerializable(STAFF_INFO);
        isCurrent = getArguments().getBoolean(STAFF_STATUS, true);
        mHelper = new FirebaseDataHelper(mActivity);

        staffFullNameView = view.findViewById(R.id.staff_fullname);
        staffEmailAddressView = view.findViewById(R.id.staff_email);
        staffPhoneNumberView = view.findViewById(R.id.staff_phone);
        staffAddBtn = view.findViewById(R.id.staff_add_btn);
        staffArchiveBtn = view.findViewById(R.id.staff_archive_btn);

        // attach our listener
        try {
            onStaffFunctionListener = (StaffFunctionListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement StaffFunctionListener");
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // validate if we are updating a current staff member
        if (isCurrent && staffMember.getStaffName() != null) {
            staffFullNameView.setText(staffMember.getStaffName());
            staffPhoneNumberView.setText(staffMember.getStaffPhone());
            staffEmailAddressView.setText(staffMember.getStaffEmail());
            staffArchiveBtn.setVisibility(View.VISIBLE);
            staffAddBtn.setText("Update Staff Member");
        } else if (!isCurrent && staffMember.getStaffName() != null) {
            staffFullNameView.setText(staffMember.getStaffName());
            staffPhoneNumberView.setText(staffMember.getStaffPhone());
            staffEmailAddressView.setText(staffMember.getStaffEmail());
            staffAddBtn.setVisibility(View.GONE);
        }




        // add our onclicklisteners
        staffAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get our data entered
                String staffName, staffPhone, staffEmail;
                staffName = staffFullNameView.getText().toString().trim();
                staffPhone = staffPhoneNumberView.getText().toString().trim();
                staffEmail = staffEmailAddressView.getText().toString().trim();

                // update our new staffMember that was created or need to update
                staffMember.setStaffName(staffName);
                staffMember.setStaffPhone(staffPhone);
                staffMember.setStaffEmail(staffEmail);

                // validate data fields
                if (staffName.isEmpty() || staffPhone.isEmpty() || staffEmail.isEmpty()) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "You must enter data into ALL fields for new Staff", Snackbar.LENGTH_SHORT).show();
                } else {
                    if(staffMember.getStaffID() != null) {
                        mHelper.createStaffMember(staffMember, false);
                    } else {
                        mHelper.createStaffMember(staffMember, true);
                    }
                    onStaffFunctionListener.dismissStaffFragment();
                }
            }
        });

        staffArchiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title(staffMember.getStaffName())
                        .content("Are you sure you want to archive \nStaff: " + staffMember.getStaffName() + "?")
                        .positiveText("ARCHIVE")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mHelper.archiveStaff(staffMember);
                                onStaffFunctionListener.dismissStaffFragment();
                            }
                        })
                        .positiveColorRes(R.color.colorBlack)
                        .negativeText("CANCEL")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .cancelable(true)
                        .build()
                        .show();
            }
        });
    }
}
