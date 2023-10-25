package com.rovianda.preventa.home.presenter;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
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
import com.rovianda.preventa.home.view.HomeView;
import com.rovianda.preventa.home.view.HomeViewContract;
import com.rovianda.preventa.utils.Constants;
import com.rovianda.preventa.utils.GsonRequest;
import com.rovianda.preventa.utils.bd.entities.Client;
import com.rovianda.preventa.utils.models.AddressCoordenatesRequest;
import com.rovianda.preventa.utils.models.AddressCoordenatesResponse;
import com.rovianda.preventa.utils.models.ModeOfflineSM;
import com.rovianda.preventa.utils.models.PreSaleSincronizedRequest;
import com.rovianda.preventa.utils.models.SaleDTO;
import com.rovianda.preventa.utils.models.SincronizationPreSaleResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePresenter implements HomePresenterContract{
    Context context;
    HomeViewContract view;
    FirebaseAuth firebaseAuth;

    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;

    private String url = Constants.URL;
    private RequestQueue requestQueue;
    public HomePresenter(Context context, HomeView view){
        this.context=context;
        this.view=view;
        this.firebaseAuth = FirebaseAuth.getInstance();

        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
    }

    @Override
    public void doLogout() {
        if(this.firebaseAuth!=null) {
            this.firebaseAuth.signOut();
        }
        view.goToLogin();
    }

    @Override
    public void doSale(SaleDTO saleDTO) {

    }

    @Override
    public void getEndDayTicket() {

    }

    @Override
    public void getStockOnline() {

    }

    @Override
    public void checkCommunicationToServer() {

    }

    @Override
    public void getAddressByCoordenates(Double latitude, Double longitude, Client client) {
        AddressCoordenatesRequest addressCoordenatesRequest = new AddressCoordenatesRequest();
        addressCoordenatesRequest.setLatitude(latitude);
        addressCoordenatesRequest.setLongitude(longitude);
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<AddressCoordenatesResponse> addressCoordenates = new GsonRequest<AddressCoordenatesResponse>
                (url+"/rovianda/geocodingaddress", AddressCoordenatesResponse.class,headers,
                        new Response.Listener<AddressCoordenatesResponse>(){
                            @Override
                            public void onResponse(AddressCoordenatesResponse response) {
                                view.setAddressForConfirm(response,latitude,longitude,client);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                                view.onDialogNegativeClick();
                    }
                }   , Request.Method.POST,this.parser.toJson(addressCoordenatesRequest)
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
                view.onDialogNegativeClick();
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

            }
        });
    }

    @Override
    public void sendEndDayRecord(String date, String uid) {

    }
}
