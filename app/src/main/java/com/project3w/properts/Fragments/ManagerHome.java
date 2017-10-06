package com.project3w.properts.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.project3w.properts.LoginActivity;
import com.project3w.properts.Objects.Tenant;
import com.project3w.properts.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Nate on 9/20/17.
 */

public class ManagerHome extends Fragment implements AdapterView.OnItemSelectedListener {

    WebView apartmentView;
    Boolean landscapeView;
    Spinner tenantSpinner, unitSpinner;
    Activity mActivity;
    private FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public ManagerHome() {
        // empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // send user to the login screen if they aren't logged in
        if (mUser == null) {
            Intent loginScreen = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginScreen);
            getActivity().finish();
        }

        return inflater.inflate(R.layout.manager_home, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // assign the firebase data to the instance for later data calls
        firebaseDatabase = FirebaseDatabase.getInstance();

        // assign our values for the view
        apartmentView = (WebView) getActivity().findViewById(R.id.apartment_view);
        landscapeView = apartmentView != null;
        tenantSpinner = (Spinner) getActivity().findViewById(R.id.tenant_spinner);
        unitSpinner = (Spinner) getActivity().findViewById(R.id.unit_spinner);

        // if landscape view is true display our full layout
        if (landscapeView) {
            apartmentView.getSettings().setBuiltInZoomControls(true);
            apartmentView.getSettings().setSupportZoom(true);
            apartmentView.getSettings().setUseWideViewPort(true);
            apartmentView.getSettings().setLoadWithOverviewMode(true);
            apartmentView.loadUrl("http://fullsail.project3w.com");

            apartmentView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // Parse the URL to determine if it's a special Plaid Link redirect or a request
                    // for a standard URL (typically a forgotten password or account not setup link).
                    // Handle Plaid Link redirects and open traditional pages directly in the  user's
                    // preferred browser.
                    Uri parsedUri = Uri.parse(url);
                    if (parsedUri.getScheme().equals("properts")) {
                        String action = parsedUri.getHost();

                        // add our fragment with our selected action
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        ManagerContent mc = ManagerContent.newInstance(action);
                        fragmentTransaction.replace(R.id.manager_content, mc);
                        fragmentTransaction.commit();

                        return true;

                    } else {
                        // Unknown case - do not override URL loading
                        return false;
                    }
                }
            });

            // setup the lower spinner to select tenant instead of from the picture
            ArrayList<String> unitNumbers = new ArrayList<>();
            unitNumbers.add("Unit 1");
            unitNumbers.add("Unit 2");
            unitNumbers.add("Unit 3");
            unitNumbers.add("Unit 4");
            unitNumbers.add("Unit 5");
            unitNumbers.add("Unit 6");

            getCurrentTenants();
            getSpinnerData(unitSpinner, unitNumbers);

        }
    }

    private void getCurrentTenants() {

        // get our database reference
        DatabaseReference currentTenantRef = firebaseDatabase.getReference("currentTenants");
        currentTenantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String,String>> t = new GenericTypeIndicator<HashMap<String, String>>() {};
                HashMap<String,String> tenantsMap = dataSnapshot.getValue(t);

                // create new arraylist
                final ArrayList<String> tenantList = new ArrayList<>();

                // remove any vacants from the list
                try {
                    // convert hashmap to arraylist to iterate over
                    tenantList.addAll(tenantsMap.values());
                    tenantList.removeAll(Collections.singleton("vacant"));

                    // if we still have a list display it
                    if (tenantList.size() > 0) {
                        final ArrayList<String> tenantNames = new ArrayList<>();
                        for (String tenantID : tenantList) {
                            DatabaseReference tenantData = firebaseDatabase.getReference("tenants").child(tenantID);
                            tenantData.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Tenant tenant = dataSnapshot.getValue(Tenant.class);
                                    tenantNames.add(tenant.getTenantName());
                                    getSpinnerData(tenantSpinner, tenantNames);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                } catch (NullPointerException e) {
                    Log.d("NULL EXCEPTION", "Database Reference Error in HashMap");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getSpinnerData(Spinner spinner, ArrayList<String> list) {

        // setup our data selections
        // TODO: Firebase pull of all active tenants to populate spinner

        // assign the listener
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, list);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // get parent id for the spinner
        int spinnerId = parent.getId();

        // switch data based on the id of the spinner
        switch (spinnerId) {

            case R.id.tenant_spinner:
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();

                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Tenant Selected: " + item, Toast.LENGTH_LONG).show();
                break;
            case R.id.unit_spinner:
                // On selecting a spinner item
                String unit = parent.getItemAtPosition(position).toString();

                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Unit Selected: " + unit, Toast.LENGTH_LONG).show();
                break;
            default:
                break;

        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
