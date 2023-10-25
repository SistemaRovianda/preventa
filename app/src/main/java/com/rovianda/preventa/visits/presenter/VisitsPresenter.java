package com.rovianda.preventa.visits.presenter;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.rovianda.preventa.utils.Constants;
import com.rovianda.preventa.utils.GsonRequest;
import com.rovianda.preventa.utils.ModeOfflineNewVersion;
import com.rovianda.preventa.utils.models.ClientDTO;
import com.rovianda.preventa.utils.models.ClientV2Request;
import com.rovianda.preventa.utils.models.ClientV2Response;
import com.rovianda.preventa.utils.models.ClientV2UpdateRequest;
import com.rovianda.preventa.utils.models.ClientV2UpdateResponse;
import com.rovianda.preventa.utils.models.ClientV2VisitRequest;
import com.rovianda.preventa.utils.models.ClientV2VisitResponse;
import com.rovianda.preventa.utils.models.ModeOfflineSM;
import com.rovianda.preventa.utils.models.ModeOfflineSincronize;
import com.rovianda.preventa.utils.models.PreSaleSincronizedRequest;
import com.rovianda.preventa.utils.models.SincronizationPreSaleResponse;
import com.rovianda.preventa.visits.view.VisitsView;
import com.rovianda.preventa.visits.view.VisitsViewContract;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitsPresenter implements VisitsPresenterContract{

    Context context;
    VisitsViewContract view;

    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url = Constants.URL;
    private RequestQueue requestQueue;
    private FirebaseAuth firebaseAuth;
    public VisitsPresenter(Context context, VisitsView view){
        this.context=context;
        this.view=view;
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getDataInitial(String sellerUid, String date) {
        System.out.println("Getting data: "+sellerUid);
        Map<String,String> headers = new HashMap<>();
        GsonRequest<ModeOfflineNewVersion> getOfflineModeData = new GsonRequest<ModeOfflineNewVersion>(
                url + "/rovianda/presale/sincronization/" + sellerUid+"?date="+date, ModeOfflineNewVersion.class, headers, new Response.Listener<ModeOfflineNewVersion>() {
            @Override
            public void onResponse(ModeOfflineNewVersion response) {
                view.modalSincronizationEnd();
                view.setModeOffline(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.setLoadingStatus(false);
                view.modalSincronizationEnd();
                view.modalMessageOperation(error.getMessage());
            }
        }, Request.Method.GET,null
        );
        requestQueue.add(getOfflineModeData).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 180000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                view.setLoadingStatus(false);
                view.modalSincronizationEnd();
                view.modalMessageOperation(error.getMessage());
            }
        });
    }


    @Override
    public void tryRegisterClients(List<ClientV2Request> clientV2Request) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2Response[]> addressCoordenates = new GsonRequest<ClientV2Response[]>
                (url+"/rovianda/customers/v2/register-arr", ClientV2Response[].class,headers,
                        new Response.Listener<ClientV2Response[]>(){
                            @Override
                            public void onResponse(ClientV2Response[] response) {
                                view.setClientsRegisters(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        view.setUploadingStatus(false);
                        view.modalSincronizationEnd();
                        view.modalMessageOperation(error.getMessage());
                        view.checkIfRecordsWithoutSincronization();
                    }
                }   , Request.Method.POST,this.parser.toJson(clientV2Request)
                );
        requestQueue.add(addressCoordenates).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                view.setUploadingStatus(false);
                view.modalSincronizationEnd();
                view.modalMessageOperation(error.getMessage());
            }
        });
    }

    @Override
    public void updateCustomerV2(List<ClientV2UpdateRequest> clientV2UpdateRequestList) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2UpdateResponse[]> addressCoordenates = new GsonRequest<ClientV2UpdateResponse[]>
                (url+"/rovianda/customers/v2/update", ClientV2UpdateResponse[].class,headers,
                        new Response.Listener<ClientV2UpdateResponse[]>(){
                            @Override
                            public void onResponse(ClientV2UpdateResponse[] response) {
                                view.setClientsUpdated(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setUploadingStatus(false);
                        view.modalSincronizationEnd();
                        view.modalMessageOperation("Error al sincronizar los clientes actualizados (paso 2)");
                        view.checkIfRecordsWithoutSincronization();
                    }
                }   , Request.Method.POST,this.parser.toJson(clientV2UpdateRequestList)
                );
        requestQueue.add(addressCoordenates).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                view.setUploadingStatus(false);
                view.modalSincronizationEnd();
                view.modalMessageOperation(error.getMessage());
            }
        });
    }

    @Override
    public void registerVisitsV2(List<ClientV2VisitRequest> clientV2VisitRequests) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2VisitResponse[]> addressCoordenates = new GsonRequest<ClientV2VisitResponse[]>
                (url+"/rovianda/customer/visit", ClientV2VisitResponse[].class,headers,
                        new Response.Listener<ClientV2VisitResponse[]>(){
                            @Override
                            public void onResponse(ClientV2VisitResponse[] response) {
                                view.setClientVisitedRegistered(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setUploadingStatus(false);
                        view.modalSincronizationEnd();
                        view.modalMessageOperation("Error al sincronizar las visitas de clientes (paso 3)");
                        view.checkIfRecordsWithoutSincronization();
                    }
                }   , Request.Method.POST,this.parser.toJson(clientV2VisitRequests)
                );
        requestQueue.add(addressCoordenates).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                view.setUploadingStatus(false);
                view.modalSincronizationEnd();
                view.modalMessageOperation(error.getMessage());
            }
        });
    }


    @Override
    public void sincronizePreSales(List<ModeOfflineSM> ModeOfflineSMS) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        PreSaleSincronizedRequest request = new PreSaleSincronizedRequest();
        request.setPreSales(ModeOfflineSMS);
        GsonRequest<SincronizationPreSaleResponse> presentationsgGet = new GsonRequest<SincronizationPreSaleResponse>
                (url+"/rovianda/sincronize-presales", SincronizationPreSaleResponse.class,headers,
                        new Response.Listener<SincronizationPreSaleResponse>(){
                            @Override
                            public void onResponse(SincronizationPreSaleResponse response) {
                                view.hiddeNotificationSincronizastion();
                                view.completeSincronzation(response);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setUploadingStatus(false);
                        view.modalSincronizationEnd();
                        view.showNotificationSincronization("Error al sincronizar venta ");
                    }
                }   , Request.Method.POST,parser.toJson(request)
                );
        requestQueue.add(presentationsgGet).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 15000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                view.setUploadingStatus(false);
                view.modalSincronizationEnd();
                view.showNotificationSincronization("Error al sincronizar venta ");
            }
        });
    }
}
