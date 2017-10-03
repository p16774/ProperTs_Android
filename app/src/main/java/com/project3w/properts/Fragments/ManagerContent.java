package com.project3w.properts.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.project3w.properts.Helpers.FirebaseDataHelper;
import com.project3w.properts.Objects.Tenant;
import com.project3w.properts.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Nate on 10/1/17.
 */

public class ManagerContent extends Fragment {

    // class variables
    public static final String UNIT_NUMBER = "com.project3w.properts.UNIT_NUMBER";
    String chosenUnit;

    public static ManagerContent newInstance(String unit) {

        // create the fragment with the selected apartment unit
        ManagerContent myFragment = new ManagerContent();

        // create bundle and add unit value
        Bundle args = new Bundle();
        args.putString(UNIT_NUMBER, unit);

        // attach bundle
        myFragment.setArguments(args);

        return myFragment;
    }

    // class variables
    Activity mActivity;
    FirebaseDataHelper firebaseDataHelper;
    EditText tenantNameView, tenantEmailView, tenantPhoneView, tenantMoveInDate, tenantDepositView, tenantKeysView, tenantOccupantsView;
    Spinner tenantAddressSpinner;
    String unitNumber;

    public interface DismissFragmentListener {
        void dismissFragment();
    }

    public interface AddNewTenantListener {
        void addTenantInstead(String unit);
    }

