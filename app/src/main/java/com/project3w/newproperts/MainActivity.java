package com.project3w.newproperts;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import com.project3w.newproperts.Fragments.ManagerFragments.CompanyInfo;
import com.project3w.newproperts.Fragments.ManagerFragments.ComplaintFragment;
import com.project3w.newproperts.Fragments.ManagerFragments.ComplaintsView;
import com.project3w.newproperts.Fragments.ManagerFragments.ManagerComplaints;
import com.project3w.newproperts.Fragments.ManagerFragments.ManagerHome;
import com.project3w.newproperts.Fragments.ManagerFragments.ManagerMessages;
import com.project3w.newproperts.Fragments.ManagerFragments.ManagerRequests;
import com.project3w.newproperts.Fragments.ManagerFragments.ManagerStaff;
import com.project3w.newproperts.Fragments.ManagerFragments.MessagesView;
import com.project3w.newproperts.Fragments.ManagerFragments.RequestFragment;
import com.project3w.newproperts.Fragments.ManagerFragments.RequestsView;
import com.project3w.newproperts.Fragments.ManagerFragments.ManagerTenants;
import com.project3w.newproperts.Fragments.ManagerFragments.ManagerUnits;
import com.project3w.newproperts.Fragments.ManagerFragments.StaffFragment;
import com.project3w.newproperts.Fragments.ManagerFragments.StaffView;
import com.project3w.newproperts.Fragments.ManagerFragments.TenantsFragment;
import com.project3w.newproperts.Fragments.ManagerFragments.UnitsFragment;
import com.project3w.newproperts.Fragments.StaffFragments.StaffHome;
import com.project3w.newproperts.Fragments.TenantFragments.AddComplaintFragment;
import com.project3w.newproperts.Fragments.TenantFragments.AddRequestFragment;
import com.project3w.newproperts.Fragments.TenantFragments.TenantComplaints;
import com.project3w.newproperts.Fragments.TenantFragments.TenantHome;
import com.project3w.newproperts.Fragments.TenantFragments.TenantMaintenance;
import com.project3w.newproperts.Fragments.TenantFragments.TenantMessages;
import com.project3w.newproperts.Fragments.TenantFragments.ViewRequestFragment;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.Objects.Complaint;
import com.project3w.newproperts.Objects.Request;
import com.project3w.newproperts.Objects.Staff;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.Objects.Unit;

