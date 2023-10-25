package com.rovianda.preventa.clients.presenter;

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
import com.google.gson.Gson;
import com.rovianda.preventa.clients.view.ClientGeneralDataView;
import com.rovianda.preventa.clients.view.ClientGeneralDataViewContract;
import com.rovianda.preventa.utils.Constants;
import com.rovianda.preventa.utils.GsonRequest;
import com.rovianda.preventa.utils.models.AddressCoordenatesRequest;
import com.rovianda.preventa.utils.models.AddressCoordenatesResponse;
import com.rovianda.preventa.utils.models.ClientV2Request;
import com.rovianda.preventa.utils.models.ClientV2Response;
import com.rovianda.preventa.utils.models.ClientV2UpdateRequest;
import com.rovianda.preventa.utils.models.ClientV2UpdateResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientGeneralDataPresenter implements ClientGeneralDataPresenterContract{

    private final String url = Constants.URL;
    private ClientGeneralDataViewContract view;
    private Context context;
    private Cache cache;
    private Network network;
    private Gson parser;
    private RequestQueue requestQueue;
    public ClientGeneralDataPresenter(Context context, ClientGeneralDataView view){
        this.context = context;
        this.view = view;
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
    }

    @Override
    public void getAddressByCoordenates(Double latitude,Double longitude) {
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
                                view.closeModalLoadingCoords();
                                view.setAddressByCoordenates(response,latitude,longitude);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.closeModalLoadingCoords();
                        view.cannotTakeCoordenatesInfo();
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
                view.closeModalLoadingCoords();
                view.showModalCoordenatesCustom("Error de red",error.getMessage(),false);
            }
        });
    }

    @Override
    public void tryRegisterClient(ClientV2Request clientV2Request) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2Response> addressCoordenates = new GsonRequest<ClientV2Response>
                (url+"/rovianda/customers/v2/register", ClientV2Response.class,headers,
                        new Response.Listener<ClientV2Response>(){
                            @Override
                            public void onResponse(ClientV2Response response) {
                                view.closeModal();
                                view.updateClientRegisteredInServer(response);
                                view.showModalCoordenatesCustom("Registro en red","Registro en red exitoso",true);
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.closeModal();
                        view.showModalCoordenatesCustom("Registro completado con detalles","Se registro y guardo el cliente en telefono, pero no se envio a servidor.",true);
                    }
                }   , Request.Method.POST,this.parser.toJson(clientV2Request)
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
                view.closeModal();
                view.showModalCoordenatesCustom("Error de red",error.getMessage(),false);
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
                                view.closeModal();
                                view.updateClientInServer(Arrays.asList(response));
                                view.showModalCoordenatesCustom("Actualizado en red","Registro actualizado en red",true);
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.closeModal();
                        view.failConnectionServiceEdit();
                        view.showModalCoordenatesCustom("Registro completado con detalles","Se registro y guardo el cliente en telefono, pero no se envio a servidor.",true);
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
                view.closeModal();
                view.failConnectionServiceEdit();
                view.showModalCoordenatesCustom("Error de red",error.getMessage(),false);
            }
        });
    }
}
