package com.rovianda.preventa.utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rovianda.preventa.utils.bd.entities.Product;

@Dao
public interface ProductDao {

    @Query("select * from products where product_key like :productKey and seller_id=:sellerId limit 1")
    Product getProductByProductKeyAndSellerId(String productKey, String sellerId);

    @Query("select * from products where product_key like :productKey limit 1")
    Product getProductByKey(String productKey);


    @Query("update products set quantity=:quantity where product_key = :productKey")
    void updateQuantityByProduct(String productKey,Float quantity);

    @Insert
    void insertProduct(Product... product);

    @Update
    void updateProduct(Product... product);

}
