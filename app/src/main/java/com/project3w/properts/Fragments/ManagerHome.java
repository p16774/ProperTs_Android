package com.project3w.properts.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.project3w.properts.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Nate on 9/20/17.
 */

public class ManagerHome extends Fragment implements AdapterView.OnItemSelectedListener {

    WebView apartmentView;
    TextView detailMessage;
    Boolean landscapeView;
    Spinner tenantSpinner, unitSpinner;
    Activity mActivity;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public ManagerHome() {
        // empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            mUser = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });

        return inflater.inflate(R.layout.manager_home, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference siteData = firebaseDatabase.getReference();



        apartmentView = (WebView) getActivity().findViewById(R.id.apartment_view);
        landscapeView = apartmentView != null;
        tenantSpinner = (Spinner) getActivity().findViewById(R.id.tenant_spinner);
        unitSpinner = (Spinner) getActivity().findViewById(R.id.unit_spinner);

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

                        return true;

                    } else {
                        // Unknown case - do not override URL loading
                        return false;
                    }
                }
            });

            // setup the lower spinner to select tenant instead of from the picture
            // Data used for prototype
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
        currentTenantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> trial = dataSnapshot.getValue(t);

                // remove any vacants from the list
                try {
                    trial.removeAll(Collections.singleton("vacant"));

                    // if we still have a list display it
                    if (trial.size() > 0) {
                        getSpinnerData(tenantSpinner, trial);
                    }
                } catch (NullPointerException e) {
                    Log.d("NULL EXCEPTION", "No Vacant Units");
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
                Toast.makeText(parent.getContext(), "Tenant Selected: " + item, Toast.LENGTH_LONG).show();
                break;
            case R.id.unit_spinner:
                // On selecting a spinner item
                String unit = parent.getItemAtPosition(position).toString();

                // Showing selected spinner item
                Toast.makeText(parent.getContext(), "Unit Selected: " + unit, Toast.LENGTH_LONG).show();
                break;
            default:
                break;

        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
