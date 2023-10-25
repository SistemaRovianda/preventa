package com.rovianda.preventa.utils;

import java.util.List;

public class ModeOfflineModel {

    public ModeOfflineModel(){
        this.logedIn=false;
    }

    private Boolean logedIn;
    private String username;
    private String lastSincronization;
    private String sellerId;

    public Boolean getLogedIn() {
        return logedIn;
    }

    public void setLogedIn(Boolean logedIn) {
        this.logedIn = logedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastSincronization() {
        return lastSincronization;
    }

    public void setLastSincronization(String lastSincronization) {
        this.lastSincronization = lastSincronization;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
