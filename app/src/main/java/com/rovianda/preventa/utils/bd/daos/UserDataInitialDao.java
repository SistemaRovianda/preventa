package com.rovianda.preventa.utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.rovianda.preventa.utils.bd.entities.UserDataInitial;

import java.util.List;

@Dao
public interface UserDataInitialDao {

    @Query("SELECT * FROM user_date_initial where uid=:uid limit 1")
    UserDataInitial getDetailsInitialByUid(String uid);

    @Query("select * from user_date_initial where email like :email and password like :password")
    UserDataInitial getByEmailAndPasswordOffline(String email,String password);

    @Query("update user_date_initial set last_sincronization=:lastSincronization where uid=:uid")
    void updateLastSincronization(String uid,String lastSincronization);

    @Query("update user_date_initial set count=:count where uid=:uid")
    void updateFolioCount(Integer count,String uid);

    @Query("update user_date_initial set loged_in=0")
    void updateAllLogedInFalse();

    @Query("select * from user_date_initial where loged_in=1")
    List<UserDataInitial> getAnyLogedIn();

    @Query("update user_date_initial set loged_in=1 where uid=:uid")
    void updateAllLogedInTrue(String uid);

    @Insert
    void insertUserDataDetail(UserDataInitial... userDetails);

    @Query("update user_date_initial set printer_mac_address=:printerAddress where uid=:uid")
    void updatePrinterAddress(String uid,String printerAddress);

    @Update
    void updateUserData(UserDataInitial... userDataInitial);
}
