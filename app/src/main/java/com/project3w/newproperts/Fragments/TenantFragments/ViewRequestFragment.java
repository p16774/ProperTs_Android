package com.project3w.newproperts.Fragments.TenantFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project3w.newproperts.Helpers.GlideApp;
import com.project3w.newproperts.Objects.Request;
import com.project3w.newproperts.R;

/**
 * Created by Nate on 10/10/17.
 */

public class ViewRequestFragment extends Fragment {

    // class variables
    public static final String REQUEST_INFO = "com.project3w.properts.REQUEST_INFO";
    Request currentRequest;
    TextView requestID, requestTitle, requestContent, requestUrgency, requestDate, requestStatus;
    ImageView requestOpenImage, requestClosedImage;
    Activity mActivity;

    public interface DismissFragmentListener {
        void dismissRequestFragment();
    }

    DismissFragmentListener onDismissFragmentListener;


    public ViewRequestFragment newInstance(Request request) {

        // create our fragment
        ViewRequestFragment myFragment = new ViewRequestFragment();

        // create bundle for our object
        Bundle args = new Bundle();
        args.putSerializable(REQUEST_INFO, request);

        // attach our bundle and return
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // create view
        View view = inflater.inflate(R.layout.view_request, container, false);

        // set our options menus
        setHasOptionsMenu(true);

        // attach the interface listener
        mActivity = getActivity();
        try {
            onDismissFragmentListener = (DismissFragmentListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement DismissFragmentListener");
        }

        mActivity.setTitle("View Request");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // pull in our references to applying data
        requestID = getActivity().findViewById(R.id.view_request_id);
        requestTitle = getActivity().findViewById(R.id.view_request_title);
        requestContent = getActivity().findViewById(R.id.view_request_content);
        requestUrgency = getActivity().findViewById(R.id.view_request_urgency);
        requestStatus = getActivity().findViewById(R.id. view_request_status);
        requestDate = getActivity().findViewById(R.id.view_request_date);
        requestOpenImage = getActivity().findViewById(R.id.view_request_image_open);
        requestClosedImage = getActivity().findViewById(R.id.view_request_image_closed);

        // grab our request for viewing
        currentRequest = (Request) getArguments().getSerializable(REQUEST_INFO);

        if (currentRequest != null) {
            requestID.setText(currentRequest.getRequestID());
            requestTitle.setText(currentRequest.getRequestTitle());
            requestContent.setText(currentRequest.getRequestContent());
            requestUrgency.setText(currentRequest.getRequestUrgency());
            requestStatus.setText(currentRequest.getRequestStatus());
            requestDate.setText(currentRequest.getRequestDate());

            // pull images as available from firebase storage
            // get our storage reference
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://properts-8db06.appspot.com/");
            StorageReference imageOpenRef = null, imageClosedRef = null;

            // check for value on open image before pulling file
            if (!currentRequest.getRequestOpenImagePath().equals("")) {
                imageOpenRef = storageRef.child("requestImages/" + currentRequest.getRequestID() + "/" + currentRequest.getRequestOpenImagePath());
            }

            // check for value on closed image before pulling file
            if (!currentRequest.getRequestClosedImagePath().equals("")) {
                imageClosedRef = storageRef.child("requestImages/" + currentRequest.getRequestID() + "/" + currentRequest.getRequestClosedImagePath());
            }

            // check for null on open image
            if (imageOpenRef != null) {
                // download and set our imageview
                GlideApp.with(getActivity())
                        .load(imageOpenRef)
                        .into(requestOpenImage);
            }

            // check for null on closed image
            if (imageClosedRef != null) {
                // download and set our imageview
                GlideApp.with(getActivity())
                        .load(imageClosedRef)
                        .into(requestClosedImage);
            }

        } else {
            System.out.println("WHAT THE HELL?!?!?! WHAT HAPPENED!!!!!!!!!!???");
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.view_request_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // pull our id of item selected
        int id = item.getItemId();

        if (id == R.id.action_close_view) {
            onDismissFragmentListener.dismissRequestFragment();
        }

        return super.onOptionsItemSelected(item);

    }
}
