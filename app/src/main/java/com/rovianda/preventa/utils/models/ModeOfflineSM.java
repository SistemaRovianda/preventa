package com.rovianda.preventa.utils.models;

import java.util.List;

public class ModeOfflineSM {

    private String folio;
    private String date;
    private String dateToDeliver;
    private Float amount;
    private String sellerId;
    private Integer clientId;
    private List<ModeOfflineSMP> products;

    private Boolean urgent;

    public Boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }

    public String getDateToDeliver() {
        return dateToDeliver;
    }

    public void setDateToDeliver(String dateToDeliver) {
        this.dateToDeliver = dateToDeliver;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public List<ModeOfflineSMP> getProducts() {
        return products;
    }

    public void setProducts(List<ModeOfflineSMP> products) {
        this.products = products;
    }
}
