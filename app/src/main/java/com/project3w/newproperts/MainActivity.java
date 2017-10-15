package com.project3w.newproperts;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project3w.newproperts.Fragments.ManagerFragments.ManagerTenants;
import com.project3w.newproperts.Fragments.ManagerFragments.UnitsFragment;
import com.project3w.newproperts.Fragments.ManagerFragments.ManagerUnits;
import com.project3w.newproperts.Fragments.TenantFragments.AddComplaintFragment;
import com.project3w.newproperts.Fragments.TenantFragments.AddRequestFragment;
import com.project3w.newproperts.Fragments.ManagerFragments.TenantsFragment;
import com.project3w.newproperts.Fragments.ManagerFragments.ManagerHome;
import com.project3w.newproperts.Fragments.TenantFragments.TenantComplaints;
import com.project3w.newproperts.Fragments.TenantFragments.TenantHome;
import com.project3w.newproperts.Fragments.TenantFragments.TenantMaintenance;
import com.project3w.newproperts.Fragments.TenantFragments.ViewRequestFragment;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.Objects.Request;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.Objects.Unit;

public class MainActivity extends AppCompatActivity implements TenantsFragment.DismissTenantFragmentListener,
        TenantMaintenance.AddNewRequestListener,
        TenantMaintenance.DisplayRequestListener,
        AddRequestFragment.DismissFragmentListener,
        ViewRequestFragment.DismissFragmentListener,
        TenantComplaints.AddNewComplaintListener,
        AddComplaintFragment.DismissFragmentListener,
        ManagerHome.MenuOptionSelectedListener,
        ManagerUnits.AddNewUnitListener,
        ManagerUnits.EditUnitListener,
        UnitsFragment.DismissUnitFragmentListener,
        ManagerTenants.EditTenantFragmentListener {

    // class variables
    FirebaseUser mUser;
    AHBottomNavigation bottomNavigation;
    Boolean tenantMenu = false;
    String userType = "";
    FirebaseDataHelper mHelper;

    public static final String COMPANY_CODE = "com.project3w.properts.COMPANY_CODE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check bools.xml and set the proper screen orientation for device widths
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        mHelper = new FirebaseDataHelper(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            Intent loginScreen = new Intent(this, LoginActivity.class);
            startActivity(loginScreen);
            finish();
        } else {

            // set our sharedpreference of the company code
            mHelper.setSharedCompanyCode();

            if (userType.equals("")) {

                // pull in the user data and send the appropriate home page flow
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userDataRef = firebaseDatabase.getReference("users").child(mUser.getUid()).child("accessRole");
                userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            // set our userType
                            userType = dataSnapshot.getValue().toString();

                            // call in our bottom navigation library
                            bottomNavigation = findViewById(R.id.bottom_navigation);
                            bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
                            bottomNavigation.setAccentColor(Color.parseColor("#0D47A1"));
                            bottomNavigation.setInactiveColor(Color.parseColor("#888888"));

                            // call home fragment on initial start up
                            callHome();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    protected void callHome() {
        clearBackStack();

        // start our Fragment Manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // add our navigation items here
        // Create items
        final AHBottomNavigationItem itemHome = new AHBottomNavigationItem(getString(R.string.navigation_home), R.drawable.home, R.color.colorAccent);
        final AHBottomNavigationItem itemTenants = new AHBottomNavigationItem(getString(R.string.navigation_tenants), R.drawable.tenants, R.color.colorBlack);
        final AHBottomNavigationItem itemMessaging = new AHBottomNavigationItem(getString(R.string.navigation_messaging), R.drawable.message, R.color.colorGrey);
        final AHBottomNavigationItem itemMaintenance = new AHBottomNavigationItem(getString(R.string.navigation_maintenance), R.drawable.maintenance, R.color.colorGrey);
        final AHBottomNavigationItem itemComplaint = new AHBottomNavigationItem(getString(R.string.navigation_complaints), R.drawable.complaint, R.color.colorGrey);

        switch (userType) {
            case "manager":

                if(bottomNavigation.getItemsCount() == 0) {
                    // add our navigations
                    bottomNavigation.addItem(itemHome);
                    bottomNavigation.addItem(itemTenants);
                    bottomNavigation.addItem(itemMessaging);
                    bottomNavigation.addItem(itemMaintenance);
                    bottomNavigation.addItem(itemComplaint);

                    // setup our navigation in each of the tabs
                    bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
                        @Override
                        public boolean onTabSelected(int position, boolean wasSelected) {

                            switch (position) {
                                case 0:
                                    callHome();
                                    break;
                                case 1:
                                    callTenants();
                                    break;
                                case 2:
                                    // TODO: add messaging functions
                                    break;
                                case 3:
                                    callMaintenance();
                                    break;
                                case 4:
                                    callComplaints();
                                    break;

                            }

                            return true;
                        }
                    });
                }

                // validate menu
                tenantMenu = false;

                // add the ManagerHome fragment
                ManagerHome mf = new ManagerHome();
                fragmentTransaction.replace(R.id.main_view_container, mf);
                fragmentTransaction.commit();

                break;
            case "tenant":

                if(bottomNavigation.getItemsCount() == 0) {
                    // add our navigations
                    bottomNavigation.addItem(itemHome);
                    bottomNavigation.addItem(itemMessaging);
                    bottomNavigation.addItem(itemMaintenance);
                    bottomNavigation.addItem(itemComplaint);

                    // setup our navigation in each of the tabs
                    bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
                        @Override
                        public boolean onTabSelected(int position, boolean wasSelected) {

                            switch (position) {
                                case 0:
                                    callHome();
                                    break;
                                case 1:
                                    // TODO: add messaging functions
                                    break;
                                case 2:
                                    callMaintenance();
                                    break;
                                case 3:
                                    callComplaints();
                                    break;
                            }

                            return true;
                        }
                    });
                }

                // clear our admin menu that was created and load our tenant menu instead
                tenantMenu = true;

                // add the TenantHome fragment
                TenantHome th = new TenantHome();
                fragmentTransaction.replace(R.id.main_view_container, th);
                fragmentTransaction.commit();

                break;
            case "maintenance":

                // add the MaintenanceHome Fragment
                //TODO: create the maintenance home fragment
                break;

            //TODO: create default display showing error - no access assigned and prevent usage
        }
    }

    protected void callTenants() {
        clearBackStack();
        // start our Fragment Manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ManagerTenants mt = new ManagerTenants();
        fragmentTransaction.replace(R.id.main_view_container, mt);
        fragmentTransaction.commit();
    }

    protected void callMaintenance() {
        clearBackStack();
         // start our Fragment Manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (userType) {
            case "manager":

                // validate menu
                tenantMenu = false;

                // add the ManagerHome fragment
                ManagerHome mf = new ManagerHome();
                fragmentTransaction.replace(R.id.main_view_container, mf);
                fragmentTransaction.commit();

                break;
            case "tenant":

                // validate menu
                tenantMenu = true;

                // add the TenantHome fragment
                TenantMaintenance tm = new TenantMaintenance();
                fragmentTransaction.replace(R.id.main_view_container, tm);
                fragmentTransaction.commit();

                break;
            case "maintenance":

                // add the MaintenanceHome Fragment
                //TODO: create the maintenance home fragment
                break;

            //TODO: create default display showing error - no access assigned and prevent usage
        }
    }

    protected void callComplaints() {
        clearBackStack();
        // start our Fragment Manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (userType) {
            case "manager":

                // add the ManagerHome fragment
                ManagerHome mf = new ManagerHome();
                fragmentTransaction.replace(R.id.main_view_container, mf);
                fragmentTransaction.commit();

                break;
            case "tenant":

                // add the TenantHome fragment
                TenantComplaints tc = new TenantComplaints();
                fragmentTransaction.replace(R.id.main_view_container, tc);
                fragmentTransaction.commit();

                break;
            case "maintenance":

                // add the MaintenanceHome Fragment
                //TODO: create the maintenance home fragment
                break;

            //TODO: create default display showing error - no access assigned and prevent usage
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add_tenant:
                // create intent to send the user to the Add Tenant Fragment
                break;
            case R.id.action_change_settings:
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addNewRequest() {
        // create intent to send the user to the Add Request Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddRequestFragment ar = new AddRequestFragment();
        fragmentTransaction.replace(R.id.main_view_container, ar);
        fragmentTransaction.addToBackStack("addrequest");
        fragmentTransaction.commit();
    }

    @Override
    public void displayRequest(Request request) {
        // create intent to send the user to the View Request Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ViewRequestFragment ar = new ViewRequestFragment().newInstance(request);
        fragmentTransaction.replace(R.id.main_view_container, ar);
        fragmentTransaction.addToBackStack("displayrequest");
        fragmentTransaction.commit();
    }

    @Override
    public void dismissRequestFragment() {
        callMaintenance();
    }

    @Override
    public void addNewComplaint() {
        // create intent to send the user to the Add Request Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddComplaintFragment ac = new AddComplaintFragment();
        fragmentTransaction.replace(R.id.main_view_container, ac);
        fragmentTransaction.addToBackStack("addcomplaint");
        fragmentTransaction.commit();
    }

    //@Override
    public void dismissComplaintFragment() {
        callComplaints();
    }

    @Override
    public void openMenuOption(String menuOption) {
        switch (menuOption) {

            case "units":
                // create intent to send the user to the Add Request Fragment
                FragmentManager unitManager = getSupportFragmentManager();
                FragmentTransaction unitTransaction = unitManager.beginTransaction();
                ManagerUnits mu = new ManagerUnits();
                unitTransaction.replace(R.id.main_view_container, mu);
                unitTransaction.addToBackStack("manageunits");
                unitTransaction.commit();
                break;
            case "tenants":
                // switch to the main add fragment task - bypass the tenant view
                FragmentManager tenantManager = getSupportFragmentManager();
                FragmentTransaction tenantTransaction = tenantManager.beginTransaction();
                TenantsFragment at = new TenantsFragment().newInstance(false, new Tenant());
                tenantTransaction.replace(R.id.main_view_container, at);
                tenantTransaction.addToBackStack("addtenant");
                tenantTransaction.commit();
                break;
        }
    }

    @Override
    public void addNewUnit() {
        // create intent to send the user to the Add Request Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        UnitsFragment au = new UnitsFragment().newInstance(false, new Unit());
        fragmentTransaction.replace(R.id.main_view_container, au);
        fragmentTransaction.addToBackStack("newunit");
        fragmentTransaction.commit();
    }

    @Override
    public void editUnit(Unit unit) {
        // create intent to send the user to the Edit Unit Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        UnitsFragment au = new UnitsFragment().newInstance(true, unit);
        fragmentTransaction.replace(R.id.main_view_container, au);
        fragmentTransaction.addToBackStack("editunit");
        fragmentTransaction.commit();
    }

    @Override
    public void dismissUnitFragment() {
        openMenuOption("units");
    }

    @Override
    public void editTenant(Tenant tenant) {
        // switch to the main add fragment task - bypass the tenant view
        FragmentManager tenantManager = getSupportFragmentManager();
        FragmentTransaction tenantTransaction = tenantManager.beginTransaction();
        TenantsFragment at = new TenantsFragment().newInstance(true, tenant);
        tenantTransaction.replace(R.id.main_view_container, at);
        tenantTransaction.addToBackStack("addtenant");
        tenantTransaction.commit();
    }

    @Override
    public void dismissTenantFragment() {
        callTenants();
    }
}
