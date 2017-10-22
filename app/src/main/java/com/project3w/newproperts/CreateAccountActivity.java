package com.project3w.newproperts;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project3w.newproperts.Fragments.ChooseAccountType;
import com.project3w.newproperts.Fragments.CreateAccount;
import com.project3w.newproperts.Fragments.NewCompanyCreation;
import com.project3w.newproperts.Fragments.StaffFragments.VerifyStaffFragment;
import com.project3w.newproperts.Fragments.TenantFragments.VerifyTenantFragment;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;

import static com.project3w.newproperts.Fragments.CreateAccount.ACCOUNT_TYPE;

/**
 * Created by Nate on 10/12/17.
 */

public class CreateAccountActivity extends AppCompatActivity implements ChooseAccountType.CreateAccountListener, CreateAccount.NextStepListener {

    // class variables
    FirebaseDataHelper firebaseDataHelper;
    String accountType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // check bools.xml and set the proper screen orientation for device widths
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // assign our Firebase Helper class
        firebaseDataHelper = new FirebaseDataHelper(this);

        // pull our intent for extra to verify user state in creation process
        if(getIntent().hasExtra(ACCOUNT_TYPE)) {
            accountType = getIntent().getExtras().getString(ACCOUNT_TYPE);
            performNextStep(accountType);
        } else {
            // add in our create fragment
            createAccountView();
        }
    }

    private void createAccountView() {
        // add our fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.create_container, new ChooseAccountType()).commit();
    }


    @Override
    public void createNewAccount(String accountType) {
        // replace the fragment with the selected accountType
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.create_container, new CreateAccount().newInstance(accountType)).commit();
    }

    @Override
    public void performNextStep(String type) {
        // get the current user that was just created in the last step
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        // validate for null to make sure we are logged in
        if (mUser != null) {

            // depending on the type of account that was just created, perform the next step
            switch (type) {
                case "tenant":
                    // send the new tenant to the verification screen to attach their UserID to their TenantID
                    FragmentTransaction tenantVerification = getSupportFragmentManager().beginTransaction();
                    tenantVerification.replace(R.id.create_container, new VerifyTenantFragment()).commit();
                    break;
                case "staff":
                    // send the new tenant to the verification screen to attach their UserID to their TenantID
                    FragmentTransaction staffVerification = getSupportFragmentManager().beginTransaction();
                    staffVerification.replace(R.id.create_container, new VerifyStaffFragment()).commit();
                    break;
                case "manager":
                    // send the new manager to the company creation screen
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.create_container, new NewCompanyCreation()).commit();
                    break;
            }
        }
    }
}
