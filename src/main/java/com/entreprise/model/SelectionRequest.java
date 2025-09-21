package com.entreprise.model;

import java.util.List;

public class SelectionRequest {
    private List<Long> entretiensIds;

    public SelectionRequest() {
    }

    public SelectionRequest(List<Long> entretiensIds) {
        this.entretiensIds = entretiensIds;
    }

    public List<Long> getEntretiensIds() {
        return entretiensIds;
    }

    public void setEntretiensIds(List<Long> entretiensIds) {
        this.entretiensIds = entretiensIds;
    }
}
