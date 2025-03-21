package com.deltacodex.EventPulse.model;

import com.google.android.gms.maps.model.LatLng;

public class GamingHub {
    private String name;
    private LatLng latLng;

    public GamingHub(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
