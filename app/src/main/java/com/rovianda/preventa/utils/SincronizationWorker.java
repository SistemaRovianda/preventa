package com.rovianda.preventa.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
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
import com.rovianda.preventa.R;
import com.rovianda.preventa.utils.bd.AppDatabase;
import com.rovianda.preventa.utils.bd.entities.Client;
import com.rovianda.preventa.utils.bd.entities.ClientVisit;
import com.rovianda.preventa.utils.bd.entities.PreSale;
import com.rovianda.preventa.utils.bd.entities.SubSale;
import com.rovianda.preventa.utils.models.ClientV2Request;
import com.rovianda.preventa.utils.models.ClientV2Response;
import com.rovianda.preventa.utils.models.ClientV2UpdateRequest;
import com.rovianda.preventa.utils.models.ClientV2UpdateResponse;
import com.rovianda.preventa.utils.models.ClientV2VisitRequest;
import com.rovianda.preventa.utils.models.ClientV2VisitResponse;
import com.rovianda.preventa.utils.models.ModeOfflineSM;
import com.rovianda.preventa.utils.models.ModeOfflineSMP;
import com.rovianda.preventa.utils.models.PreSaleSincronizedRequest;
import com.rovianda.preventa.utils.models.SincronizationPreSaleResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SincronizationWorker extends Worker {
    Context context;
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private RequestQueue requestQueue;
    NotificationManagerCompat notificationManager;
    private static final String url = Constants.URL;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    private String dateSincronization=null;
    public SincronizationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
    }

    @NonNull
    @Override
    public Result doWork() {
        showNotificationSynchronization("Sincronización automática");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateSincronization = dateFormat.format(calendar.getTime());
        firstStep();
        return Result.success();
    }

    void firstStep(){

        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                List<Client> clientsUnsincronized =conexion.clientDao().getAllClientsUnsicronizedTemp();
                List<ClientV2Request> requestRegister = new ArrayList<>();
                for(Client client : clientsUnsincronized) {
                    if (client.isTemp) {
                        ClientV2Request clientV2Request = new ClientV2Request();
                        clientV2Request.setClientCp(client.cp);
                        clientV2Request.setClientName(client.name);
                        clientV2Request.setClientMobileId(client.clientMobileId);
                        clientV2Request.setClientType(client.type);
                        clientV2Request.setClientStreet(client.street);
                        clientV2Request.setClientSuburb(client.suburb);
                        clientV2Request.setClientMunicipality(client.municipality);
                        clientV2Request.setClientExtNumber(client.noExterior);
                        clientV2Request.setClientSellerUid(client.uid);
                        clientV2Request.setClientContact(client.contact);
                        clientV2Request.setClientPhoneNumber(client.phoneNumber);
                        clientV2Request.setClientReference(client.reference);
                        clientV2Request.setTemp(client.isTemp);
                        if (client.latitude != null) {
                            clientV2Request.setLatitude(client.latitude);
                        }
                        if (client.longitude != null) {
                            clientV2Request.setLongitude(client.longitude);
                        }
                        clientV2Request.setMonday(client.monday);
                        clientV2Request.setTuesday(client.tuesday);
                        clientV2Request.setWednesday(client.wednesday);
                        clientV2Request.setThursday(client.thursday);
                        clientV2Request.setFriday(client.friday);
                        clientV2Request.setSaturday(client.saturday);
                        requestRegister.add(clientV2Request);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(requestRegister.size()>0) {
                            tryRegisterClients(requestRegister);
                        }else{
                            setClientsRegisters(new ArrayList<>());
                        }
                    }
                });
            }
        });
    }

    public void tryRegisterClients(List<ClientV2Request> clientV2Request) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2Response[]> addressCoordenates = new GsonRequest<ClientV2Response[]>
                (url+"/rovianda/customers/v2/register-arr", ClientV2Response[].class,headers,
                        new Response.Listener<ClientV2Response[]>(){
                            @Override
                            public void onResponse(ClientV2Response[] response) {
                                setClientsRegisters(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showNotificationSynchronization("Sincronizacion automatica - Error al sincronizar los clientes nuevos (paso 1)");
                        tryToUploadSales();
                    }
                }   , Request.Method.POST,this.parser.toJson(clientV2Request)
                );
        requestQueue.add(addressCoordenates).setRetryPolicy(new RetryPolicy() {
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

            }
        });
    }
    /** updating clients registered to database*/
    public void setClientsRegisters(List<ClientV2Response> clientsRegistered) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                for(ClientV2Response clientReg : clientsRegistered){
                    Client client = conexion.clientDao().getClientByClientIdMobile(clientReg.getClientMobileId());
                    client.clientRovId=clientReg.getClientId();
                    client.clientKey=clientReg.getClientId();
                    client.clientKeyTemp=clientReg.getClientId();
                    client.sincronized=true;
                    client.isTemp=false;
                    conexion.clientDao().updateClient(client);
                    ClientVisit clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(clientReg.getClientMobileId(),dateSincronization);
                    if(clientVisit!=null) {
                        clientVisit.clientId = clientReg.getClientId();
                        conexion.clientVisitDao().updateClientVisit(clientVisit);
                    }
                    List<PreSale> preSalesTemp = conexion.preSaleDao().getAllSalesByDateAndClientId(dateSincronization+"T00:00:00.000Z",dateSincronization+"T23:59:59.000Z",clientReg.getClientMobileId());
                    for(PreSale sale : preSalesTemp){
                        sale.isTempKeyClient=false;
                        sale.keyClient=client.clientRovId;
                        sale.clientId=client.clientRovId;
                        conexion.preSaleDao().updateSale(sale);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        secondStep();
                    }
                });
            }
        });
    }

    /** updating clients unsincronized */
    void secondStep(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                List<Client> clientsUnsincronized =conexion.clientDao().getAllClientsUnsicronizedNotTemp();
                List<ClientV2UpdateRequest> clientV2UpdateRequestList = new ArrayList<>();
                for(Client client : clientsUnsincronized) {
                    if (client.sincronized==false && client.clientRovId!=null) {
                        ClientV2UpdateRequest clientV2UpdateRequest = new ClientV2UpdateRequest();
                        clientV2UpdateRequest.setClientId(client.clientRovId);
                        clientV2UpdateRequest.setClientKey(client.clientKey);
                        clientV2UpdateRequest.setClientCp(client.cp);
                        clientV2UpdateRequest.setClientName(client.name);
                        clientV2UpdateRequest.setClientStreet(client.street);
                        clientV2UpdateRequest.setClientSuburb(client.suburb);
                        clientV2UpdateRequest.setClientMunicipality(client.municipality);
                        clientV2UpdateRequest.setClientContact(client.contact);
                        clientV2UpdateRequest.setClientPhoneNumber(client.phoneNumber);
                        clientV2UpdateRequest.setClientReference(client.reference);
                        if (client.noExterior != null) {
                            clientV2UpdateRequest.setClientExtNumber(client.noExterior);
                        }
                        clientV2UpdateRequest.setMonday(client.monday);
                        clientV2UpdateRequest.setTuesday(client.tuesday);
                        clientV2UpdateRequest.setWednesday(client.wednesday);
                        clientV2UpdateRequest.setThursday(client.thursday);
                        clientV2UpdateRequest.setFriday(client.friday);
                        clientV2UpdateRequest.setSaturday(client.saturday);
                        if (client.latitude != null) {
                            clientV2UpdateRequest.setLatitude(client.latitude);
                        }
                        if (client.longitude != null) {
                            clientV2UpdateRequest.setLongitude(client.longitude);
                        }
                        clientV2UpdateRequestList.add(clientV2UpdateRequest);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(clientV2UpdateRequestList.size()>0) {
                            updateCustomerV2(clientV2UpdateRequestList);
                        }else{
                            setClientsUpdated(new ArrayList<>());
                        }
                    }
                });
            }
        });
    }


    public void updateCustomerV2(List<ClientV2UpdateRequest> clientV2UpdateRequestList) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2UpdateResponse[]> addressCoordenates = new GsonRequest<ClientV2UpdateResponse[]>
                (url+"/rovianda/customers/v2/update", ClientV2UpdateResponse[].class,headers,
                        new Response.Listener<ClientV2UpdateResponse[]>(){
                            @Override
                            public void onResponse(ClientV2UpdateResponse[] response) {
                                setClientsUpdated(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        showNotificationSynchronization("Sincronizacion automatica - Error al sincronizar los clientes actualizados (pase 2)");
                        tryToUploadSales();
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

            }
        });
    }

    /** Updating clients updated to database*/
    public void setClientsUpdated(List<ClientV2UpdateResponse> clientsUpdated) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                for(ClientV2UpdateResponse clientReg : clientsUpdated){
                    Client client = conexion.clientDao().getClientBydId(clientReg.getClientId());
                    if(client!=null) {
                        client.sincronized = true;
                        conexion.clientDao().updateClient(client);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        thirdStep();
                    }
                });
            }
        });
    }

    /** Registering clients visits to server */
    void thirdStep(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                List<ClientVisit> visits = conexion.clientVisitDao().getClientVisitByDateUnsincronized(dateSincronization);
                List<ClientV2VisitRequest> requests = new ArrayList<>();
                for(ClientVisit clientVisit : visits){
                    ClientV2VisitRequest request = new ClientV2VisitRequest();
                    request.setVisited(clientVisit.visited);
                    request.setDate(clientVisit.date);
                    request.setAmount(clientVisit.amount);
                    request.setObservations(clientVisit.observations);
                    request.setClientId(clientVisit.clientId);
                    requests.add(request);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(requests.size()>0) {
                            registerVisitsV2(requests);
                        }else{
                            setClientVisitedRegistered(new ArrayList());
                        }
                    }
                });
            }
        });
    }
    public void registerVisitsV2(List<ClientV2VisitRequest> clientV2VisitRequests) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2VisitResponse[]> addressCoordenates = new GsonRequest<ClientV2VisitResponse[]>
                (url+"/rovianda/customer/visit", ClientV2VisitResponse[].class,headers,
                        new Response.Listener<ClientV2VisitResponse[]>(){
                            @Override
                            public void onResponse(ClientV2VisitResponse[] response) {
                                setClientVisitedRegistered(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showNotificationSynchronization("Sincronizacion Automatica - Error al sincronizar las visitas de clientes (paso 3)");
                        tryToUploadSales();
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

            }
        });
    }
    /** Updating clients visits in database */
    public void setClientVisitedRegistered(List<ClientV2VisitResponse> clientV2Visit) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                for(ClientV2VisitResponse response : clientV2Visit){
                    ClientVisit clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(response.getClientId(),response.getDate());
                    clientVisit.sincronized=true;
                    conexion.clientVisitDao().updateClientVisit(clientVisit);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tryToUploadSales();
                    }
                });
            }
        });
    }

    private void showNotificationSynchronization(String msg){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "rovisapi")

                .setSmallIcon(R.drawable.ic_logorov)
                .setContentTitle("Sistema Rovianda")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    void tryToUploadSales() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getApplicationContext());
                List<PreSale> sales = conexion.preSaleDao().getAllPreSalesUnsincronized();
                List<ModeOfflineSM> modeOfflineSMS = new ArrayList<>();

                for (PreSale preSale : sales) {
                    if (!preSale.isTempKeyClient) {
                        System.out.println("Sale without sincronization: " + preSale.folio);
                        ModeOfflineSM modeOfflineSM = new ModeOfflineSM();
                        modeOfflineSM.setAmount(preSale.amount);
                        modeOfflineSM.setClientId(preSale.clientId);
                        modeOfflineSM.setDate(preSale.date);
                        modeOfflineSM.setDateToDeliver(preSale.dateToDeliver);
                        modeOfflineSM.setFolio(preSale.folio);
                        modeOfflineSM.setSellerId(preSale.sellerId);
                        List<SubSale> subSales = conexion.subSalesDao().getSubSalesBySale(preSale.folio);
                        List<ModeOfflineSMP> modeOfflineSMPS = new ArrayList<>();
                        for (SubSale subSale : subSales) {
                            ModeOfflineSMP modeOfflineSMP = new ModeOfflineSMP();
                            modeOfflineSMP.setPresentationId(subSale.presentationId);
                            modeOfflineSMP.setProductId(subSale.productId);
                            modeOfflineSMP.setQuantity(subSale.quantity);
                            modeOfflineSMP.setAmount(subSale.price);
                            modeOfflineSMP.setAppSubSaleId(subSale.subSaleId);
                            modeOfflineSMPS.add(modeOfflineSMP);
                        }
                        modeOfflineSM.setProducts(modeOfflineSMPS);
                        modeOfflineSMS.add(modeOfflineSM);
                    }

                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (modeOfflineSMS.size() > 0) {
                            sincronizePreSales(modeOfflineSMS);
                        } else {
                            notificationManager.cancel(1);
                        }
                    }

                });
            }
        });
    }

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
                                completeSincronzation(response);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

    public void completeSincronzation(SincronizationPreSaleResponse sincronizationResponse) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getApplicationContext());
                for(int i=0;i<sincronizationResponse.getPreSalesSincronized().size();i++){
                    String folio = sincronizationResponse.getPreSalesSincronized().get(i).getFolio();
                    Integer preSaleId = sincronizationResponse.getPreSalesSincronized().get(i).getPreSaleId();
                    conexion.preSaleDao().updatePreSaleIdByFolio(preSaleId,folio);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        notificationManager.cancel(1);
                    }
                });
            }
        });
    }

}
