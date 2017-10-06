package com.project3w.properts;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project3w.properts.Fragments.AddTenantFragment;
import com.project3w.properts.Fragments.ManagerContent;
import com.project3w.properts.Fragments.ManagerHome;
import com.project3w.properts.Fragments.TenantHome;
import com.project3w.properts.Helpers.FirebaseDataHelper;

public class MainActivity extends AppCompatActivity implements AddTenantFragment.DismissFragmentListener, ManagerContent.DismissFragmentListener, ManagerContent.AddNewTenantListener {

    // class variables
    FirebaseUser mUser;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    callHome();
                    return true;
                case R.id.navigation_tenant:

                    return true;
                case R.id.navigation_maintenance:

                    return true;
                case R.id.navigation_message:

                    return true;
                case R.id.navigation_complaint:

                    return true;
            }
            return false;
        }

    };


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

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            Intent loginScreen = new Intent(this, LoginActivity.class);
            startActivity(loginScreen);
            finish();
        } else {

            // grab and set listener on bottom navigation view
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            navigation.setSelectedItemId(R.id.navigation_home);

            // call home fragment on initial start up
            callHome();

        }
    }

    protected void callHome() {

        // pull in the user data and send the appropriate home page flow
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userDataRef = firebaseDatabase.getReference("users").child(mUser.getUid()).child("role");

        // start our Fragment Manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // add our event listen for the one piece of info we need
        // (stupid firebase not allowing me to read directly one simple string)
        userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String userType = dataSnapshot.getValue().toString();

                    switch (userType) {
                        case "manager":

                            // add the ManagerHome fragment
                            ManagerHome mf = new ManagerHome();
                            fragmentTransaction.replace(R.id.content, mf);
                            fragmentTransaction.commit();

                            break;
                        case "tenant":

                            // add the TenantHome fragment
                            TenantHome th = new TenantHome();
                            fragmentTransaction.replace(R.id.content, th);
                            fragmentTransaction.commit();

                            break;
                        case "maintenance":

                            // add the MaintenanceHome Fragment
                            //TODO: create the maintenance home fragment
                            break;

                        //TODO: create default display showing error - no access assigned and prevent usage
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_tenant) {
            // create intent to send the user to the Add Tenant Fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            AddTenantFragment at = new AddTenantFragment();
            fragmentTransaction.replace(R.id.manager_content, at);
            fragmentTransaction.commit();

        } else if (id == R.id.action_change_settings) {
            //TODO: change settings fragment for user

        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void dismissFragment() {
        callHome();
    }

    @Override
    public void addTenantInstead(String unit) {
        //TODO: add tenant fragment with unit

        // create intent to send the user to the Add Tenant Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddTenantFragment at = new AddTenantFragment();
        fragmentTransaction.replace(R.id.manager_content, at);
        fragmentTransaction.commit();

    }
}
