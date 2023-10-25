package com.rovianda.preventa.utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.rovianda.preventa.utils.bd.entities.ClientVisit;

import java.util.List;

@Dao
public interface ClientVisitDao {

    @Insert
    void insertClientVisit(ClientVisit... clientVisit);

    @Query("select * from clients_visits where client_id=:clientId and date=:date limit 1")
    ClientVisit getClientVisitByIdAndDate(Integer clientId,String date);

    @Query("select * from clients_visits where date=:date")
    List<ClientVisit> getClientVisitByDate(String date);

    @Query("select * from clients_visits where date=:date and sincronized=0")
    List<ClientVisit> getClientVisitByDateUnsincronized(String date);

    @Query("select * from clients_visits where sincronized=0")
    List<ClientVisit> getClientVisitUnsincronized();

    @Update
    void updateClientVisit(ClientVisit... clientVisit);
}
