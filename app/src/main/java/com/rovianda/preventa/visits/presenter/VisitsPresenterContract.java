package com.rovianda.preventa.visits.presenter;

import com.rovianda.preventa.utils.models.ClientDTO;
import com.rovianda.preventa.utils.models.ClientV2Request;
import com.rovianda.preventa.utils.models.ClientV2UpdateRequest;
import com.rovianda.preventa.utils.models.ClientV2VisitRequest;
import com.rovianda.preventa.utils.models.ModeOfflineSM;
import com.rovianda.preventa.utils.models.ModeOfflineSincronize;

import java.util.List;

public interface VisitsPresenterContract {

    void getDataInitial(String sellerUid,String date);
    void tryRegisterClients(List<ClientV2Request> clientV2Request);
    void updateCustomerV2(List<ClientV2UpdateRequest> clientV2UpdateRequestList);
    void registerVisitsV2(List<ClientV2VisitRequest> clientV2VisitRequests);
    void sincronizePreSales(List<ModeOfflineSM> ModeOfflineSMS);
}
