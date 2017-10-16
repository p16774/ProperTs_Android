package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.Objects.Unit;
import com.project3w.newproperts.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.project3w.newproperts.MainActivity.COMPANY_CODE;

/**
 * Created by Nate on 9/30/17.
 */

public class TenantsFragment extends Fragment implements View.OnClickListener {

    // class variables
    Activity mActivity;
    FirebaseDataHelper firebaseDataHelper;
    EditText tenantFirstNameView, tenantLastNameView, tenantEmailView, tenantPhoneView, tenantMoveInDate, tenantDepositView, tenantKeysView, tenantOccupantsView;
    Spinner tenantAddressSpinner;
    Boolean isUpdate;
    String companyCode;
    Button addTenantBtn, archiveTenantBtn;
    Tenant newTenant;

    public static final String UPDATE_TENANT = "com.project3w.properts.UPDATE_TENANT";
    public static final String TENANT_INFO = "com.project3w.properts.TENANT_INFO";

    public interface DismissTenantFragmentListener {
        void dismissTenantFragment();
    }

    // create listener
    DismissTenantFragmentListener onDismissFragmentListener;

    public TenantsFragment newInstance(Boolean editMode, Tenant editTenant) {

        TenantsFragment addTenantFragment = new TenantsFragment();
        Bundle args = new Bundle();
        args.putBoolean(UPDATE_TENANT, editMode);
        args.putSerializable(TENANT_INFO, editTenant);
        addTenantFragment.setArguments(args);

        return addTenantFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // inflate our view
        View view = inflater.inflate(R.layout.add_tenant, container, false);

        // attach the interface listener
        mActivity = getActivity();
        try {
            onDismissFragmentListener = (DismissTenantFragmentListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement DismissTenantFragmentListener");
        }

        // pull in our firebase helper
        firebaseDataHelper = new FirebaseDataHelper(getActivity());

        // create our view data points
        tenantFirstNameView = view.findViewById(R.id.tenant_firstname);
        tenantLastNameView = view.findViewById(R.id.tenant_lastname);
        tenantAddressSpinner = view.findViewById(R.id.tenant_address_spinner);
        tenantEmailView = view.findViewById(R.id.tenant_email);
        tenantPhoneView = view.findViewById(R.id.tenant_phone);
        tenantMoveInDate = view.findViewById(R.id.tenant_moveindate);
        tenantDepositView = view.findViewById(R.id.tenant_deposit);
        tenantKeysView = view.findViewById(R.id.tenant_keys);
        tenantOccupantsView = view.findViewById(R.id.tenant_occupants);
        addTenantBtn = view.findViewById(R.id.tenant_add_btn);
        archiveTenantBtn = view.findViewById(R.id.tenant_archive_btn);

        // grab our company code
        SharedPreferences mPrefs = mActivity.getSharedPreferences("com.project3w.properts", Context.MODE_PRIVATE);
        companyCode = mPrefs.getString(COMPANY_CODE, null);

        // check if we are updating a tenant
        isUpdate = getArguments().getBoolean(UPDATE_TENANT);

        if(isUpdate) {
            mActivity.setTitle("Update Tenant");
        } else {
            mActivity.setTitle("Add Tenant");
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        if (isUpdate) {
            newTenant = (Tenant) getArguments().getSerializable(TENANT_INFO);
            if (newTenant != null) {
                // set the text into our views
                tenantFirstNameView.setText(newTenant.getTenantFirstName());
                tenantLastNameView.setText(newTenant.getTenantLastName());
                tenantEmailView.setText(newTenant.getTenantEmail());
                tenantPhoneView.setText(newTenant.getTenantPhone());
                tenantMoveInDate.setText(newTenant.getTenantMoveInDate());
                tenantDepositView.setText(newTenant.getTenantDeposit());
                tenantKeysView.setText(newTenant.getTenantKeys());
                tenantOccupantsView.setText(newTenant.getTenantOccupants());
                addTenantBtn.setText(R.string.update_tenant);

                // update our delete button data
                archiveTenantBtn.setVisibility(View.GONE); //TODO: update this to visible when the function works
                archiveTenantBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
                archiveTenantBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MaterialDialog.Builder(getActivity())
                                .title(newTenant.getTenantFirstName() + " " + newTenant.getTenantLastName())
                                .content("Are you sure you want to archive \nTenant: " + newTenant.getTenantFirstName()
                                        + " " + newTenant.getTenantLastName() + "?")
                                .positiveText("ARCHIVE")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        //firebaseDataHelper.deleteSelectedUnit(newUnit);
                                        //onDismissUnitFragmentListener.dismissUnitFragment();
                                        //TODO: create archive function in firebasedatahelper file
                                        dialog.dismiss();
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

        // pull in our unit data to populate the spinner selection
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference unitsRef = firebaseDatabase.getReference().child(companyCode).child("1").child("units");
        unitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> units = new ArrayList<>();
                units.add("Select Rental Unit...");

                for (DataSnapshot unitDetails : dataSnapshot.getChildren()) {
                    Unit currentUnit = unitDetails.getValue(Unit.class);
                    if(currentUnit != null) {
                        String unitAddress = currentUnit.getUnitAddress();
                        units.add(unitAddress);
                    }
                }

                if (units.size() < 2) {
                    units.clear();
                    units.add("Create Rental Unit First!");
                    Snackbar.make(mActivity.findViewById(android.R.id.content), "You must create a rental unit before you create a tenant", Snackbar.LENGTH_LONG).show();
                    addTenantBtn.setVisibility(View.GONE);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, units);
                tenantAddressSpinner.setAdapter(arrayAdapter);

                // check if we need update the selected spinner with the tenant address to update as needed
                if(isUpdate) {
                    tenantAddressSpinner.setSelection(getIndex(tenantAddressSpinner, newTenant.getTenantAddress()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // get our button and set our click listener
        addTenantBtn.setOnClickListener(this);

        tenantMoveInDate.setClickable(true);
        tenantMoveInDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        //TODO: add an event listener to pull the current tenant data to populate the fields in the tenantAddressSpinner

    }

    @Override
    public void onClick(View v) {
        // validate our tenant object
        newTenant = validateTenant();

        if (newTenant != null) {
            if(isUpdate) {
                firebaseDataHelper.saveTenant(newTenant, false);
            } else {
                firebaseDataHelper.saveTenant(newTenant, true);
            }
            onDismissFragmentListener.dismissTenantFragment();
        }
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    public Tenant validateTenant() {
        String tenantID = "";

        // get our text about the tenant
        String tenantFirstName = tenantFirstNameView.getText().toString().trim();
        String tenantLastName = tenantLastNameView.getText().toString().trim();
        String tenantAddress = tenantAddressSpinner.getSelectedItem().toString();
        String tenantEmail = tenantEmailView.getText().toString().trim();
        String tenantPhone = tenantPhoneView.getText().toString().trim();
        String tenantDate = tenantMoveInDate.getText().toString().trim();
        String tenantDeposit = tenantDepositView.getText().toString().trim();
        String tenantKeys = tenantKeysView.getText().toString().trim();
        String tenantOccupants = tenantOccupantsView.getText().toString().trim();
        if(isUpdate) {
            tenantID = newTenant.getTenantID(); // created for later deletion that happens in the new Tenant object creation
        }

        // validate text entered
        if (tenantFirstName.isEmpty()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must enter the first name to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }
        if (tenantLastName.isEmpty()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must enter the first name to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }
        if (tenantAddress.equals("Select Rental Unit...")) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must select a unit to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }
        if (tenantEmail.isEmpty()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must enter an email address to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }
        if (tenantPhone.isEmpty()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must enter a phone number to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }
        if (tenantDate.isEmpty()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must select a move-in date to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }
        if (tenantDeposit.isEmpty()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must enter the deposit amount to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }
        if (tenantKeys.isEmpty()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must enter the # of keys given to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }
        if (tenantOccupants.isEmpty()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must enter the # of occupants to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }

        newTenant = new Tenant(tenantFirstName, tenantLastName, tenantAddress, tenantEmail, tenantPhone, tenantDate, tenantDeposit,
                tenantKeys, tenantOccupants);

        // replace the tenantID that's removed in the previous step
        if(isUpdate && !tenantID.isEmpty()){
            newTenant.setTenantID(tenantID);
        }

        return newTenant;

    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // translate the month
            String selectedDate = dateSelected(year, month, day);

            // set the text field to display the date
            EditText dateField = (EditText) getActivity().findViewById(R.id.tenant_moveindate);
            dateField.setText(selectedDate);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
    }

    public static String dateSelected(int year, int month, int day) {

        // convert month and return date
        switch (month) {
            case 0:
                return "January " + day + ", " + year;
            case 1:
                return "February " + day + ", " + year;
            case 2:
                return "March " + day + ", " + year;
            case 3:
                return "April " + day + ", " + year;
            case 4:
                return "May " + day + ", " + year;
            case 5:
                return "June " + day + ", " + year;
            case 6:
                return "July " + day + ", " + year;
            case 7:
                return "August " + day + ", " + year;
            case 8:
                return "September " + day + ", " + year;
            case 9:
                return "October " + day + ", " + year;
            case 10:
                return "November " + day + ", " + year;
            case 11:
                return "December " + day + ", " + year;
            default:
                return "";
        }
    }
}

