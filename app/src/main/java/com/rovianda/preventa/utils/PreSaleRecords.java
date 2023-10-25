package com.rovianda.preventa.utils;

import java.util.List;

public class PreSaleRecords {

    private Float amount;
    private Integer clientId;
    private String clientName;
    private Float credit;
    private String date;
    private String folio;
    private Integer keyClient;
    private Float payed;
    private Integer preSaleId;
    private String sellerId;
    private String statusStr;
    private Boolean status;
    private String typeSale;
    private Boolean urgent;
    private List<SubSaleOfflineNewVersion> products;
    private String cancelAutorized;

    public Boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }

    public String getCancelAutorized() {
        return cancelAutorized;
    }

    public void setCancelAutorized(String cancelAutorized) {
        this.cancelAutorized = cancelAutorized;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Float getCredit() {
        return credit;
    }

    public void setCredit(Float credit) {
        this.credit = credit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Integer getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(Integer keyClient) {
        this.keyClient = keyClient;
    }

    public Float getPayed() {
        return payed;
    }

    public void setPayed(Float payed) {
        this.payed = payed;
    }

    public Integer getPreSaleId() {
        return preSaleId;
    }

    public void setPreSaleId(Integer preSaleId) {
        this.preSaleId = preSaleId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getTypeSale() {
        return typeSale;
    }

    public void setTypeSale(String typeSale) {
        this.typeSale = typeSale;
    }

    public List<SubSaleOfflineNewVersion> getProducts() {
        return products;
    }

    public void setProducts(List<SubSaleOfflineNewVersion> products) {
        this.products = products;
    }
}
