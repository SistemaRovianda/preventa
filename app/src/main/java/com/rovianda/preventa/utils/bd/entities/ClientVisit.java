package com.rovianda.preventa.utils.bd.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clients_visits")
public class ClientVisit {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "client_visit_id")
    public Integer clientVisitId;

    @ColumnInfo(name="is_client_id_temp")
    public Boolean isClientIdTemp;

    @ColumnInfo(name="client_id")
    public Integer clientId;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name="observations")
    public String observations;

    @ColumnInfo(name="visited")
    public Boolean visited;

    @ColumnInfo(name="amount")
    public Float amount;

    @ColumnInfo(name="sincronized")
    public Boolean sincronized;
}
