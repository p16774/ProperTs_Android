package com.project3w.newproperts.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project3w.newproperts.R;

/**
 * Created by Nate on 10/19/17.
 */

public class StaffViewHolder extends RecyclerView.ViewHolder {

    // class variables
    public TextView staffName;

    public StaffViewHolder(View itemView) {
        super(itemView);

        staffName = itemView.findViewById(R.id.staff_name);

        //listener set on ENTIRE ROW
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    private StaffViewHolder.ClickListener mClickListener;

    //Interface to send callbacks...
    public interface ClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnClickListener(StaffViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}
