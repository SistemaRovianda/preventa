package com.rovianda.preventa.utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rovianda.preventa.utils.bd.entities.SubSale;

import java.util.List;


@Dao
public interface SubSaleDao {

    @Query("select * from sub_sales where folio= :folio")
    List<SubSale> getSubSalesBySale(String folio);

    @Query("delete from sub_sales")
    void deleteAllSubSales();
    @Insert
    void insertAllSubSales(SubSale... subSales);

    @Query("select * from sub_sales where sub_sale_id=:subSaleid")
    SubSale getSubSaleBySubSaleId(Integer subSaleid);

}
