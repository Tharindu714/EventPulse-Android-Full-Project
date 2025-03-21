package com.deltacodex.EventPulse.model;

import com.google.android.gms.maps.model.LatLng;

public class Cinema {
    private String name;
    private LatLng latLng;

    public Cinema(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
