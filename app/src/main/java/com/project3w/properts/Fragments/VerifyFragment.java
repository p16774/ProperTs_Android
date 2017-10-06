package com.project3w.properts.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project3w.properts.Objects.AccountVerification;
import com.project3w.properts.R;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Nate on 10/3/17.
 */

public class VerifyFragment extends Fragment {

    // class variables

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    private EditText verifyCode;
    Button verifyBtn;
    Activity mActivity;

    public interface SignUpUserListener {
        void signUpUser(String tenantID);
    }

    // declare our listener
    SignUpUserListener onSignUpUserListener;

    public VerifyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // attach the interface listener
        mActivity = getActivity();
        try {
            onSignUpUserListener = (SignUpUserListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement SignUpUserListener");
        }

        return inflater.inflate(R.layout.fragment_verify, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        verifyCode = (EditText) getActivity().findViewById(R.id.verify_code);
        verifyBtn = (Button) getActivity().findViewById(R.id.verify_btn);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAccount(verifyCode.getText().toString());
            }
        });

    }

    public void verifyAccount(final String tenantID) {

        //TODO: add a progress indication to show the user we are verifying their account

        // sign in user anonymously
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            mUser = mAuth.getCurrentUser();

                            // pull data and verify id
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference tenantVerifyRef = firebaseDatabase.getReference("needAccount").child(tenantID);

                            // verify the tenantID and display information
                            tenantVerifyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // assign data from database pull
                                    AccountVerification av = dataSnapshot.getValue(AccountVerification.class);
                                    //TODO: validate for null
                                    final String tenantName = av.getTenantName();
                                    String tenantAddress = av.getTenantAddress();

                                    // Use the Builder class for convenient dialog construction
                                    new MaterialDialog.Builder(getActivity())
                                            .title("Confirm Account")
                                            .content("Please confirm that you are " + tenantName + "\nand you are moving into " + tenantAddress + ".")
                                            .positiveText("Confirm")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    // call interface to replace container with the tenantID
                                                    onSignUpUserListener.signUpUser(tenantID);
                                                }
                                            })
                                            .positiveColorRes(R.color.colorBlack)
                                            .negativeText("Deny")
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    //TODO: new function to display message to contact the management immediately
                                                }
                                            })
                                            .negativeColorRes(R.color.colorGrey)
                                            .cancelable(false)
                                            .build()
                                            .show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });
    }
}
