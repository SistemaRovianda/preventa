package com.rovianda.preventa.visits.view;

import com.rovianda.preventa.home.DatePickerType;
import com.rovianda.preventa.utils.ModeOfflineNewVersion;
import com.rovianda.preventa.utils.models.ClientV2Response;
import com.rovianda.preventa.utils.models.ClientV2UpdateResponse;
import com.rovianda.preventa.utils.models.ClientV2VisitResponse;
import com.rovianda.preventa.utils.models.SincronizationPreSaleResponse;

import java.util.List;

public interface VisitsViewContract {

    void goToHome();
    void goToSalesHistory();
    void goToClients();
    void setLoadingStatus(boolean loadingStatus);
    void setModeOffline(ModeOfflineNewVersion records);
    void modalMessageOperation(String msg);
    void modalSincronizationStart(String msg);
    void modalSincronizationEnd();
    void setUploadingStatus(boolean flag);
    void firstStep();
    void setClientsRegisters(List<ClientV2Response> clientsRegistered);
    void secondStep();
    void setClientsUpdated(List<ClientV2UpdateResponse> clientsUpdated);
    void thirdStep();
    void setClientVisitedRegistered(List<ClientV2VisitResponse> clientV2Visit);
    void sendPreSalesToSystem();
    void showNotificationSincronization(String msg);
    void hiddeNotificationSincronizastion();
    void completeSincronzation(SincronizationPreSaleResponse sincronizationResponse);
    void checkIfRecordsWithoutSincronization();
    public void onDateSet(String date, DatePickerType datePickerType);
    public void onDateCancel();
}
