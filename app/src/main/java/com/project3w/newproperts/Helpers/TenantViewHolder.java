package com.project3w.newproperts.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project3w.newproperts.R;

/**
 * Created by Nate on 10/15/17.
 */

public class TenantViewHolder extends RecyclerView.ViewHolder {

    // class variables
    public TextView tenantName, tenantAddress;

    public TenantViewHolder(View itemView) {
        super(itemView);

        tenantName = itemView.findViewById(R.id.tenant_name);
        tenantAddress = itemView.findViewById(R.id.tenant_address);

        //listener set on ENTIRE ROW
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    private TenantViewHolder.ClickListener mClickListener;

    //Interface to send callbacks...
    public interface ClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnClickListener(TenantViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }

}
