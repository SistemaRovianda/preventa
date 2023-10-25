package com.rovianda.preventa.utils;

public class ProductToSaveEntity {

    private Integer productId;
    private String productKey;
    private String name;
    private String presentationName;
    private String uniMed;
    private Float price;
    private Float secondPrice;
    private Float quantity;
    private Integer presentationId;
    private Float weightOriginal;
    private int esqKey;
    private String esqDescription;

    public Float getSecondPrice() {
        return secondPrice;
    }

    public void setSecondPrice(Float secondPrice) {
        this.secondPrice = secondPrice;
    }

    public int getEsqKey() {
        return esqKey;
    }

    public void setEsqKey(int esqKey) {
        this.esqKey = esqKey;
    }

    public String getEsqDescription() {
        return esqDescription;
    }

    public void setEsqDescription(String esqDescription) {
        this.esqDescription = esqDescription;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPresentationName() {
        return presentationName;
    }

    public void setPresentationName(String presentationName) {
        this.presentationName = presentationName;
    }

    public String getUniMed() {
        return uniMed;
    }

    public void setUniMed(String uniMed) {
        this.uniMed = uniMed;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Integer getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(Integer presentationId) {
        this.presentationId = presentationId;
    }

    public Float getWeightOriginal() {
        return weightOriginal;
    }

    public void setWeightOriginal(Float weightOriginal) {
        this.weightOriginal = weightOriginal;
    }
}
