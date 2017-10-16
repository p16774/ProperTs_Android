package com.project3w.newproperts.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.project3w.newproperts.R;

import java.util.Objects;

/**
 * Created by Nate on 10/12/17.
 */

public class CreateAccount extends Fragment {

    // class variables
    public static final String ACCOUNT_TYPE = "com.project3w.properts.ACCOUNT_TYPE";
    EditText usernameView, passwordView, confirmView;
    Button createBtn;
    String accountType;
    Activity mActivity;
    FirebaseAuth mAuth;

    public interface NextStepListener {
        void performNextStep(String type);
    }

    NextStepListener onNextStepListener;

    public CreateAccount newInstance(String _accountType) {
        // create fragment
        CreateAccount createFragment = new CreateAccount();
        Bundle args = new Bundle();
        args.putString(ACCOUNT_TYPE, _accountType);
        createFragment.setArguments(args);

        return createFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_signup, container, false);

        // assign variables and references
        mActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();

        usernameView = view.findViewById(R.id.signupEmailEditText);
        passwordView = view.findViewById(R.id.signupPasswordEditText);
        confirmView = view.findViewById(R.id.signupConfirmPasswordEditText);
        createBtn = view.findViewById(R.id.signupCompleteButton);

        try {
            onNextStepListener = (NextStepListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must fully implement NextStepListener");
        }

        mActivity.setTitle("Create Account");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set our account type
        accountType = getArguments().getString(ACCOUNT_TYPE);
        System.out.println("ACCOUNT TYPE: " + accountType);


        //Setup signup button listener
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if input email is a valid email and password and confirm password fields have 7 or more characters and match, return to login activity
                if (isValidEmail(usernameView.getText().toString()) &&
                        passwordView.getText().length() > 6 &&
                        Objects.equals(passwordView.getText().toString(), confirmView.getText().toString())) {

                    // pull in our email/password entered
                    String inputEmail = usernameView.getText().toString();
                    String inputPassword = passwordView.getText().toString();

                    // sign up the user with a new account
                    mAuth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                            .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("Firebase", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    // perform the next steps
                                    onNextStepListener.performNextStep(accountType);

                                    /*// pull our userID to update tenant information
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
                                        Snackbar.make(mActivity.findViewById(android.R.id.content),
                                                "It appears that an account with this email address has already been created. " +
                                                        "Please use another email, or sign in with your account.",
                                                Snackbar.LENGTH_LONG).show();
                                    }*/

                                }
                            });

                } else {
                    //Setup logic for user creation error.
                    if (!isValidEmail(usernameView.getText().toString())) {
                        Toast.makeText(mActivity, "You must enter a valid email in order to create an account.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (passwordView.getText().length() <= 6) {
                        Toast.makeText(mActivity, "Password must be at least 7 characters in length.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!Objects.equals(passwordView.getText().toString(), confirmView.getText().toString())) {
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
