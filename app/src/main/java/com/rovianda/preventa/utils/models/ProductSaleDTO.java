package com.rovianda.preventa.utils.models;

public class ProductSaleDTO {
    private String productKey;
    private Float quantity;
    private Float price;
    private String productName;
    private String productPresentation;
    private String typeUnid;
    private Float weightOriginal;// for offline use
    private Integer presentationId;
    private Integer productId;


    public Integer getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(Integer presentationId) {
        this.presentationId = presentationId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Float getWeightOriginal() {
        return weightOriginal;
    }

    public void setWeightOriginal(Float weightOriginal) {
        this.weightOriginal = weightOriginal;
    }

    public String getTypeUnid() {
        return typeUnid;
    }

    public void setTypeUnid(String typeUnid) {
        this.typeUnid = typeUnid;
    }

    public String getProductPresentation() {
        return productPresentation;
    }

    public void setProductPresentation(String productPresentation) {
        this.productPresentation = productPresentation;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
