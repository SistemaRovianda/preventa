package com.rovianda.preventa.visits.models;

public class ClientVisitListItem {
    private Integer clientId;
    private Integer keyClient;
    private String clientName;
    private Double latitude;
    private Double longitude;
    private boolean visited;
    private Boolean mustVisited;
    private boolean selected;
    private Float amount;
    private boolean isKeyClientTemp;
    private boolean availableForRecordOfVisit;

    public boolean isKeyClientTemp() {
        return isKeyClientTemp;
    }

    public void setKeyClientTemp(boolean keyClientTemp) {
        isKeyClientTemp = keyClientTemp;
    }

    public boolean isAvailableForRecordOfVisit() {
        return availableForRecordOfVisit;
    }

    public void setAvailableForRecordOfVisit(boolean availableForRecordOfVisit) {
        this.availableForRecordOfVisit = availableForRecordOfVisit;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public ClientVisitListItem(){
        this.selected=false;
        this.amount=0f;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return this.clientName;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(Integer keyClient) {
        this.keyClient = keyClient;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public Boolean getMustVisited() {
        return mustVisited;
    }

    public void setMustVisited(Boolean mustVisited) {
        this.mustVisited = mustVisited;
    }
}
