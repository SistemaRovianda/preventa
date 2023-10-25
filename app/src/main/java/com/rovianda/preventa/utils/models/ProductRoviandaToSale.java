package com.rovianda.preventa.utils.models;

public class ProductRoviandaToSale {

    private String presentationType;
    private Float price;
    private String keySae;
    private Integer presentationId;
    private Integer productId;
    private Boolean isPz;
    private Float weight;
    private String nameProduct;
    private Float quantity;
    private String observations;
    private Float weightOriginal;
    private Boolean outOfStock;

    public Boolean getOutOfStock() {
        return outOfStock;
    }

    public void setOutOfStock(Boolean outOfStock) {
        this.outOfStock = outOfStock;
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

    public Boolean getPz() {
        return isPz;
    }

    public void setPz(Boolean pz) {
        isPz = pz;
    }


    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getPresentationType() {
        return presentationType;
    }

    public void setPresentationType(String presentationType) {
        this.presentationType = presentationType;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getKeySae() {
        return keySae;
    }

    public void setKeySae(String keySae) {
        this.keySae = keySae;
    }

    public Integer getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(Integer presentationId) {
        this.presentationId = presentationId;
    }

    public Boolean isIsPz() {
        return isPz;
    }

    public void setIsPz(Boolean isPz) {
        this.isPz = isPz;
    }
}
