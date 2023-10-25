package com.rovianda.preventa.utils.models;

public class AddressClient {

    public AddressClient(){
        this.intNumber=null;
    }
    private int id;
    private String street;
    private Integer extNumber;
    private Integer intNumber;
    private String intersectionOne;
    private String intersectionTwo;
    private String suburb;
    private String location;
    private String reference;
    private String population;
    private Integer cp;
    private String state;
    private String municipality;
    private String nationality;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setExtNumber(Integer extNumber) {
        this.extNumber = extNumber;
    }

    public void setIntNumber(Integer intNumber) {
        this.intNumber = intNumber;
    }

    public void setIntersectionOne(String intersectionOne) {
        this.intersectionOne = intersectionOne;
    }

    public void setIntersectionTwo(String intersectionTwo) {
        this.intersectionTwo = intersectionTwo;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public void setCp(Integer cp) {
        this.cp = cp;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getStreet() {
        return street;
    }

    public Integer getExtNumber() {
        return extNumber;
    }

    public Integer getIntNumber() {
        return intNumber;
    }

    public String getIntersectionOne() {
        return intersectionOne;
    }

    public String getIntersectionTwo() {
        return intersectionTwo;
    }

    public String getSuburb() {
        return suburb;
    }

    public String getLocation() {
        return location;
    }

    public String getReference() {
        return reference;
    }

    public String getPopulation() {
        return population;
    }

    public Integer getCp() {
        return cp;
    }

    public String getState() {
        return state;
    }

    public String getMunicipality() {
        return municipality;
    }
}