    // create listeners
    ManagerContent.DismissFragmentListener onDismissFragmentListener;
    ManagerContent.AddNewTenantListener onAddNewTenantListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // attach the interface listener
        mActivity = getActivity();
        try {
            onDismissFragmentListener = (ManagerContent.DismissFragmentListener) mActivity;
            onAddNewTenantListener = (ManagerContent.AddNewTenantListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement DismissFragmentListener");
        }

        // pull our arguments
        Bundle newBundle = getArguments();
        chosenUnit = newBundle.getString(UNIT_NUMBER);

        // switch our chosenUnit
        if (chosenUnit != null && !chosenUnit.trim().isEmpty()) {
            unitNumber = chosenUnit.substring(chosenUnit.length() - 1);
            chosenUnit = "Unit " + unitNumber;
        } else {
            chosenUnit = "";
        }

        return inflater.inflate(R.layout.add_tenant, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // pull in our firebase helper
        firebaseDataHelper = new FirebaseDataHelper(getActivity());

        // create our view data points
        tenantNameView = (EditText) getActivity().findViewById(R.id.tenant_name);
        tenantAddressSpinner = (Spinner) getActivity().findViewById(R.id.tenant_address_spinner);
        tenantEmailView = (EditText) getActivity().findViewById(R.id.tenant_email);
        tenantPhoneView = (EditText) getActivity().findViewById(R.id.tenant_phone);
        tenantMoveInDate = (EditText) getActivity().findViewById(R.id.tenant_moveindate);
        tenantDepositView = (EditText) getActivity().findViewById(R.id.tenant_deposit);
        tenantKeysView = (EditText) getActivity().findViewById(R.id.tenant_keys);
        tenantOccupantsView = (EditText) getActivity().findViewById(R.id.tenant_occupants);

        // set up our spinner
        ArrayList<String> unitSelection = new ArrayList<>();
        unitSelection.add("Select Unit...");
        unitSelection.add("Unit 1");
        unitSelection.add("Unit 2");
        unitSelection.add("Unit 3");
        unitSelection.add("Unit 4");
        unitSelection.add("Unit 5");
        unitSelection.add("Unit 6");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, unitSelection);
        tenantAddressSpinner.setAdapter(arrayAdapter);

        // call function to pull the current details of the selected tenant
        updateTenantValues();

        // get our button and set our click listener
        Button addTenantBtn = (Button) getActivity().findViewById(R.id.tenant_add_btn);
        addTenantBtn.setText("Update Tenant");
        addTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pull in our entered values and verify we have the needed data
                Tenant newTenant = validateTenant();

                if (newTenant != null) {
                    //firebaseDataHelper.saveTenant(newTenant);
                    onDismissFragmentListener.dismissFragment();
                }
            }
        });

        tenantMoveInDate.setClickable(true);
        tenantMoveInDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

    }

    public Tenant validateTenant() {
        // get our text about the tenant
        String tenantName = tenantNameView.getText().toString().trim();
        String tenantAddress = tenantAddressSpinner.getSelectedItem().toString();
        String tenantEmail = tenantEmailView.getText().toString().trim();
        String tenantPhone = tenantPhoneView.getText().toString().trim();
        String tenantDate = tenantMoveInDate.getText().toString().trim();
        String tenantDeposit = tenantDepositView.getText().toString().trim();
        String tenantKeys = tenantKeysView.getText().toString().trim();
        String tenantOccupants = tenantOccupantsView.getText().toString().trim();

        // validate text entered
        if (tenantName.isEmpty()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must enter a name to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }
        if (tenantAddress.equals("Select Unit...")) {
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
                    "You must enter the number of keys given to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }
        if (tenantOccupants.isEmpty()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must enter the number of occupants to create a tenant", Snackbar.LENGTH_LONG).show();
            return null;
        }

        return new Tenant(tenantName, tenantAddress, tenantEmail, tenantPhone, tenantDate, tenantDeposit,
                tenantKeys, tenantOccupants);

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
        DialogFragment newFragment = new AddTenantFragment.DatePickerFragment();
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

    private void updateTenantValues() {

        if (chosenUnit.isEmpty()) {
            Snackbar.make(mActivity.findViewById(android.R.id.content), "Unknown Error: Please Contact ProperTs", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // dismiss happens automatically
                        }
                    })
                    .show();
            // dismiss the update fragment
            onDismissFragmentListener.dismissFragment();
        } else {
            // pull the values from Firebase and populate our EditText view
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference selectedUnit = firebaseDatabase.getReference().child("currentTenants");
            selectedUnit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // pull our chosenUnit
                    GenericTypeIndicator<HashMap<String,String>> t = new GenericTypeIndicator<HashMap<String, String>>() {};
                    HashMap<String,String> unitMap = dataSnapshot.getValue(t);

                    // pull our tenantID and get our unit data
                    try {
                        // get our tenantID to pull our tenant data
                        String tenantID = unitMap.get(chosenUnit);

                        // verify the unit isn't vacant
                        if (!tenantID.equals("vacant")) {
                            // get our tenant reference
                            DatabaseReference tenantInfo = firebaseDatabase.getReference().child("tenants").child(tenantID);
                            tenantInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // assign our data to a Tenant object
                                    Tenant selectedTenant = dataSnapshot.getValue(Tenant.class);

                                    // try to pull our data out and update our EditText Fields
                                    try {
                                        // get our data from selectedTenant and apply to the EditText fields
                                        tenantNameView.setText(selectedTenant.getTenantName());
                                        tenantEmailView.setText(selectedTenant.getTenantEmail());
                                        tenantPhoneView.setText(selectedTenant.getTenantPhone());
                                        tenantMoveInDate.setText(selectedTenant.getTenantMoveInDate());
                                        tenantDepositView.setText(selectedTenant.getTenantDeposit());
                                        tenantKeysView.setText(selectedTenant.getTenantKeys());
                                        tenantOccupantsView.setText(selectedTenant.getTenantOccupants());
                                        tenantAddressSpinner.setSelection(Integer.parseInt(unitNumber));

                                    } catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            // display the add fragment instead and display a snackbar informing the user
                            Snackbar.make(mActivity.findViewById(android.R.id.content), "Unit Vacant, Add Tenant Action Started.", Snackbar.LENGTH_LONG).show();
                            onAddNewTenantListener.addTenantInstead(chosenUnit);
                        }


                    } catch (NullPointerException e) {
                        Log.d("Null Pointer", "Database Reference Error");
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }
}
