package com.rovianda.preventa.home.view;


import com.rovianda.preventa.home.DatePickerType;
import com.rovianda.preventa.utils.bd.entities.Client;
import com.rovianda.preventa.utils.models.AddressCoordenatesResponse;
import com.rovianda.preventa.utils.models.SincronizationPreSaleResponse;

public interface HomeViewContract {
    void goToLogin();
    void showErrorConnectingPrinter();
    void connectionPrinterSuccess(String printerName);
    void saleSuccess(String ticket);
    void genericMessage(String title,String msg);
    void showNotificationSincronization(String msg);
    void hiddeNotificationSincronizastion();
    void completeSincronzation(SincronizationPreSaleResponse sincronizationPreSaleResponse);
    public void onDialogPositiveClick(Double latitude,Double longitude);
    public void onDialogNegativeClick();
    public void setAddressForConfirm(AddressCoordenatesResponse addressForConfirm, Double latitude, Double longitude, Client client);
    public void onDateSet(String date, DatePickerType datePickerType);
    public void onDateCancel(DatePickerType datePickerType);
}
