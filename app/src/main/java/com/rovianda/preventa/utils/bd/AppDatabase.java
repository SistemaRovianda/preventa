package com.rovianda.preventa.utils.bd;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rovianda.preventa.utils.bd.daos.ClientDao;
import com.rovianda.preventa.utils.bd.daos.ClientVisitDao;
import com.rovianda.preventa.utils.bd.daos.EndingDayDao;
import com.rovianda.preventa.utils.bd.daos.ProductDao;
import com.rovianda.preventa.utils.bd.daos.PreSaleDao;
import com.rovianda.preventa.utils.bd.daos.SubSaleDao;
import com.rovianda.preventa.utils.bd.daos.UserDataInitialDao;
import com.rovianda.preventa.utils.bd.entities.Client;
import com.rovianda.preventa.utils.bd.entities.ClientVisit;
import com.rovianda.preventa.utils.bd.entities.EndingDay;
import com.rovianda.preventa.utils.bd.entities.PreSale;
import com.rovianda.preventa.utils.bd.entities.Product;
import com.rovianda.preventa.utils.bd.entities.SubSale;
import com.rovianda.preventa.utils.bd.entities.UserDataInitial;

@Database(entities = {PreSale.class, SubSale.class, Client.class, Product.class, UserDataInitial.class,  EndingDay.class, ClientVisit.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public abstract PreSaleDao preSaleDao();
    public abstract SubSaleDao subSalesDao();
    public abstract ClientDao clientDao();
    public abstract ProductDao productDao();
    public abstract UserDataInitialDao userDataInitialDao();
    public abstract EndingDayDao endingDayDao();
    public abstract ClientVisitDao clientVisitDao();

    public static synchronized AppDatabase getInstance(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"rovisapipreventa").fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
