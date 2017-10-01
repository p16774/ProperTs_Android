package com.project3w.properts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project3w.properts.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    // string values for intents
    public static final String EXTRA_USER_EMAIL = "com.project3w.wharrynathan_tripjournal.EXTRA_USER_EMAIL";
    public static final String EXTRA_USER_PASSWORD = "com.project3w.wharrynathan_tripjournal.EXTRA_USER_PASSWORD";

    // Firebase Auth Variables
    public static FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // class variables for UI functions
    Button signupButton;
    Button loginButton;
    EditText emailEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get reference to our FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // create our listener to validate logged in status
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Firebase", "onAuthStateChanged:signed_in:" + user.getUid());
                    //if signed in, move to data activity
                    Intent toMainScreenIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(toMainScreenIntent);

                    emailEditText.setText("");
                    passwordEditText.setText("");
                } else {
                    // User is signed out
                    Log.d("Firebase", "onAuthStateChanged:signed_out");
                }
            }
        };

        //Link variables to XML
        signupButton = (Button) findViewById(R.id.signUpScreenButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        emailEditText = (EditText) findViewById(R.id.loginEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.loginPasswordEditText);

        //Setup signup button listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move to signup screen
                Intent toSignupScreenIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(toSignupScreenIntent, 0);
            }
        });

        //Setup login button listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setup login logic
                //If email and password edit texts are not empty...
                if (!Objects.equals(emailEditText.getText().toString(), "") &&
                        !Objects.equals(passwordEditText.getText().toString(), "")) {

                    //Signin to Firebase (this will cause onAuthChanged, which will connect to data activity)
                    mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("Firebase", "signInWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.d("Firebase", "signInWithEmail:failed", task.getException());
                                        //Send login fail error toast
                                        Toast.makeText(getApplicationContext(), "Login failed. Please check your email and password and try again.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Enter Login Information", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Add listener to maintain login
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            //Remove listener to maintain login
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override //Create logic for return from signup activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            //get inputEmail and inputPassword strings
            String inputEmail = data.getStringExtra(EXTRA_USER_EMAIL);
            String inputPassword = data.getStringExtra(EXTRA_USER_PASSWORD);

            //create a user with input strings
            mAuth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Firebase", "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user.
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "It appears that an account with this email address has already been created. Please use another email.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }
}
