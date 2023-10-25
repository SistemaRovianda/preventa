package com.rovianda.preventa.utils.bd.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_inventory_id")
    public int productInventoryId;

    @ColumnInfo(name = "product_id")
    public int productId;

    @ColumnInfo(name="product_key")
    public String productKey;

    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name = "presentation_name")
    public String presentationName;

    @ColumnInfo(name = "uni_med")
    public String uniMed;

    @ColumnInfo(name="price")
    public Float price;

    @ColumnInfo(name="second_price")
    public Float secondPrice;

    @ColumnInfo(name="quantity")
    public Float quantity;

    @ColumnInfo(name="presentation_id")
    public int presentationId;

    @ColumnInfo(name="weight_original")
    public Float weightOriginal;

    @ColumnInfo(name="seller_id")
    public String sellerId;

    @ColumnInfo(name="esq_key")
    public int esqKey;

    @ColumnInfo(name="esq_description")
    public String esqDescription;
}
