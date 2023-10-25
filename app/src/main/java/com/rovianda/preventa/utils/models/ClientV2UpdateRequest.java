package com.rovianda.preventa.utils.models;

import java.io.Serializable;

public class ClientV2UpdateRequest implements Serializable {

    private Integer clientId;
    private Integer clientKey;
    private String clientName;
    private String clientStreet;
    private String clientSuburb;
    private String clientMunicipality;
    private String clientCp;
    private String clientExtNumber;
    private String clientContact;
    private String clientPhoneNumber;
    private String clientReference;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private double longitude;
    private double latitude;

    public String getClientContact() {
        return clientContact;
    }

    public void setClientContact(String clientContact) {
        this.clientContact = clientContact;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getClientReference() {
        return clientReference;
    }

    public void setClientReference(String clientReference) {
        this.clientReference = clientReference;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getClientKey() {
        return clientKey;
    }

    public void setClientKey(Integer clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientStreet() {
        return clientStreet;
    }

    public void setClientStreet(String clientStreet) {
        this.clientStreet = clientStreet;
    }

    public String getClientSuburb() {
        return clientSuburb;
    }

    public void setClientSuburb(String clientSuburb) {
        this.clientSuburb = clientSuburb;
    }

    public String getClientMunicipality() {
        return clientMunicipality;
    }

    public void setClientMunicipality(String clientMunicipality) {
        this.clientMunicipality = clientMunicipality;
    }

    public String getClientCp() {
        return clientCp;
    }

    public void setClientCp(String clientCp) {
        this.clientCp = clientCp;
    }

    public String getClientExtNumber() {
        return clientExtNumber;
    }

    public void setClientExtNumber(String clientExtNumber) {
        this.clientExtNumber = clientExtNumber;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