import static com.project3w.newproperts.Fragments.TenantFragments.VerifyTenantFragment.ACCESS_TYPE;

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
        ManagerTenants.EditTenantFragmentListener,
        ComplaintsView.ComplaintAcknowledgementListener,
        ComplaintFragment.ComplaintAcknowledgedListener,
        ManagerComplaints.ComplaintTypeListener,
        ManagerRequests.RequestTypeListener,
        RequestsView.DisplayRequestListener,
        RequestFragment.RequestUpdateListener,
        ManagerStaff.ManageStaffListener,
        StaffView.DisplayStaffListener,
        StaffFragment.StaffFunctionListener,
        CompanyInfo.CompanyUpdatedListener,
        ManagerMessages.DisplayTenantMesssages {

    // class variables
    FirebaseUser mUser;
    AHBottomNavigation bottomNavigation;
    Boolean tenantMenu = false;
    String userType = "";
    FirebaseDataHelper mHelper;

    public static final String COMPANY_CODE = "com.project3w.properts.COMPANY_CODE";
    public static final String TENANT_ID = "com.project3w.properts.TENANT_ID";
    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 0x2001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check bools.xml and set the proper screen orientation for device widths
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mHelper = new FirebaseDataHelper(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            Intent loginScreen = new Intent(this, LoginActivity.class);
            startActivity(loginScreen);
            finish();
        } else {

            if (Build.VERSION.SDK_INT >= 23) {
                // request permissions
                requestPermissions(new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }

            // set our sharedpreference of the company code every time we come here just in case something deletes it
            mHelper.setSharedCompanyCode();
            mHelper.setSharedTenantID();

            // grab our intent that should have our access type
            if (getIntent().hasExtra(ACCESS_TYPE)) {
                userType = getIntent().getExtras().getString(ACCESS_TYPE, "");
            }

            // grab the access type from the database if sent back here in a weird way to not have the intent running
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
            } else {
                // call in our bottom navigation library
                bottomNavigation = findViewById(R.id.bottom_navigation);
                bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
                bottomNavigation.setAccentColor(Color.parseColor("#0D47A1"));
                bottomNavigation.setInactiveColor(Color.parseColor("#888888"));

                // call home fragment on initial start up
                callHome();
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
                                    callMessages();
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
                                    callMessages();
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
            case "staff":
                if(bottomNavigation.getItemsCount() == 0) {
                    // add our navigations
                    bottomNavigation.addItem(itemHome);
                    bottomNavigation.addItem(itemMaintenance);

                    // setup our navigation in each of the tabs
                    bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
                        @Override
                        public boolean onTabSelected(int position, boolean wasSelected) {

                            switch (position) {
                                case 0:
                                    callHome();
                                    break;
                                case 1:
                                    callMaintenance();
                                    break;
                            }

                            return true;
                        }
                    });
                }

                // validate menu
                tenantMenu = false;

                // add the ManagerHome fragment
                StaffHome sh = new StaffHome();
                fragmentTransaction.replace(R.id.main_view_container, sh);
                fragmentTransaction.commit();
                break;

            //TODO: create default display showing error - no access assigned and prevent usage
        }
    }

    protected void callMessages() {
        switch (userType) {
            case "tenant":
                // start our Fragment Manager
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                TenantMessages tm = new TenantMessages();
                fragmentTransaction.replace(R.id.main_view_container, tm);
                fragmentTransaction.commit();
                break;
            case "manager":
                // start our Fragment Manager
                FragmentManager messagesManager = getSupportFragmentManager();
                FragmentTransaction messagesTransaction = messagesManager.beginTransaction();
                ManagerMessages mm = new ManagerMessages();
                messagesTransaction.replace(R.id.main_view_container, mm);
                messagesTransaction.commit();
                break;
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
                ManagerRequests mm = new ManagerRequests();
                fragmentTransaction.replace(R.id.main_view_container, mm);
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
            case "staff":

                // validate menu
                tenantMenu = false;

                // reuse the manager maintenance task
                ManagerRequests sm = new ManagerRequests();
                fragmentTransaction.replace(R.id.main_view_container, sm);
                fragmentTransaction.commit();
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
                ManagerComplaints mc = new ManagerComplaints();
                fragmentTransaction.replace(R.id.main_view_container, mc);
                fragmentTransaction.commit();

                break;
            case "tenant":

                // add the TenantHome fragment
                TenantComplaints tc = new TenantComplaints();
                fragmentTransaction.replace(R.id.main_view_container, tc);
                fragmentTransaction.commit();

                break;
            case "staff":
                //TODO: for later versions to implement staff functionality
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
                // go to the manage units screen
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
            case "staff":
                // go to the manage staff screen
                FragmentManager staffManager = getSupportFragmentManager();
                FragmentTransaction staffTransaction = staffManager.beginTransaction();
                ManagerStaff ms = new ManagerStaff();
                staffTransaction.replace(R.id.main_view_container, ms);
                staffTransaction.addToBackStack("managestaff");
                staffTransaction.commit();
                break;
            case "company":
                // update our company data
                FragmentManager companyManager = getSupportFragmentManager();
                FragmentTransaction companyTransaction = companyManager.beginTransaction();
                CompanyInfo ci = new CompanyInfo();
                companyTransaction.replace(R.id.main_view_container, ci);
                companyTransaction.addToBackStack("companydata");
                companyTransaction.commit();
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

    @Override
    public void displayComplaint(Complaint complaint, Tenant tenant, Boolean isClosed) {
        // switch to the main add fragment task - bypass the tenant view
        FragmentManager complaintManager = getSupportFragmentManager();
        FragmentTransaction tenantTransaction = complaintManager.beginTransaction();
        ComplaintFragment at = new ComplaintFragment().newInstance(complaint, tenant, isClosed);
        tenantTransaction.replace(R.id.main_view_container, at);
        tenantTransaction.addToBackStack("managercomplaints");
        tenantTransaction.commit();
    }

    @Override
    public void complaintAcknowledged() {
        callComplaints();
    }

    @Override
    public void callComplaintFragment(String complaintType) {
        // switch to the main add fragment task - bypass the tenant view
        FragmentManager complaintManager = getSupportFragmentManager();
        FragmentTransaction tenantTransaction = complaintManager.beginTransaction();
        ComplaintsView cv = new ComplaintsView().newInstance(complaintType);
        tenantTransaction.replace(R.id.main_view_container, cv);
        tenantTransaction.addToBackStack("complainttype");
        tenantTransaction.commit();
    }

    @Override
    public void callRequestFragment(String requestType) {
        // switch to the main add fragment task - bypass the tenant view
        FragmentManager requestManager = getSupportFragmentManager();
        FragmentTransaction tenantTransaction = requestManager.beginTransaction();
        RequestsView rv = new RequestsView().newInstance(requestType);
        tenantTransaction.replace(R.id.main_view_container, rv);
        tenantTransaction.addToBackStack("requesttype");
        tenantTransaction.commit();
    }

    @Override
    public void displayRequest(Request request, Tenant tenant, String requestType, Boolean isClosed) {
        // switch to the main add fragment task - bypass the tenant view
        FragmentManager requestManager = getSupportFragmentManager();
        FragmentTransaction tenantTransaction = requestManager.beginTransaction();
        RequestFragment rf = new RequestFragment().newInstance(request, tenant, requestType, isClosed);
        tenantTransaction.replace(R.id.main_view_container, rf);
        tenantTransaction.addToBackStack("managerrequest");
        tenantTransaction.commit();
    }

    @Override
    public void requestUpdated() {
        callMaintenance();
    }

    @Override
    public void callManagerStaff(String staffFunction) {
        // switch to the main add fragment task - bypass the tenant view
        FragmentManager staffManager = getSupportFragmentManager();
        FragmentTransaction staffTransaction = staffManager.beginTransaction();
        StaffView sv = new StaffView().newInstance(staffFunction);
        staffTransaction.replace(R.id.main_view_container, sv);
        staffTransaction.addToBackStack("managerstaff");
        staffTransaction.commit();
    }

    @Override
    public void displayStaff(Staff staff, Boolean isCurrent) {
        // switch to the main add fragment task - bypass the tenant view
        FragmentManager staffManager = getSupportFragmentManager();
        FragmentTransaction staffTransaction = staffManager.beginTransaction();
        StaffFragment sf = new StaffFragment().newInstance(staff, isCurrent);
        staffTransaction.replace(R.id.main_view_container, sf);
        staffTransaction.addToBackStack("viewstaff");
        staffTransaction.commit();
    }

    @Override
    public void addNewStaff() {
        // switch to the main add fragment task - bypass the tenant view
        FragmentManager newStaffManager = getSupportFragmentManager();
        FragmentTransaction newStaffTransaction = newStaffManager.beginTransaction();
        StaffFragment sf = new StaffFragment();
        newStaffTransaction.replace(R.id.main_view_container, sf);
        newStaffTransaction.addToBackStack("newstaff");
        newStaffTransaction.commit();
    }

    @Override
    public void dismissStaffFragment() {
        openMenuOption("staff");
    }

    @Override
    public void companyUpdated() {
        callHome();
    }

    @Override
    public void displayMessages(Tenant tenant) {
        // switch to the main add fragment task - bypass the tenant view
        FragmentManager messagesManager = getSupportFragmentManager();
        FragmentTransaction messagesTransaction = messagesManager.beginTransaction();
        MessagesView sf = new MessagesView().newInstance(tenant);
        messagesTransaction.replace(R.id.main_view_container, sf);
        messagesTransaction.addToBackStack("managermessages");
        messagesTransaction.commit();
    }
}
