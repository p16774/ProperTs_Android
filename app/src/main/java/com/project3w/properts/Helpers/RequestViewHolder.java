package com.project3w.properts.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project3w.properts.R;

/**
 * Created by Nate on 10/7/17.
 */

public class RequestViewHolder extends RecyclerView.ViewHolder {

    // class variables
    public TextView requestTitle, requestStatus, requestDate;
    //public Request requestItem;

    public RequestViewHolder(View itemView) {
        super(itemView);

        requestTitle = itemView.findViewById(R.id.request_title);
        requestStatus = itemView.findViewById(R.id.request_status);
        requestDate = itemView.findViewById(R.id.request_date);

    }
}
