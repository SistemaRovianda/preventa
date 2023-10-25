package com.rovianda.preventa.utils.bd.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ending_days")
public class EndingDay {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ending_day_id")
    public int endingDayId;

    @ColumnInfo(name = "date")
    public String date;

}
