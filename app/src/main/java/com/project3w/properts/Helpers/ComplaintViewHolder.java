package com.project3w.properts.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project3w.properts.R;

/**
 * Created by Nate on 10/11/17.
 */

public class ComplaintViewHolder extends RecyclerView.ViewHolder {

    // class variables
    public TextView complaintTitle, complaintStatus, complaintDate;
    //public Request requestItem;

    public ComplaintViewHolder(View itemView) {
        super(itemView);

        complaintTitle = itemView.findViewById(R.id.complaint_title);
        complaintStatus = itemView.findViewById(R.id.complaint_status);
        complaintDate = itemView.findViewById(R.id.complaint_date);

        //listener set on ENTIRE ROW
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());

            }
        });
    }

    private ComplaintViewHolder.ClickListener mClickListener;

    //Interface to send callbacks...
    public interface ClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnClickListener(ComplaintViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }

}
