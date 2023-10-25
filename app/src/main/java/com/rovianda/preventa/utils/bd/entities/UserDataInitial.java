package com.rovianda.preventa.utils.bd.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_date_initial")
public class UserDataInitial {

    @PrimaryKey(autoGenerate = true)
    public int userDataInitialId;

    @ColumnInfo(name = "uid")
    public String uid;

    @ColumnInfo(name="count")
    public int count;

    @ColumnInfo(name="nomenclature")
    public String nomenclature;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name="last_sincronization")
    public String lastSincronization;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name="password")
    public String password;

    @ColumnInfo(name="loged_in")
    public Boolean logedIn;

    @ColumnInfo(name="printer_mac_address")
    public String printerMacAddress;

}
