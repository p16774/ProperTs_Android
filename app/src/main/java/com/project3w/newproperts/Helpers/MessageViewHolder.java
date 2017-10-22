package com.project3w.newproperts.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project3w.newproperts.R;

/**
 * Created by Nate on 10/21/17.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    // class variables
    public TextView messageText, timeText;

    public MessageViewHolder (View itemView) {
        super(itemView);

        messageText = itemView.findViewById(R.id.message_body);
        timeText = itemView.findViewById(R.id.message_time);
    }
}
