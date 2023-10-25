package com.rovianda.preventa.history.view;

import com.rovianda.preventa.home.DatePickerType;
import com.rovianda.preventa.utils.bd.entities.PreSale;

public interface HistoryViewContract {
    void genericMessage(String title, String msg);
    void goToHome();
    void goToVisits();
    void goToClients();
    void showErrorConnectingPrinter();
    void connectionPrinterSuccess(String printerName);
    void showOptionsSale(PreSale preSale);
    public void onDateSet(String date, DatePickerType datePickerType);
    public void onDateCancel();
}
