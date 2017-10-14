package com.project3w.newproperts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project3w.newproperts.Fragments.ChooseAccountType;
import com.project3w.newproperts.Fragments.CreateAccount;
import com.project3w.newproperts.Fragments.NewCompanyCreation;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;

/**
 * Created by Nate on 10/12/17.
 */

public class CreateAccountActivity extends AppCompatActivity implements ChooseAccountType.CreateAccountListener, CreateAccount.NextStepListener {

    // class variables
    FirebaseDataHelper firebaseDataHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // assign our Firebase Helper class
        firebaseDataHelper = new FirebaseDataHelper(this);

        // add in our create fragment
        createAccountView();
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
                    break;
                case "staff":
                    break;
                case "manager":

                    //TODO: add our logged in user data to firebase since they should be logged in at this point.
                    System.out.println("UID:    " + mUser.getUid());

                    // send the new manager to the company creation screen
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.create_container, new NewCompanyCreation()).commit();
            }
        }
    }
}
