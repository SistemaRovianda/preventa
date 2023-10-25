package com.rovianda.preventa.utils.models;

import java.io.Serializable;

public class ClientDTO implements Serializable {

    private int id;
    private int idAspel;
    private String typeClient;
    private int keyClient;
    private String name;
    private String phone;
    private Float credit;
    private String cfdi;
    private String paymentSat;
    private Float currentCredit;
    private int daysCredit;
    private String dayCharge;
    private String rfc;
    private String curp;
    private String clasification;
    private boolean hasDebts;
    private AddressClient address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAspel() {
        return idAspel;
    }

    public void setIdAspel(int idAspel) {
        this.idAspel = idAspel;
    }

    public String getTypeClient() {
        return typeClient;
    }

    public void setTypeClient(String typeClient) {
        this.typeClient = typeClient;
    }

    public int getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(int keyClient) {
        this.keyClient = keyClient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Float getCredit() {
        return credit;
    }

    public void setCredit(Float credit) {
        this.credit = credit;
    }

    public String getCfdi() {
        return cfdi;
    }

    public void setCfdi(String cfdi) {
        this.cfdi = cfdi;
    }

    public String getPaymentSat() {
        return paymentSat;
    }

    public void setPaymentSat(String paymentSat) {
        this.paymentSat = paymentSat;
    }

    public Float getCurrentCredit() {
        return currentCredit;
    }

    public void setCurrentCredit(Float currentCredit) {
        this.currentCredit = currentCredit;
    }

    public int getDaysCredit() {
        return daysCredit;
    }

    public void setDaysCredit(int daysCredit) {
        this.daysCredit = daysCredit;
    }

    public String getDayCharge() {
        return dayCharge;
    }

    public void setDayCharge(String dayCharge) {
        this.dayCharge = dayCharge;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getClasification() {
        return clasification;
    }

    public void setClasification(String clasification) {
        this.clasification = clasification;
    }

    public boolean isHasDebts() {
        return hasDebts;
    }

    public void setHasDebts(boolean hasDebts) {
        this.hasDebts = hasDebts;
    }

    public AddressClient getAddress() {
        return address;
    }

    public void setAddress(AddressClient address) {
        this.address = address;
    }
}
