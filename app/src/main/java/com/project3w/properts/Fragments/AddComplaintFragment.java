package com.project3w.properts.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.project3w.properts.Helpers.FirebaseDataHelper;
import com.project3w.properts.Objects.Complaint;
import com.project3w.properts.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nate on 10/8/17.
 */

public class AddComplaintFragment extends Fragment {

    // class variables
    EditText complaintTitleView, complaintContentView;
    Activity mActivity;

    public interface DismissFragmentListener {
        void dismissComplaintFragment();
    }


    DismissFragmentListener onDismissFragmentListener;

    public AddComplaintFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set to be able to replace menu for the fragment
        View view = inflater.inflate(R.layout.add_complaint, container, false);
        setHasOptionsMenu(true);

        // attach the interface listener
        mActivity = getActivity();
        try {
            onDismissFragmentListener = (DismissFragmentListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement DismissFragmentListener");
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // grab our references to get values when user clicks save
        complaintTitleView = getActivity().findViewById(R.id.new_complaint_title);
        complaintContentView = getActivity().findViewById(R.id.new_complaint_content);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.add_complaint_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // pull our id of item selected
        int id = item.getItemId();

        if (id == R.id.action_save_complaint) {
            submitNewComplaint();
        }

        return super.onOptionsItemSelected(item);

    }

    public boolean submitNewComplaint() {

        // check for null fields and assign necessary values to others
        if (complaintTitleView.getText().toString().trim().equals("") || complaintContentView.getText().toString().trim().equals("")) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must fill out all fields.",
                    Snackbar.LENGTH_LONG).show();
            return false;
        } else {

            // get our firebase helper reference
            FirebaseDataHelper firebaseDataHelper = new FirebaseDataHelper(getActivity());

            // pull our data and create our request item
            String title = complaintTitleView.getText().toString();
            String content = complaintContentView.getText().toString();
            String status = "New";

            // convert our date object for swift iOS implementation
            SimpleDateFormat fmt = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss aaa", Locale.US);
            Date currentDate = new Date();
            String date = fmt.format(currentDate);

            // create our Request Object
            Complaint newComplaint = new Complaint(title,content,status,date);
            boolean submitted = firebaseDataHelper.submitComplaint(newComplaint);

            if(submitted) {
                onDismissFragmentListener.dismissComplaintFragment();
            }

            return true;

        }
    }
}
