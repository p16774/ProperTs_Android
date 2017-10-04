package com.project3w.properts.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.project3w.properts.Helpers.FirebaseDataHelper;
import com.project3w.properts.R;

/**
 * Created by Nate on 10/3/17.
 */

public class VerifyFragment extends Fragment {

    // class variables
    private EditText verifyCode;
    Button verifyBtn;

    public VerifyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verify, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        verifyCode = (EditText) getActivity().findViewById(R.id.verify_code);
        verifyBtn = (Button) getActivity().findViewById(R.id.verify_btn);

        final FirebaseDataHelper firebaseDataHelper = new FirebaseDataHelper(getActivity());

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isValid = firebaseDataHelper.verifyAccount(verifyCode.getText().toString());
                if (isValid) {
                    //TODO: display signin screen with interface back to activity with anon auth data
                }
            }
        });

    }
}
