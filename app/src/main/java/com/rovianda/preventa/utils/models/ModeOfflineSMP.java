package com.rovianda.preventa.utils.models;

public class ModeOfflineSMP {

    private Float quantity,amount;
    private Integer productId,presentationId,appSubSaleId;

    public Integer getAppSubSaleId() {
        return appSubSaleId;
    }

    public void setAppSubSaleId(Integer appSubSaleId) {
        this.appSubSaleId = appSubSaleId;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(Integer presentationId) {
        this.presentationId = presentationId;
    }
}
