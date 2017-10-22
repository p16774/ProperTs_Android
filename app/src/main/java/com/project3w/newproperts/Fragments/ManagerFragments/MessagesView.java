package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.Helpers.MessageViewHolder;
import com.project3w.newproperts.LoginActivity;
import com.project3w.newproperts.Objects.Message;
import com.project3w.newproperts.Objects.Tenant;
import com.project3w.newproperts.R;

import static com.project3w.newproperts.Fragments.ManagerFragments.TenantsFragment.TENANT_INFO;
import static com.project3w.newproperts.MainActivity.COMPANY_CODE;
import static com.project3w.newproperts.MainActivity.TENANT_ID;

/**
 * Created by Nate on 10/21/17.
 */

public class MessagesView extends Fragment {

    RecyclerView mMessageRecycler;
    FirebaseRecyclerAdapter mMessageAdapter;
    EditText messageContentView;
    Button messageSendBtn;
    String companyCode, tenantID, tenantName;
    Tenant currentTenant;
    Activity mActivity;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Context mContext;
    FirebaseDataHelper mHelper;

    public MessagesView newInstance(Tenant tenant) {

        MessagesView myFragment = new MessagesView();
        Bundle args = new Bundle();
        args.putSerializable(TENANT_INFO, tenant);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_message, container, false);

        mActivity = getActivity();
        mHelper = new FirebaseDataHelper(mActivity);
        mContext = mActivity;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        currentTenant = (Tenant) getArguments().getSerializable(TENANT_INFO);
        if (currentTenant != null) {
            tenantID = currentTenant.getTenantID();
            tenantName = currentTenant.getTenantFirstName() + " "  + currentTenant.getTenantLastName();
        }

        setHasOptionsMenu(true);

        mMessageRecycler = view.findViewById(R.id.reyclerview_message_list);
        messageContentView = view.findViewById(R.id.tenant_message_content);
        messageSendBtn = view.findViewById(R.id.tenant_message_send);

        // send user to the login screen if they aren't logged in
        if (mUser == null) {
            Intent loginScreen = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginScreen);
            getActivity().finish();
        } else {
            // grab our company code from shared preferences
            SharedPreferences mPrefs = mActivity.getSharedPreferences("com.project3w.properts", Context.MODE_PRIVATE);
            companyCode = mPrefs.getString(COMPANY_CODE, null);
        }

        mActivity.setTitle(tenantName);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.tenant_menu, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // make sure we have a tenantID first
        if (!tenantID.isEmpty()) {

            // grab the reference to our RecyclerView
            final LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
            //layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);

            // setup our database references
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            Query tenantMessageQuery = firebaseDatabase.getReference()
                    .child(companyCode).child("1")
                    .child("messages")
                    .child(tenantID);

            // setup our RecyclerView to display content
            FirebaseRecyclerOptions<Message> messageOptions =
                    new FirebaseRecyclerOptions.Builder<Message>()
                            .setQuery(tenantMessageQuery, Message.class)
                            .build();

            mMessageAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(messageOptions) {

                @Override
                public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    // Create a new instance of the ViewHolder, in this case we are using a custom
                    // layout called R.layout.message for each item
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(viewType, parent, false);
                    return new MessageViewHolder(view);
                }

                @Override
                public int getItemViewType(int position) {
                    Message currentMessage = getItem(position);
                    if (currentMessage.getMessageSender().equals("tenant")) {
                        return R.layout.message_receiver;
                    } else {
                        return R.layout.message_sender;
                    }
                }

                @Override
                protected void onBindViewHolder(final MessageViewHolder holder, int position, final Message message) {

                    holder.messageText.setText(message.getMessageContent());

                    // Format the stored timestamp into a readable String using method.
                    holder.timeText.setText(DateUtils.formatDateTime(mContext, message.getMessageDate(), DateUtils.FORMAT_SHOW_TIME));

                }
            };

            mMessageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int friendlyMessageCount = mMessageAdapter.getItemCount();
                    int lastVisiblePosition =
                            layoutManager.findLastCompletelyVisibleItemPosition();
                    // If the recycler view is initially being loaded or the
                    // user is at the bottom of the list, scroll to the bottom
                    // of the list to show the newly added message.
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (friendlyMessageCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) {
                        mMessageRecycler.scrollToPosition(positionStart);
                    }
                }
            });

            // call our recycler
            mMessageRecycler.setAdapter(mMessageAdapter);
            mMessageRecycler.setLayoutManager(layoutManager);

        } else {
            Snackbar.make(mActivity.findViewById(android.R.id.content), "ERROR", Snackbar.LENGTH_SHORT).show();
        }

        messageSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get message content and make sure there's content before sending
                String messageContent = messageContentView.getText().toString().trim();
                if(!messageContent.isEmpty()) {
                    // create our date
                    long date = System.currentTimeMillis();
                    // create message and send
                    Message newMessage = new Message(messageContent,"manager", date);
                    mHelper.sendManagerMessage(newMessage, tenantID);
                    // empty the message sent
                    messageContentView.setText("");
                    // Check if no view has focus:
                    View view = mActivity.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mMessageAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMessageAdapter.stopListening();
    }
}
