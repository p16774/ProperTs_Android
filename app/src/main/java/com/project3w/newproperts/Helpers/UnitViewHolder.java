package com.project3w.newproperts.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project3w.newproperts.R;

/**
 * Created by Nate on 10/14/17.
 */

public class UnitViewHolder extends RecyclerView.ViewHolder {

    // class variables
    public TextView unitAddress;

    public UnitViewHolder(View itemView) {
        super(itemView);

        unitAddress = itemView.findViewById(R.id.unit_address);

        //listener set on ENTIRE ROW
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());

            }
        });
    }

    private UnitViewHolder.ClickListener mClickListener;

    //Interface to send callbacks...
    public interface ClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnClickListener(UnitViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}
