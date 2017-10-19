package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.Objects.Complaint;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.R;

import java.util.Date;

import static com.project3w.newproperts.Fragments.ManagerFragments.TenantsFragment.TENANT_INFO;

/**
 * Created by Nate on 10/17/17.
 */

public class ComplaintFragment extends Fragment {

    // class variables
    TextView complaintDateView, complaintTitleView, complaintContentView, complaintTenantView, complaintTenantAddressView;
    EditText managerReplyContentView;
    Button managerAcknowledgeBtn;
    Activity mActivity;
    Complaint complaint;
    Boolean isClosed;
    Tenant tenant;
    FirebaseDataHelper mHelper;

    public static final String COMPLAINT_INFO = "com.project3w.properts.COMPLAINT_INFO";
    public static final String CLOSED_COMPLAINT = "com.project3w.properts.CLOSED_COMPLAINT";


    public interface ComplaintAcknowledgedListener {
        void complaintAcknowledged();
    }

    ComplaintAcknowledgedListener onComplaintAcknowledgedListener;


    public ComplaintFragment newInstance(Complaint complaint, Tenant tenant, Boolean isClosed) {

        ComplaintFragment myFragment = new ComplaintFragment();
        Bundle args = new Bundle();
        args.putSerializable(COMPLAINT_INFO, complaint);
        args.putSerializable(TENANT_INFO, tenant);
        args.putBoolean(CLOSED_COMPLAINT, isClosed);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_manager_complaint, container, false);

        mActivity = getActivity();
        mHelper = new FirebaseDataHelper(getActivity());

        complaint = (Complaint) getArguments().getSerializable(COMPLAINT_INFO);
        tenant = (Tenant) getArguments().getSerializable(TENANT_INFO);
        isClosed = getArguments().getBoolean(CLOSED_COMPLAINT);

        // assign our references
        complaintDateView = view.findViewById(R.id.manager_complaint_date);
        complaintTitleView = view.findViewById(R.id.manager_complaint_complainttitle);
        complaintContentView = view.findViewById(R.id.manager_complaint_complaintcontent);
        complaintContentView.setMovementMethod(new ScrollingMovementMethod());
        complaintTenantView = view.findViewById(R.id.manager_complaint_tenantname);
        complaintTenantAddressView = view.findViewById(R.id.manager_complaint_tenantaddress);
        managerReplyContentView = view.findViewById(R.id.manager_complaint_reply);
        managerAcknowledgeBtn = view.findViewById(R.id.manager_complaint_btn);

        // if complaint has been closed, remove the update functions
        if(isClosed) {
            managerReplyContentView.setVisibility(View.GONE);
            managerAcknowledgeBtn.setVisibility(View.GONE);
        }

        // attach our listener
        try {
            onComplaintAcknowledgedListener = (ComplaintAcknowledgedListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement ComplaintAcknowledgedListener");
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (complaint != null && tenant != null) {

            complaintDateView.setText(complaint.getComplaintDate());
            complaintTitleView.setText(complaint.getComplaintTitle());
            complaintContentView.setText(complaint.getComplaintContent());
            complaintTenantView.setText(tenant.getTenantFirstName() +  " " + tenant.getTenantLastName());
            complaintTenantAddressView.setText(tenant.getTenantAddress());

            managerAcknowledgeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String managerReply = managerReplyContentView.getText().toString().trim();
                    if (!managerReply.isEmpty()) {
                        String complaintUpdate = complaint.getComplaintContent() + "\n\n" + new Date().toString() + "\n*** Manager Reply ***\n\n" + managerReply;
                        complaint.setComplaintContent(complaintUpdate);
                    } else {
                        String noReply = complaint.getComplaintContent() + "\n\n" + new Date().toString() + "\n\n*** Manager Acknowledged ***";
                        complaint.setComplaintContent(noReply);
                    }
                    complaint.setComplaintStatus("Closed");
                    mHelper.acknowledgeComplaint(complaint, tenant);
                    onComplaintAcknowledgedListener.complaintAcknowledged();
                }
            });
        }
    }
}
