package com.project3w.properts.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project3w.properts.R;

/**
 * Created by Nate on 10/8/17.
 */

public class AddRequestFragment extends Fragment {

    // class variables



    public AddRequestFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set to be able to replace menu for the fragment
        View view = inflater.inflate(R.layout.add_request, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*Toolbar actionBarToolBar = (Toolbar) findViewById(R.id.my_toobar);
        setSupportActionBar(actionBarToolBar);
        actionBarToolBar.setNavigationIcon(R.drawable.icon);
        actionBarToolBar.setNavigationContextDescription(getResources().getString(R.string.desc);
        actionBarToolBar.setLogo(R.drawable.other_icon);
        actionBarToolBar.setLogoDescription(getResources().getString(R.string.other_desc);
        actionBarToolBar.inflateMenu(R.menu.fragment_menu);*/


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.add_request_menu, menu);
    }

}
