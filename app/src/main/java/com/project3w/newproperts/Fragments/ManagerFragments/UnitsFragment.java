package com.project3w.newproperts.Fragments.ManagerFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.project3w.newproperts.Helpers.FirebaseDataHelper;
import com.project3w.newproperts.Objects.Unit;
import com.project3w.newproperts.R;

/**
 * Created by Nate on 10/14/17.
 */

public class UnitsFragment extends Fragment implements View.OnClickListener {

    // class variables
    Activity mActivity;
    EditText unitAddressView, unitBedsView, unitBathsView, unitSqFtView, unitNotesView;
    Button unitAddBtn, unitDeleteBtn;
    Boolean isUpdate;
    FirebaseDataHelper firebaseDataHelper;
    String unitKey;
    Unit newUnit;

    public static final String UPDATE_UNIT = "com.project3w.properts.UPDATE_UNIT";
    public static final String UNIT_INFO = "com.project3w.properts.UNIT_INFO";

    public interface DismissUnitFragmentListener {
        void dismissUnitFragment();
    }

    // create listener
    DismissUnitFragmentListener onDismissUnitFragmentListener;


    public UnitsFragment newInstance(Boolean editMode, Unit editUnit) {

        UnitsFragment addUnitFragment = new UnitsFragment();
        Bundle args = new Bundle();
        args.putBoolean(UPDATE_UNIT, editMode);
        args.putSerializable(UNIT_INFO, editUnit);
        addUnitFragment.setArguments(args);

        return addUnitFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_units, container, false);

        mActivity = getActivity();

        try {
            onDismissUnitFragmentListener = (DismissUnitFragmentListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement DismissUnitFragmentListener");
        }

        unitAddressView = view.findViewById(R.id.units_address);
        unitBedsView = view.findViewById(R.id.units_beds);
        unitBathsView = view.findViewById(R.id.units_baths);
        unitSqFtView = view.findViewById(R.id.units_sqft);
        unitNotesView = view.findViewById(R.id.units_notes);
        unitAddBtn = view.findViewById(R.id.units_add_btn);
        unitDeleteBtn = view.findViewById(R.id.units_delete_btn);

        unitAddBtn.setOnClickListener(this);

        firebaseDataHelper = new FirebaseDataHelper(getActivity());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        isUpdate = getArguments().getBoolean(UPDATE_UNIT);
        if (isUpdate) {
            // pull in our text data
            newUnit = (Unit) getArguments().getSerializable(UNIT_INFO);
            if(newUnit != null) {
                unitAddressView.setText(newUnit.getUnitAddress());
                unitBedsView.setText(newUnit.getUnitBeds());
                unitBathsView.setText(newUnit.getUnitBaths());
                unitSqFtView.setText(newUnit.getUnitSqFt());
                unitNotesView.setText(newUnit.getUnitNotes());
                unitAddBtn.setText(R.string.update_unit);
                unitKey = newUnit.getUnitID();

                // update our delete button data
                unitDeleteBtn.setVisibility(View.VISIBLE);
                unitDeleteBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
                unitDeleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MaterialDialog.Builder(getActivity())
                                .title(newUnit.getUnitAddress())
                                .content("Are you sure you want to delete \nUnit: " + newUnit.getUnitAddress() + "?")
                                .positiveText("DELETE")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        firebaseDataHelper.deleteSelectedUnit(newUnit);
                                        onDismissUnitFragmentListener.dismissUnitFragment();
                                    }
                                })
                                .positiveColorRes(R.color.colorBlack)
                                .negativeText("CANCEL")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .cancelable(true)
                                .build()
                                .show();
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        Boolean isValid = validateUnit();
        if(isValid) {
            // create our unit
            if(isUpdate) {
                firebaseDataHelper.createNewUnit(newUnit, false);
            } else {
                firebaseDataHelper.createNewUnit(newUnit, true);
            }
            onDismissUnitFragmentListener.dismissUnitFragment();
        }
    }

    public boolean validateUnit() {
        // verify information is filled out, notes field is optional
        String unitAddress = unitAddressView.getText().toString().trim();
        String unitBeds = unitBedsView.getText().toString().trim();
        String unitBaths = unitBathsView.getText().toString().trim();
        String unitSqFt = unitSqFtView.getText().toString().trim();
        String unitNotes = unitNotesView.getText().toString().trim();

        // validate text entered
        if (unitAddress.isEmpty()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You must enter an address to define the Unit.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        // create the unit and return true
        newUnit = new Unit(unitAddress,unitBeds,unitBaths,unitSqFt,unitNotes);

        if(isUpdate) {
            newUnit.setUnitID(unitKey);
        }

        return true;
    }

}
