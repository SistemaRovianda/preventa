package com.rovianda.preventa.utils.bd.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pre_sales")
public class PreSale {

    @PrimaryKey
    @NonNull
    public String folio;

    @ColumnInfo(name="sale_server_id")
    public int saleId;

    @ColumnInfo(name = "seller_id")
    public String sellerId;

    @ColumnInfo(name="key_client")
    public int keyClient;

    @ColumnInfo(name="is_temp_key_client")
    public boolean isTempKeyClient;

    @ColumnInfo(name="amount")
    public Float amount;

    @ColumnInfo(name="client_name")
    public String clientName;

    @ColumnInfo(name="date")
    public String date;

    @ColumnInfo(name="date_to_deliver")
    public String dateToDeliver;

    @ColumnInfo(name="status_str")
    public String statusStr;

    @ColumnInfo(name="sincronized")
    public Boolean sincronized;

    @ColumnInfo(name="client_id")
    public int clientId;

    @ColumnInfo( name="urgent")
    public Boolean urgent;

}
