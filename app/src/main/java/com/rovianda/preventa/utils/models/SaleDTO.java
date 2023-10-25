package com.rovianda.preventa.utils.models;

import java.util.List;

public class SaleDTO {

    private int keyClient;
    private String sellerId;
    private Float payed;
    private Float amount;
    private String typeSale;
    private Float credit;
    private int days;
    private List<ProductSaleDTO> products;
    private String folio;
    private String statusStr;
    private String clientName;// para menejo offline
    private Boolean status;
    private Integer clientId;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public int getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(int keyClient) {
        this.keyClient = keyClient;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Float getPayed() {
        return payed;
    }

    public void setPayed(Float payed) {
        this.payed = payed;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getTypeSale() {
        return typeSale;
    }

    public void setTypeSale(String typeSale) {
        this.typeSale = typeSale;
    }

    public Float getCredit() {
        return credit;
    }

    public void setCredit(Float credit) {
        this.credit = credit;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public List<ProductSaleDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductSaleDTO> products) {
        this.products = products;
    }
}
