package com.rovianda.preventa.utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rovianda.preventa.utils.bd.entities.EndingDay;

@Dao
public interface EndingDayDao {
    @Query("select * from ending_days where date=:date")
    EndingDay getEndingDayByDate(String date);

    @Insert
    void saveEndingDay(EndingDay... endingDay);
}
