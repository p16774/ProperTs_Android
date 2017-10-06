package com.project3w.properts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.project3w.properts.Fragments.SignUpFragment;
import com.project3w.properts.Fragments.VerifyFragment;

/**
 * Created by Nate on 10/3/17.
 */

public class VerifyActivity extends AppCompatActivity implements VerifyFragment.SignUpUserListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        // add our verify fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        VerifyFragment vf = new VerifyFragment();
        fragmentTransaction.replace(R.id.signup_container, vf);
        fragmentTransaction.commit();
    }


    @Override
    public void signUpUser(String tenantID) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SignUpFragment sf = SignUpFragment.newInstance(tenantID);
        fragmentTransaction.replace(R.id.signup_container, sf);
        fragmentTransaction.commit();
    }
}
