package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.Helpers.GlideApp;
import com.project3w.newproperts.Objects.Request;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.R;

import java.util.Date;

import static com.project3w.newproperts.Fragments.ManagerFragments.TenantsFragment.TENANT_INFO;

/**
 * Created by Nate on 10/18/17.
 */

public class RequestFragment extends Fragment {

    // class variables
    TextView requestDateView, requestTitleView, requestContentView, requestTenantView, requestTenantAddressView;
    ImageView requestOpenImageView, requestClosedImageView;
    ScrollView requestScrollView;
    EditText managerUpdateContentView;
    Button managerUpdateBtn, managerClosedBtn;
    Activity mActivity;
    Request request;
    Boolean isClosed;
    Tenant tenant;
    String requestType;
    FirebaseDataHelper mHelper;

    public static final String REQUEST_INFO = "com.project3w.properts.REQUEST_INFO";
    public static final String CLOSED_REQUEST = "com.project3w.properts.CLOSED_REQUEST";
    public static final String REQUEST_TYPE = "com.project3w.properts.REQUEST_TYPE";


    public interface RequestUpdateListener {
        void requestUpdated();
    }

    RequestUpdateListener onRequestUpdateListener;


    public RequestFragment newInstance(Request request, Tenant tenant, String requestType, Boolean isClosed) {

        RequestFragment myFragment = new RequestFragment();
        Bundle args = new Bundle();
        args.putSerializable(REQUEST_INFO, request);
        args.putSerializable(TENANT_INFO, tenant);
        args.putString(REQUEST_TYPE, requestType);
        args.putBoolean(CLOSED_REQUEST, isClosed);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_manager_request, container, false);

        mActivity = getActivity();
        mHelper = new FirebaseDataHelper(getActivity());

        request = (Request) getArguments().getSerializable(REQUEST_INFO);
        tenant = (Tenant) getArguments().getSerializable(TENANT_INFO);
        isClosed = getArguments().getBoolean(CLOSED_REQUEST);
        requestType = getArguments().getString(REQUEST_TYPE, "active");

        // assign our references
        requestDateView = view.findViewById(R.id.manager_request_viewdate);
        requestTitleView = view.findViewById(R.id.manager_request_viewtitle);
        requestContentView = view.findViewById(R.id.manager_request_viewcontent);
        requestTenantView = view.findViewById(R.id.manager_request_tenantname);
        requestTenantAddressView = view.findViewById(R.id.manager_request_tenantaddress);
        managerUpdateContentView = view.findViewById(R.id.manager_request_reply);
        requestOpenImageView = view.findViewById(R.id.manager_request_image_open);
        managerUpdateBtn = view.findViewById(R.id.manager_update_request_btn);
        managerClosedBtn = view.findViewById(R.id.manager_close_request_btn);

        // update our scrolling movement to properly scroll text OR the main windows
        requestScrollView = view.findViewById(R.id.manager_request_scrollview);
        requestContentView.setMovementMethod(new ScrollingMovementMethod());
        requestScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                requestContentView.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        requestContentView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                requestContentView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        // if complaint has been closed, remove the update functions
        if(isClosed) {
            managerUpdateContentView.setVisibility(View.GONE);
            managerUpdateBtn.setVisibility(View.GONE);
            managerClosedBtn.setVisibility(View.GONE);
        }

        // attach our listener
        try {
            onRequestUpdateListener = (RequestUpdateListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement RequestUpdateListener");
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (request != null && tenant != null) {

            requestDateView.setText(request.getRequestDate());
            requestTitleView.setText(request.getRequestTitle());
            requestContentView.setText(request.getRequestContent());
            requestTenantView.setText(tenant.getTenantFirstName() +  " " + tenant.getTenantLastName());
            requestTenantAddressView.setText(tenant.getTenantAddress());

            // pull images as available from firebase storage
            // get our storage reference
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://properts-8db06.appspot.com/");
            StorageReference imageOpenRef = null, imageClosedRef = null;

            // check for value on open image before pulling file
            if (!request.getRequestOpenImagePath().equals("")) {
                imageOpenRef = storageRef.child("requestImages/" + request.getRequestID() + "/" + request.getRequestOpenImagePath());
            }

            /*// check for value on closed image before pulling file
            if (!request.getRequestClosedImagePath().equals("")) {
                imageClosedRef = storageRef.child("requestImages/" + request.getRequestID() + "/" + request.getRequestClosedImagePath());
            }*/

            // check for null on open image
            if (imageOpenRef != null) {
                // download and set our imageview
                GlideApp.with(getActivity())
                        .load(imageOpenRef)
                        .into(requestOpenImageView);
            }

            /*// check for null on closed image
            if (imageClosedRef != null) {
                // download and set our imageview
                GlideApp.with(getActivity())
                        .load(imageClosedRef)
                        .into(requestClosedImageView);
            }*/

            managerUpdateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String managerReply = managerUpdateContentView.getText().toString().trim(),
                            requestTo = "", requestFrom = "";
                    if (!managerReply.isEmpty()) {
                        String requestUpdate = request.getRequestContent() + "\n\n*** Manager Update ***\n" + new Date().toString() + "\n\n" + managerReply;
                        request.setRequestContent(requestUpdate);

                        switch (requestType) {
                            case "new":
                                requestTo = "active";
                                requestFrom = requestType;
                                request.setRequestStatus("Active");
                                break;
                            case "active":
                                requestTo = requestType;
                                requestFrom = requestType;
                                break;
                            case "critical":
                                // check to see if our urgency level is still critical after we have viewed a critical request
                                if (request.getRequestUrgency().equals("Critical")) {
                                    requestTo = requestType;
                                    requestFrom = requestType;
                                } else {
                                    requestTo = "active";
                                    requestFrom = requestType;
                                }
                        }

                        mHelper.updateRequest(request, tenant, requestTo, requestFrom);
                        onRequestUpdateListener.requestUpdated();

                    } else {
                        Snackbar.make(mActivity.findViewById(android.R.id.content), "Update content needed to submit request", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

            managerClosedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHelper.closeRequest(request, tenant, requestType);
                    onRequestUpdateListener.requestUpdated();
                }
            });
        }
    }
}
