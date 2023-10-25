package com.rovianda.preventa.utils.bd.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clients")
public class Client {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "client_mobile_id")
    public Integer clientMobileId;

    @ColumnInfo(name = "client_rov_id")
    public Integer clientRovId;

    @ColumnInfo(name="client_key")
    public Integer clientKey;

    @ColumnInfo(name="client_key_temp")
    public Integer clientKeyTemp;

    @ColumnInfo(name="is_temp")
    public Boolean isTemp;

    @ColumnInfo(name="seller_uid")
    public String uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name="type")
    public String type;

    @ColumnInfo(name="current_credit_used")
    public Float currentCreditUsed;

    @ColumnInfo(name="credit_limit")
    public Float creditLimit;

    @ColumnInfo(name="street")
    public String street;
    @ColumnInfo(name="municipality")
    public String municipality;
    @ColumnInfo(name="suburb")
    public String suburb;
    @ColumnInfo(name="no_exterior")
    public String noExterior;
    @ColumnInfo(name="cp")
    public String cp;

    @ColumnInfo(name="monday")
    public Boolean monday;
    @ColumnInfo(name="tuesday")
    public Boolean tuesday;
    @ColumnInfo(name="wednesday")
    public Boolean wednesday;
    @ColumnInfo(name="thursday")
    public Boolean thursday;
    @ColumnInfo(name="friday")
    public Boolean friday;
    @ColumnInfo(name="saturday")
    public Boolean saturday;
    @ColumnInfo(name="sunday")
    public Boolean sunday;

    @ColumnInfo(name="registered_in_mobile")
    public Boolean registeredInMobile;
    @ColumnInfo(name="sincronized")
    public Boolean sincronized;

    @ColumnInfo(name="latitude")
    public Double latitude;
    @ColumnInfo(name="longitude")
    public Double longitude;

    @ColumnInfo(name="estatus",defaultValue = "ACTIVE")
    public String estatus;

    @ColumnInfo(name="reference")
    public String reference;

    @ColumnInfo(name="phone_number")
    public String phoneNumber;

    @ColumnInfo(name="contact")
    public String contact;

}
