package com.deltacodex.EventPulse.model;

import java.util.List;

public class TrailerRowModel {
    private String category;
    private List<TrailerModel> trailers;

    public TrailerRowModel(String category, List<TrailerModel> trailers) {
        this.category = category;
        this.trailers = trailers;
    }

    public String getCategory() { return category; }
    public List<TrailerModel> getTrailers() { return trailers; }
}

