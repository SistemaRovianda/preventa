package com.rovianda.preventa.visits.view;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.rovianda.preventa.R;
import com.rovianda.preventa.home.DatePickerType;
import com.rovianda.preventa.utils.ClientToSaveEntity;
import com.rovianda.preventa.utils.DatePickerFragment;
import com.rovianda.preventa.utils.ModeOfflineNewVersion;
import com.rovianda.preventa.utils.PreSaleRecords;
import com.rovianda.preventa.utils.ProductToSaveEntity;
import com.rovianda.preventa.utils.SubSaleOfflineNewVersion;
import com.rovianda.preventa.utils.ViewModelStore;
import com.rovianda.preventa.utils.bd.AppDatabase;
import com.rovianda.preventa.utils.bd.entities.Client;
import com.rovianda.preventa.utils.bd.entities.ClientVisit;
import com.rovianda.preventa.utils.bd.entities.PreSale;
import com.rovianda.preventa.utils.bd.entities.Product;
import com.rovianda.preventa.utils.bd.entities.SubSale;
import com.rovianda.preventa.utils.bd.entities.UserDataInitial;
import com.rovianda.preventa.utils.models.ClientV2Request;
import com.rovianda.preventa.utils.models.ClientV2Response;
import com.rovianda.preventa.utils.models.ClientV2UpdateRequest;
import com.rovianda.preventa.utils.models.ClientV2UpdateResponse;
import com.rovianda.preventa.utils.models.ClientV2VisitRequest;
import com.rovianda.preventa.utils.models.ClientV2VisitResponse;
import com.rovianda.preventa.utils.models.ModeOfflineSM;
import com.rovianda.preventa.utils.models.ModeOfflineSMP;
import com.rovianda.preventa.utils.models.SincronizationPreSaleResponse;
import com.rovianda.preventa.visits.presenter.VisitsPresenter;
import com.rovianda.preventa.visits.presenter.VisitsPresenterContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VisitsView extends Fragment implements  VisitsViewContract,View.OnClickListener{

    private ViewModelStore viewModelStore;
    private TextView userNameTextView;
    private ListView simpleList;
    private NavController navController;
    private Button logoutButton,goToVisitMap,endDayButton;
    private MaterialButton searchClientButton;
    private ImageView printerButton;
    private ImageButton downloadChangesButton,uploadChangesButton,resincronizeButton;
    private LinearLayout downloadLayout,uploadLayout,resincronizeLayout;
    private CircularProgressIndicator circularProgressIndicator;
    private TextInputLayout inputSearch;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    private boolean logoutPicked=false,isLoading=false;
    private VisitsPresenterContract presenter;
    private BottomNavigationView bottomMenu;
    private String currentHint="";
    private boolean actionForSincronization=true;
    private String dateSincronization=null;
    private Integer actionForDatePicker;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.visits_layout,null);
        userNameTextView = view.findViewById(R.id.userName);
        this.simpleList = (ListView) view.findViewById(R.id.listClientsVisits);
        this.navController = NavHostFragment.findNavController(this);
        this.logoutButton = view.findViewById(R.id.Logout_button);
        this.logoutButton.setVisibility(View.GONE);
        this.presenter = new VisitsPresenter(getContext(),this);
        this.printerButton = view.findViewById(R.id.printerButton);
        this.printerButton.setOnClickListener(this);
        this.circularProgressIndicator = view.findViewById(R.id.loginLoadingSpinner);
        this.downloadChangesButton=view.findViewById(R.id.downloadChanges);
        this.downloadChangesButton.setOnClickListener(this);
        this.uploadChangesButton= view.findViewById(R.id.uploadChanges);
        this.uploadChangesButton.setOnClickListener(this);
        this.resincronizeButton= view.findViewById(R.id.resincronize);
        this.resincronizeButton.setOnClickListener(this);
        this.downloadLayout=view.findViewById(R.id.linearLayoutDownload);
        this.downloadLayout.setVisibility(View.VISIBLE);
        this.uploadLayout=view.findViewById(R.id.linearLayoutUpload);
        this.uploadLayout.setVisibility(View.VISIBLE);
        this.goToVisitMap = view.findViewById(R.id.goToVisitMap);
        this.goToVisitMap.setOnClickListener(this);
        this.resincronizeLayout=view.findViewById(R.id.linearLayoutResincronize);
        this.resincronizeLayout.setVisibility(View.VISIBLE);
        this.presenter = new VisitsPresenter(getContext(),this);
        this.endDayButton=view.findViewById(R.id.end_day_button);
        this.endDayButton.setVisibility(View.GONE);

        this.searchClientButton = view.findViewById(R.id.buscarClienteButton);
        this.searchClientButton.setOnClickListener(this);
        this.inputSearch = view.findViewById(R.id.cliente_input_search);
        this.bottomMenu = view.findViewById(R.id.bottom_navigation_visits);
        this.bottomMenu.setSelectedItemId(R.id.visitas_section);
        this.bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_section:
                        goToHome();
                        break;
                    case R.id.visitas_section:
                        // PANTALLA ACTUAL
                        break;
                    case R.id.cliente_section:
                        goToClients();
                        break;
                    case R.id.history_section:
                        goToSalesHistory();
                        break;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        this.userNameTextView.setText("Usuario: "+ this.viewModelStore.getStore().getUsername());
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        setClientsVisits(day,"",false);
        checkIfRecordsWithoutSincronization();
    }

    public void setClientsVisits(int day,String hint,boolean filtered){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                List<Client> clientsToVisit = new ArrayList<>();
                List<Client> clientsFiltered = new ArrayList<>();
                String uid = viewModelStore.getStore().getSellerId();
                switch (day){
                    case Calendar.MONDAY:
                        clientsToVisit = appDatabase.clientDao().getClientsMonday(uid);
                        break;
                    case Calendar.TUESDAY:
                        clientsToVisit = appDatabase.clientDao().getClientsTuesday(uid);
                        break;
                    case Calendar.WEDNESDAY:
                        clientsToVisit = appDatabase.clientDao().getClientsWednesday(uid);
                        break;
                    case Calendar.THURSDAY:
                        clientsToVisit = appDatabase.clientDao().getClientsThursday(uid);
                        break;
                    case Calendar.FRIDAY:
                        clientsToVisit = appDatabase.clientDao().getClientsFriday(uid);
                        break;
                    case Calendar.SATURDAY:
                        clientsToVisit = appDatabase.clientDao().getClientsSaturday(uid);
                        break;
                }
                if(filtered){
                    for(Client client : clientsToVisit){
                        if(client.name.contains(hint) || String.valueOf(client.clientKey).contains(hint)) {
                            clientsFiltered.add(client);
                        }
                    }
                }else{
                    clientsFiltered=clientsToVisit;
                }
                List<Client> finalClientsFiltered = clientsFiltered;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(finalClientsFiltered.size()>0){
                            List<String> clients = new ArrayList<>();
                            for(Client client : finalClientsFiltered){
                                clients.add(client.clientKey+" "+client.name);
                            }
                            ArrayAdapter<String> customAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,clients){
                                @NonNull
                                @Override
                                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    View view = super.getView(position,convertView,parent);
                                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                                    tv.setTextColor(Color.WHITE);
                                    return view;
                                }
                            };
                            simpleList.setAdapter(customAdapter);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.downloadChanges:
                if(!isLoading){
                    actionForSincronization=true;
                    String currentDateStr = getCurrentDateStr();
                    setLoadingStatus(true);
                    presenter.getDataInitial(viewModelStore.getStore().getSellerId(),currentDateStr);
                }
                break;
            case R.id.uploadChanges:
                  dateSincronization = getCurrentDateStr();
                  firstStep();
                break;
            case R.id.resincronize:

                resinconizeMethod();

                break;
            case R.id.buscarClienteButton:
                search();
                break;
            case R.id.goToVisitMap:
                    goMap();
                break;
        }
    }

    AlertDialog dialogResincronize=null;
    void resinconizeMethod(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.resincronize_modal,null);
        builder.setView(view);
        ImageView reDownload = view.findViewById(R.id.reDownload);
        ImageView reUpload = view.findViewById(R.id.reUpload);
        reDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResincronize.dismiss();
                actionForDatePicker=1;
                showDatePicker();
            }
        });
        reUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResincronize.dismiss();
                actionForDatePicker=2;
                showDatePicker();
            }
        });
        Button cancelResincronization= view.findViewById(R.id.cancelResincronization);
        builder.setPositiveButton(null,null);
        builder.setNegativeButton(null,null);
        dialogResincronize=builder.create();
        cancelResincronization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResincronize.dismiss();
            }
        });

        dialogResincronize.show();
    }

    void showDatePicker(){
        System.out.println("Reinicializando");
        DatePickerFragment newFragment = new DatePickerFragment(this,DatePickerType.RESINCRONIZATION);
        newFragment.show(getActivity().getSupportFragmentManager(),"datePicker");
    }

    void goMap(){
        this.navController.navigate(VisitsViewDirections.actionVisitsViewToVisitMapView());
    }

    void search(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        this.currentHint = inputSearch.getEditText().getText().toString();
        setClientsVisits(day,this.currentHint,true);
    }

    @Override
    public void goToHome() {
        this.navController.navigate(VisitsViewDirections.actionVisitsViewToHomeView());
    }

    @Override
    public void goToClients() {
        this.navController.navigate(VisitsViewDirections.actionVisitsViewToClientsView());
    }

    @Override
    public void goToSalesHistory() {
        this.navController.navigate(VisitsViewDirections.actionVisitsViewToHistoryView());
    }
    private String getCurrentDateStr(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public void setLoadingStatus(boolean loadingStatus) {
        if(loadingStatus){
            circularProgressIndicator.setVisibility(View.VISIBLE);
            isLoading=true;
            uploadChangesButton.setEnabled(false);
            downloadChangesButton.setEnabled(false);
        }else{
            circularProgressIndicator.setVisibility(View.GONE);
            isLoading=false;
            uploadChangesButton.setEnabled(true);
            downloadChangesButton.setEnabled(true);
        }
    }

    @Override
    public void setModeOffline(ModeOfflineNewVersion records) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                for(ClientToSaveEntity clientEntity : records.getClients()){
                    Client client = appDatabase.clientDao().getClientByKeyClient(clientEntity.getKeyClient());
                    if(client==null) {
                        client = new Client();
                        client.clientRovId = clientEntity.getClientId();
                        client.clientKey = clientEntity.getKeyClient();
                        client.cp = clientEntity.getCp();
                        client.latitude = clientEntity.getLatitude();
                        client.longitude = clientEntity.getLongitude();
                        client.monday = clientEntity.getMonday();
                        client.tuesday = clientEntity.getTuesday();
                        client.wednesday = clientEntity.getWednesday();
                        client.thursday = clientEntity.getThursday();
                        client.friday = clientEntity.getFriday();
                        client.saturday = clientEntity.getSaturday();
                        client.sunday = clientEntity.getSunday();
                        client.municipality = clientEntity.getMunicipality();
                        client.name = clientEntity.getName();
                        client.noExterior = clientEntity.getExtNum() != null ? clientEntity.getExtNum().toString() : "";
                        client.registeredInMobile = false;
                        client.sincronized = true;
                        client.street = clientEntity.getStreet();
                        client.type = clientEntity.getType();
                        client.suburb = clientEntity.getSuburb();
                        client.uid = records.getUid();
                        client.estatus = clientEntity.getStatus();
                        client.phoneNumber=clientEntity.getPhone();
                        client.contact=clientEntity.getContact();
                        client.reference = clientEntity.getReference();
                        appDatabase.clientDao().insertClient(client);
                    }else{
                        boolean modified=false;
                        if(client.clientRovId==null){
                            client.clientRovId=clientEntity.getClientId();
                            // actualizar las notas pendientes de este vendedor
                        }
                        if(clientEntity.getModified()) {
                            client.clientKey = clientEntity.getKeyClient();
                            client.cp = clientEntity.getCp();
                            client.latitude = clientEntity.getLatitude();
                            client.longitude = clientEntity.getLongitude();
                            client.monday = clientEntity.getMonday();
                            client.tuesday = clientEntity.getTuesday();
                            client.wednesday = clientEntity.getWednesday();
                            client.thursday = clientEntity.getThursday();
                            client.friday = clientEntity.getFriday();
                            client.saturday = clientEntity.getSaturday();
                            client.sunday = clientEntity.getSunday();
                            client.municipality = clientEntity.getMunicipality();
                            client.name = clientEntity.getName();
                            client.noExterior = clientEntity.getExtNum().toString();
                            client.registeredInMobile = false;
                            client.sincronized = true;
                            client.street = clientEntity.getStreet();
                            client.type = clientEntity.getType();
                            client.suburb = clientEntity.getSuburb();
                            client.uid = records.getUid();
                            client.estatus=clientEntity.getStatus();
                            client.phoneNumber=clientEntity.getPhone();
                            client.contact=clientEntity.getContact();
                            client.reference = clientEntity.getReference();
                            modified=true;

                        }
                        if(modified){
                            appDatabase.clientDao().updateClient(client);
                        }
                    }
                }
                UserDataInitial userData = appDatabase.userDataInitialDao().getDetailsInitialByUid(records.getUid());
                if (userData == null) {
                    UserDataInitial userDataInitial1 = new UserDataInitial();
                    userDataInitial1.name = records.getName();
                    viewModelStore.getStore().setUsername(records.getName());
                    userNameTextView.setText("Usuario: "+viewModelStore.getStore().getUsername());
                    userDataInitial1.count = records.getCount();
                    userDataInitial1.email = records.getEmail();
                    userDataInitial1.lastSincronization = records.getLastSicronization();
                    userDataInitial1.logedIn = true;
                    userDataInitial1.nomenclature = records.getNomenclature();
                    userDataInitial1.uid = records.getUid();
                    userDataInitial1.password = records.getPassword();
                    viewModelStore.setUserDataInitial(userDataInitial1);
                    appDatabase.userDataInitialDao().insertUserDataDetail(userDataInitial1);
                    System.out.println("Informacion de usuario guardada");
                    System.out.println("Usuario: "+userDataInitial1.name);
                }else{
                    UserDataInitial userDataInitial1 = appDatabase.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                    userDataInitial1.name = records.getName();
                    viewModelStore.getStore().setUsername(records.getName());
                    userNameTextView.setText("Usuario: "+viewModelStore.getStore().getUsername());
                    userDataInitial1.count = records.getCount();
                    userDataInitial1.email = records.getEmail();
                    userDataInitial1.lastSincronization = records.getLastSicronization();
                    userDataInitial1.logedIn = true;
                    userDataInitial1.nomenclature = records.getNomenclature();
                    userDataInitial1.uid = records.getUid();
                    userDataInitial1.password = records.getPassword();
                    viewModelStore.setUserDataInitial(userDataInitial1);
                    appDatabase.userDataInitialDao().updateUserData(userDataInitial1);
                    System.out.println("Actualizando vendedor");
                }
                for(ProductToSaveEntity productToSave : records.getProducts()){
                    Product product1 = appDatabase.productDao().getProductByProductKeyAndSellerId(productToSave.getProductKey(),records.getUid());
                    if(product1==null){
                        product1 = new Product();
                        product1.name = productToSave.getName();
                        product1.presentationId = productToSave.getPresentationId();
                        product1.presentationName = productToSave.getPresentationName();
                        product1.price = productToSave.getPrice();
                        product1.secondPrice=productToSave.getSecondPrice();
                        product1.productId = productToSave.getProductId();
                        product1.productKey = productToSave.getProductKey();
                        product1.quantity = productToSave.getQuantity();
                        product1.uniMed = productToSave.getUniMed();
                        product1.weightOriginal = productToSave.getWeightOriginal();
                        product1.sellerId = viewModelStore.getStore().getSellerId();
                        product1.esqKey=productToSave.getEsqKey();
                        product1.esqDescription = productToSave.getEsqDescription();
                        appDatabase.productDao().insertProduct(product1);
                        System.out.println("Producto instalado: " + product1.name + " " + product1.presentationName);
                    }else{
                        product1.name = productToSave.getName();
                        product1.presentationId = productToSave.getPresentationId();
                        product1.presentationName = productToSave.getPresentationName();
                        product1.price = productToSave.getPrice();
                        product1.secondPrice=productToSave.getSecondPrice();
                        product1.productId = productToSave.getProductId();
                        product1.quantity = productToSave.getQuantity();
                        product1.uniMed = productToSave.getUniMed();
                        product1.weightOriginal = productToSave.getWeightOriginal();
                        product1.sellerId = viewModelStore.getStore().getSellerId();
                        product1.esqKey = productToSave.getEsqKey();
                        product1.esqDescription=productToSave.getEsqDescription();
                        appDatabase.productDao().updateProduct(product1);
                        System.out.println("Producto actualizad: " + product1.name + " " + product1.presentationName);
                    }
                }
                for(PreSaleRecords preSaleToSave : records.getPreSalesOfDay()){
                    PreSale preSale = appDatabase.preSaleDao().getByFolio(preSaleToSave.getFolio());
                    if(preSale==null){
                        preSale = new PreSale();
                        preSale.amount = preSaleToSave.getAmount();
                        preSale.statusStr = preSaleToSave.getStatusStr();
                        preSale.keyClient = preSaleToSave.getKeyClient();
                        preSale.clientName = preSaleToSave.getClientName();
                        preSale.folio = preSaleToSave.getFolio();
                        preSale.saleId = preSaleToSave.getPreSaleId();
                        preSale.sincronized = true;
                        preSale.sellerId = preSaleToSave.getSellerId();
                        preSale.date = preSaleToSave.getDate();
                        preSale.clientId = preSaleToSave.getClientId();
                        preSale.urgent=preSaleToSave.getUrgent();
                        appDatabase.preSaleDao().insertAll(preSale);

                        for (SubSaleOfflineNewVersion subSaleOffline : preSaleToSave.getProducts()) {
                            SubSale subSale = new SubSale();
                            System.out.println("Creando subproducto: "+preSaleToSave.getFolio()+" - "+subSaleOffline.getProductId()+" "+subSaleOffline.getQuantity());
                            subSale.quantity = subSaleOffline.getQuantity();
                            subSale.uniMed = subSaleOffline.getUniMed();
                            subSale.weightStandar = subSaleOffline.getWeightStandar();
                            subSale.productPresentationType = subSaleOffline.getProductPresentationType();
                            subSale.productName = subSaleOffline.getProductName();
                            subSale.price = subSaleOffline.getPrice();
                            subSale.folio = preSaleToSave.getFolio();
                            subSale.presentationId = subSaleOffline.getPresentationId();
                            subSale.productId = subSaleOffline.getProductId();
                            subSale.productKey = subSaleOffline.getProductKey();
                            subSale.subSaleServerId = subSaleOffline.getSubSaleServerId();
                            appDatabase.subSalesDao().insertAllSubSales(subSale);
                        }
                        String[] dateForVisit = preSaleToSave.getDate().split("T");
                        if(dateForVisit.length>0){
                            ClientVisit clientVisit = appDatabase.clientVisitDao().getClientVisitByIdAndDate(preSale.clientId,dateForVisit[0]);
                            if(clientVisit==null){
                                clientVisit = new ClientVisit();
                                clientVisit.visited=true;
                                clientVisit.clientId=preSaleToSave.getClientId();
                                clientVisit.date=dateForVisit[0];
                                clientVisit.observations="";
                                clientVisit.amount=preSaleToSave.getAmount();
                                clientVisit.sincronized=true;
                                appDatabase.clientVisitDao().insertClientVisit(clientVisit);
                            }else{
                                clientVisit.amount+=preSaleToSave.getAmount();
                                appDatabase.clientVisitDao().updateClientVisit(clientVisit);
                            }
                        }
                    }else{
                        System.out.println("Actualizando estatus");
                        preSale.amount = preSaleToSave.getAmount();
                        preSale.statusStr = preSaleToSave.getStatusStr();
                        preSale.keyClient = preSaleToSave.getKeyClient();
                        preSale.clientName = preSaleToSave.getClientName();
                        preSale.folio = preSaleToSave.getFolio();
                        preSale.saleId = preSaleToSave.getPreSaleId();
                        preSale.sincronized = true;
                        preSale.sellerId = preSaleToSave.getSellerId();
                        preSale.date = preSaleToSave.getDate();
                        preSale.clientId = preSaleToSave.getClientId();
                        preSale.urgent=preSaleToSave.getUrgent();
                        appDatabase.preSaleDao().updateSale(preSale);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setLoadingStatus(false);
                        modalMessageOperation("Descarga de cambios exitosa");
                    }
                });
            }
        });
    }

    @Override
    public void modalMessageOperation(String msg){
        System.out.println("Mensaje a imprimir: "+msg);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.modal_success_operation,null);
        TextView messageLoad = view.findViewById(R.id.message_load);
        Button btnAccept = view.findViewById(R.id.acceptButtonModal);
        messageLoad.setText(msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog modalInfo= builder.show();
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalInfo.dismiss();
            }
        });

    }

    android.app.AlertDialog dialogSinc=null;
    @Override
    public void modalSincronizationStart(String msg){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_load_message,null);
        TextView messageLoad = view.findViewById(R.id.message_load);
        messageLoad.setText(msg);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(false);
        this.dialogSinc=builder.create();
        this.dialogSinc.show();
    }

    @Override
    public void setUploadingStatus(boolean flag){
        if(flag){
            circularProgressIndicator.setVisibility(View.VISIBLE);
            isLoading=true;
            uploadChangesButton.setEnabled(false);
            downloadChangesButton.setEnabled(false);
        }else{
            circularProgressIndicator.setVisibility(View.GONE);
            isLoading=false;
            uploadChangesButton.setEnabled(true);
            downloadChangesButton.setEnabled(true);
        }
    }

    /** registering clients unsincronized */
    @Override
    public void firstStep(){
        modalSincronizationStart("Subiendo cambios");
        setUploadingStatus(true);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
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
                        presenter.tryRegisterClients(requestRegister);
                    }
                });
            }
        });
    }
    /** updating clients registered to database*/
    @Override
    public void setClientsRegisters(List<ClientV2Response> clientsRegistered) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                for(ClientV2Response clientReg : clientsRegistered){
                    Client client = conexion.clientDao().getClientByClientIdMobile(clientReg.getClientMobileId());
                    client.clientRovId=clientReg.getClientId();
                    client.clientKey=clientReg.getClientId();
                    client.clientKeyTemp=clientReg.getClientId();
                    client.sincronized=true;
                    conexion.clientDao().updateClient(client);
                    ClientVisit clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(clientReg.getClientMobileId(),dateSincronization);
                    if(clientVisit!=null) {
                        clientVisit.clientId = clientReg.getClientId();
                        conexion.clientVisitDao().updateClientVisit(clientVisit);
                    }
                    List<PreSale> salesTemp = conexion.preSaleDao().getAllSalesByDateAndClientId(dateSincronization+"T00:00:00.000Z",dateSincronization+"T23:59:59.000Z",clientReg.getClientMobileId());
                    for(PreSale preSale : salesTemp){
                        preSale.isTempKeyClient=false;
                        preSale.keyClient=client.clientRovId;
                        preSale.clientId=client.clientRovId;
                        conexion.preSaleDao().updateSale(preSale);
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
    @Override
    public void secondStep(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<Client> clientsUnsincronized =conexion.clientDao().getAllClientsUnsicronizedNotTemp();
                List<ClientV2UpdateRequest> clientV2UpdateRequestList = new ArrayList<>();
                for(Client client : clientsUnsincronized) {
                    System.out.println("ClientSincronized: "+client.sincronized);
                    System.out.println("ClientRovId: "+client.clientRovId);
                    if (client.sincronized==false) {
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
                        presenter.updateCustomerV2(clientV2UpdateRequestList);
                    }
                });
            }
        });
    }
    /** Updating clients updated to database*/
    @Override
    public void setClientsUpdated(List<ClientV2UpdateResponse> clientsUpdated) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
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
    @Override
    public void thirdStep(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
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
                        presenter.registerVisitsV2(requests);
                    }
                });
            }
        });
    }
    /** Updating clients visits in database */
    @Override
    public void setClientVisitedRegistered(List<ClientV2VisitResponse> clientV2Visit) {
        System.out.println("Actualizando estatus de visitas");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                for(ClientV2VisitResponse response : clientV2Visit){
                    ClientVisit clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(response.getClientId(),response.getDate());
                    clientVisit.sincronized=true;
                    conexion.clientVisitDao().updateClientVisit(clientVisit);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Iniciando proceso de ventas");
                        sendPreSalesToSystem();
                    }
                });
            }
        });
    }

    @Override
    public void sendPreSalesToSystem(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<PreSale> preSales=new ArrayList<>();
                if(dateSincronization==null) {
                    preSales = conexion.preSaleDao().getAllPreSalesUnsincronized();
                }else{
                    String date1=dateSincronization+"T00:00:00.000Z";
                    String date2=dateSincronization+"T23:59:59.000Z";
                    preSales = conexion.preSaleDao().getAllPreSalesUnsincronziedByDate(date1,date2);
                }
                List<ModeOfflineSM> modeOfflineSMS = new ArrayList<>();
                for(PreSale preSale : preSales) {
                    ModeOfflineSM modeOfflineSM = new ModeOfflineSM();
                    modeOfflineSM.setAmount(preSale.amount);
                    modeOfflineSM.setClientId(preSale.clientId);
                    modeOfflineSM.setDate(preSale.date);
                    modeOfflineSM.setFolio(preSale.folio);
                    modeOfflineSM.setDateToDeliver(preSale.dateToDeliver);
                    modeOfflineSM.setSellerId(preSale.sellerId);
                    modeOfflineSM.setUrgent(preSale.urgent);
                    List<SubSale> subSales = conexion.subSalesDao().getSubSalesBySale(preSale.folio);
                    List<ModeOfflineSMP> modeOfflineSMPS = new ArrayList<>();
                    for(SubSale subSale : subSales){
                        ModeOfflineSMP modeOfflineSMP = new ModeOfflineSMP();
                        modeOfflineSMP.setPresentationId(subSale.presentationId);
                        modeOfflineSMP.setProductId(subSale.productId);
                        modeOfflineSMP.setQuantity(subSale.quantity);
                        modeOfflineSMP.setAmount(subSale.price);
                        modeOfflineSMPS.add(modeOfflineSMP);
                    }
                    modeOfflineSM.setProducts(modeOfflineSMPS);
                    modeOfflineSMS.add(modeOfflineSM);
                }

                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        System.out.println("Sincronizando notas");
                        if(modeOfflineSMS.size()>0) {
                            presenter.sincronizePreSales(modeOfflineSMS);
                        }else{
                            setUploadingStatus(false);
                            modalSincronizationEnd();
                            modalMessageOperation("Sincronización exitosa");
                            checkIfRecordsWithoutSincronization();
                        }
                    }
                });
            }
        });
    }


    @Override
    public void modalSincronizationEnd(){
        if(this.dialogSinc!=null && this.dialogSinc.isShowing()){
            this.dialogSinc.dismiss();
        }
    }

    @Override
    public void completeSincronzation(SincronizationPreSaleResponse sincronizationResponse) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                for(int i=0;i<sincronizationResponse.getPreSalesSincronized().size();i++){
                    String folio = sincronizationResponse.getPreSalesSincronized().get(i).getFolio();
                    Integer preSaleId = sincronizationResponse.getPreSalesSincronized().get(i).getPreSaleId();
                    conexion.preSaleDao().updatePreSaleIdByFolio(preSaleId,folio);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setUploadingStatus(false);
                        modalSincronizationEnd();
                        modalMessageOperation("Sincronización exitosa");
                        checkIfRecordsWithoutSincronization();
                    }
                });
            }
        });
    }

    @Override
    public void showNotificationSincronization(String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "rovisapi")
                .setSmallIcon(R.drawable.ic_logorov)
                .setContentTitle("Sistema Rovianda")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void hiddeNotificationSincronizastion() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.cancel(1);
    }

    @Override
    public void checkIfRecordsWithoutSincronization(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                List<PreSale> preSales = appDatabase.preSaleDao().getAllPreSalesUnsincronized();
                List<Client> clients = appDatabase.clientDao().getAllClientsUnsicronizedNotTemp();
                List<ClientVisit> clientVisits = appDatabase.clientVisitDao().getClientVisitUnsincronized();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(preSales.size()>0 || clientVisits.size()>0 || clients.size()>0){
                            setWithChangesToUpload();
                        }else{
                            setWithoutChangesToUpload();
                        }
                    }
                });
            }
        });
    }
    private void setWithoutChangesToUpload(){
        downloadChangesButton.setEnabled(true);
        downloadChangesButton.setColorFilter(Color.parseColor("#2FBF34"));
        uploadChangesButton.setColorFilter(Color.GRAY);
        uploadChangesButton.setEnabled(false);
        showNotificationSincronization("Sistema de sincronización, Nada por sincronizar...");
    }

    private void setWithChangesToUpload(){
        downloadChangesButton.setEnabled(false);
        downloadChangesButton.setColorFilter(Color.GRAY);
        uploadChangesButton.setColorFilter(Color.parseColor("#2FBF34"));
        uploadChangesButton.setEnabled(true);
        showNotificationSincronization("Hay cambios por sincronizar....");
    }

    @Override
    public void onDateSet(String date, DatePickerType datePickerType) {
        if(datePickerType.equals(DatePickerType.RESINCRONIZATION)) {
            String dateSelected = date;
            setUploadingStatus(true);
            modalSincronizationStart("Sincronizando");
            if (this.actionForDatePicker == 1) {
                presenter.getDataInitial(viewModelStore.getStore().getSellerId(), dateSelected);
            } else if (this.actionForDatePicker == 2) {
                dateSincronization = dateSelected;
                sendPreSalesToSystem();
            }
        }
    }

    @Override
    public void onDateCancel() {

    }
}
