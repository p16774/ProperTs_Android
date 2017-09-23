package com.project3w.properts.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.project3w.properts.R;

import java.util.ArrayList;

/**
 * Created by Nate on 9/20/17.
 */

public class ManagerHome extends Fragment implements AdapterView.OnItemSelectedListener {

    WebView apartmentView;
    TextView detailMessage;
    Boolean landscapeView;
    Spinner tenantSpinner, unitSpinner;

    public ManagerHome() {
        // empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.manager_home, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        apartmentView = (WebView) getActivity().findViewById(R.id.apartment_view);
        landscapeView = apartmentView != null;
        detailMessage = (TextView) getActivity().findViewById(R.id.detail_test);
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

                        detailMessage.setText(action);

                        return true;

                    } else {
                        // Unknown case - do not override URL loading
                        return false;
                    }
                }
            });

            // setup the lower spinner to select tenant instead of from the picture
            // Data used for prototype
            ArrayList<String> tenantNames = new ArrayList<>();
            ArrayList<String> unitNumbers = new ArrayList<>();

            tenantNames.add("Tony Stark"); // apt1
            tenantNames.add("Natasha Romanoff"); // apt2
            tenantNames.add("Thor Odinson [Archive]"); // apt3 empty
            tenantNames.add("Bruce Banner [Archive]"); // apt4 empty
            tenantNames.add("Peter Parker"); // apt5
            tenantNames.add("Steve Rogers"); // apt6

            unitNumbers.add("Super Unit 1");
            unitNumbers.add("Super Unit 2");
            unitNumbers.add("Super Unit 3");
            unitNumbers.add("Super Unit 4");
            unitNumbers.add("Super Unit 5");
            unitNumbers.add("Super Unit 6");


            getSpinnerData(tenantSpinner, tenantNames);
            getSpinnerData(unitSpinner, unitNumbers);

        }
    }

    private void getSpinnerData(Spinner spinner, ArrayList<String> list) {

        // setup our data selections
        // TODO: Firebase pull of all active tenants to populate spinner

        // assign the listener
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);

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
