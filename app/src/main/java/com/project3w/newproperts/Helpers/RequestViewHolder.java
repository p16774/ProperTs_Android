package com.project3w.newproperts.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project3w.newproperts.R;

/**
 * Created by Nate on 10/7/17.
 */

public class RequestViewHolder extends RecyclerView.ViewHolder {

    // class variables
    public TextView requestTitle, requestStatus, requestDate, managerRequestTitle;
    //public Request requestItem;

    public RequestViewHolder(View itemView) {
        super(itemView);

        requestTitle = itemView.findViewById(R.id.request_title);
        requestStatus = itemView.findViewById(R.id.request_status);
        requestDate = itemView.findViewById(R.id.request_date);
        managerRequestTitle = itemView.findViewById(R.id.manager_request_title);

        //listener set on ENTIRE ROW
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());

            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });

    }

    private RequestViewHolder.ClickListener mClickListener;

    //Interface to send callbacks...
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(RequestViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}
