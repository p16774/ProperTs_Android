package com.project3w.properts.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.project3w.properts.Helpers.FirebaseDataHelper;
import com.project3w.properts.Objects.Tenant;
import com.project3w.properts.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Nate on 9/30/17.
 */

public class AddTenantFragment extends Fragment {

    // class variables
    Activity mActivity;
    FirebaseDataHelper firebaseDataHelper;
    EditText tenantNameView, tenantEmailView, tenantPhoneView, tenantMoveInDate, tenantDepositView, tenantKeysView, tenantOccupantsView;
    Spinner tenantAddressSpinner;

    public interface DismissFragmentListener {
        void dismissFragment();
    }

    // create listener
    DismissFragmentListener onDismissFragmentListener;

    public AddTenantFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // set options menu
        setHasOptionsMenu(true);

        // attach the interface listener
        mActivity = getActivity();
        try {
            onDismissFragmentListener = (DismissFragmentListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement DismissFragmentListener");
        }

        return inflater.inflate(R.layout.add_tenant, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.tenant_menu, menu);
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, unitSelection);
        tenantAddressSpinner.setAdapter(arrayAdapter);

        // get our button and set our click listener
        Button addTenantBtn = (Button) getActivity().findViewById(R.id.tenant_add_btn);
        addTenantBtn.setText("Add Tenant");
        addTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pull in our entered values and verify we have the needed data
                Tenant newTenant = validateTenant();

                if (newTenant != null) {
                    firebaseDataHelper.saveTenant(newTenant, true);
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

