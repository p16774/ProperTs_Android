package com.project3w.properts.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project3w.properts.Helpers.FirebaseDataHelper;
import com.project3w.properts.R;

import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * Created by Nate on 10/4/17.
 */

public class SignUpFragment extends Fragment {

    // class variables
    public static final String TENANT_ID = "com.project3w.properts.TENANT_ID";
    String newTenant;
    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button signupButton;
    Activity mActivity;

    // Firebase Auth Variable
    FirebaseAuth mAuth;

    public static SignUpFragment newInstance(String tenantID) {

        SignUpFragment sf = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(TENANT_ID, tenantID);
        sf.setArguments(args);

        return sf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // ease of using the same activity assigning it to a variable
        mActivity = getActivity();

        // pull in our bundled tenantID we already verified is ready for an account
        Bundle tenantInfo = getArguments();
        newTenant = tenantInfo.getString(TENANT_ID);

        // create our FirebaseAuth pathway
        mAuth = FirebaseAuth.getInstance();

        // log out the anonymous user to prevent any problems with other logged in data points
        mAuth.signOut();

        // return the layout
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Link variables to XML
        emailEditText = (EditText) getActivity().findViewById(R.id.signupEmailEditText);
        passwordEditText = (EditText) getActivity().findViewById(R.id.signupPasswordEditText);
        confirmPasswordEditText = (EditText) getActivity().findViewById(R.id.signupConfirmPasswordEditText);
        signupButton = (Button) getActivity().findViewById(R.id.signupCompleteButton);

        //Setup signup button listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if input email is a valid email and password and confirm password fields have 7 or more characters and match, return to login activity
                if (isValidEmail(emailEditText.getText().toString()) &&
                        passwordEditText.getText().length() > 6 &&
                        Objects.equals(passwordEditText.getText().toString(), confirmPasswordEditText.getText().toString())) {

                    // pull in our email/password entered
                    String inputEmail = emailEditText.getText().toString();
                    String inputPassword = passwordEditText.getText().toString();

                    // sign up the user with a new account
                    mAuth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                            .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("Firebase", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    // pull our userID to update tenant information
                                    FirebaseUser newUser = mAuth.getCurrentUser();
                                    Boolean didCreate = false;
                                    try {
                                        String userID = newUser.getUid();
                                        FirebaseDataHelper firebaseDataHelper = new FirebaseDataHelper(getActivity());
                                        didCreate = firebaseDataHelper.updateNewTenantAccount(newTenant, userID);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Snackbar.make(mActivity.findViewById(android.R.id.content),
                                                "Something went terribly wrong. Please read the StackTrace.",
                                                Snackbar.LENGTH_INDEFINITE).show();
                                    }

                                    if (didCreate) {
                                        //TODO: send user to tenant screen and log them in
                                    }

                                    // If sign in fails, display a message to the user.
                                    if (!task.isSuccessful()) {
                                        Snackbar.make(mActivity.findViewById(android.R.id.content), "It appears that an account with this email address has already been created. Please use another email.",
                                                Snackbar.LENGTH_LONG).show();
                                    }

                                }
                            });

                } else {
                    //Setup logic for user creation error.
                    if (!isValidEmail(emailEditText.getText().toString())) {
                        Toast.makeText(mActivity, "You must enter a valid email in order to create an account.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (passwordEditText.getText().length() <= 6) {
                        Toast.makeText(mActivity, "Password must be at least 7 characters in length.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!Objects.equals(passwordEditText.getText().toString(), confirmPasswordEditText.getText().toString())) {
                        Toast.makeText(mActivity, "Password fields do not match.", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
