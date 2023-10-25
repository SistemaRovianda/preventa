package com.rovianda.preventa.utils.bd.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sub_sales")
public class SubSale {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sub_sale_id")
    public int subSaleId;

    @ColumnInfo(name="folio")
    public String folio;

    @ColumnInfo(name = "sub_sale_server_id")
    public int subSaleServerId;

    @ColumnInfo(name="product_key")
    public String productKey;

    @ColumnInfo(name="quantity")
    public Float quantity;

    @ColumnInfo(name="price")
    public Float price;

    @ColumnInfo(name="weight_standar")
    public Float weightStandar;

    @ColumnInfo(name="product_name")
    public String productName;

    @ColumnInfo(name="product_presentation_type")
    public String productPresentationType;

    @ColumnInfo(name="presentation_id")
    public int presentationId;

    @ColumnInfo(name="product_id")
    public int productId;

    @ColumnInfo(name="uni_med")
    public String uniMed;
}
