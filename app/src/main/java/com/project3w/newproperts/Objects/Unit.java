package com.project3w.newproperts.Objects;

/**
 * Created by Nate on 10/14/17.
 */

public class Unit {

    // class variables
    String unitAddress, unitBeds, unitBaths, unitSqFt, unitNotes;

    public Unit() {
    }

    public Unit(String unitAddress, String unitBeds, String unitBaths, String unitSqFt, String unitNotes) {
        this.unitAddress = unitAddress;
        this.unitBeds = unitBeds;
        this.unitBaths = unitBaths;
        this.unitSqFt = unitSqFt;
        this.unitNotes = unitNotes;
    }

    public String getUnitAddress() {
        return unitAddress;
    }

    public void setUnitAddress(String unitAddress) {
        this.unitAddress = unitAddress;
    }

    public String getUnitBeds() {
        return unitBeds;
    }

    public void setUnitBeds(String unitBeds) {
        this.unitBeds = unitBeds;
    }

    public String getUnitBaths() {
        return unitBaths;
    }

    public void setUnitBaths(String unitBaths) {
        this.unitBaths = unitBaths;
    }

    public String getUnitSqFt() {
        return unitSqFt;
    }

    public void setUnitSqFt(String unitSqFt) {
        this.unitSqFt = unitSqFt;
    }

    public String getUnitNotes() {
        return unitNotes;
    }

    public void setUnitNotes(String unitNotes) {
        this.unitNotes = unitNotes;
    }
}
