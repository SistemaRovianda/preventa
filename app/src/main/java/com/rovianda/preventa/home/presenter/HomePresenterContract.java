package com.rovianda.preventa.home.presenter;


import com.rovianda.preventa.utils.bd.entities.Client;
import com.rovianda.preventa.utils.models.ModeOfflineSM;
import com.rovianda.preventa.utils.models.SaleDTO;

import java.util.List;

public interface HomePresenterContract {
    void doLogout();
    void doSale(SaleDTO saleDTO);
    void getEndDayTicket();
    void getStockOnline();
    void checkCommunicationToServer();
    void sincronizePreSales(List<ModeOfflineSM> ModeOfflineSMS);
    void sendEndDayRecord(String date,String uid);
    void getAddressByCoordenates(Double latitude, Double longitude, Client client);
}
