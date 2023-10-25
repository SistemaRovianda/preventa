package com.rovianda.preventa.home.view;


import android.Manifest;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.rovianda.preventa.R;
import com.rovianda.preventa.home.DatePickerType;
import com.rovianda.preventa.home.fragments.MapDialogFragment;
import com.rovianda.preventa.home.models.LocationTrackModel;
import com.rovianda.preventa.home.presenter.HomePresenter;
import com.rovianda.preventa.home.presenter.HomePresenterContract;
import com.rovianda.preventa.utils.DatePickerFragment;
import com.rovianda.preventa.utils.LocationTrack;
import com.rovianda.preventa.utils.NumberDecimalFilter;
import com.rovianda.preventa.utils.PrinterUtil;
import com.rovianda.preventa.utils.ViewModelStore;
import com.rovianda.preventa.utils.bd.AppDatabase;
import com.rovianda.preventa.utils.bd.entities.Client;
import com.rovianda.preventa.utils.bd.entities.ClientVisit;
import com.rovianda.preventa.utils.bd.entities.EndingDay;
import com.rovianda.preventa.utils.bd.entities.PreSale;
import com.rovianda.preventa.utils.bd.entities.Product;
import com.rovianda.preventa.utils.bd.entities.SubSale;
import com.rovianda.preventa.utils.bd.entities.UserDataInitial;
import com.rovianda.preventa.utils.models.AddressCoordenatesResponse;
import com.rovianda.preventa.utils.models.ClientDTO;
import com.rovianda.preventa.utils.models.ModeOfflineSM;
import com.rovianda.preventa.utils.models.ModeOfflineSMP;
import com.rovianda.preventa.utils.models.PreSaleSincronizedResponse;
import com.rovianda.preventa.utils.models.ProductRoviandaToSale;
import com.rovianda.preventa.utils.models.SincronizationPreSaleResponse;

import java.nio.MappedByteBuffer;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class HomeView extends Fragment implements HomeViewContract, View.OnClickListener {

    private BluetoothAdapter bluetoothAdapter;
    private NavController navController;
    private TextView userNameTextView, amountTextView, clientKeySae, clientName;
    private ViewModelStore viewModelStore;
    private HomePresenterContract presenter;
    private ImageView printerButton;
    private MaterialButton searchClientButton, addProductButton, genPreSaleButton;
    private Button logoutButton, endDayButton;
    private Float amount;
    private CircularProgressIndicator circularProgressIndicator;
    private BottomNavigationView bottomMenu;
    private TextInputLayout clientInput, keyProductInput, weightProductInput;
    private List<ProductRoviandaToSale> carList;
    private LinearLayout listCarSale;
    private PrinterUtil printerUtil;
    private Gson parser;
    private boolean logoutPicked = false;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    private boolean sincronizateSession = false;
    private Client clientSelected = null;
    private boolean findingClient = false;
    private boolean findingProduct = false;
    private boolean isPaying = false;
    private BluetoothDevice printer;
    private boolean printerConnected = false;
    private boolean isPickingPrinter = false;
    private int printerIndexSeleced=1;
    private String dateForPresale=null;
    private LocationTrack locationTrack;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, null);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.navController = NavHostFragment.findNavController(this);
        this.userNameTextView = view.findViewById(R.id.userName);
        this.presenter = new HomePresenter(getContext(), this);

        this.printerButton = view.findViewById(R.id.printerButton);
        this.printerButton.setOnClickListener(this);
        this.addProductButton = view.findViewById(R.id.AgregarProductoButton);
        this.addProductButton.setOnClickListener(this);
        this.amount = 0f;
        this.logoutButton = view.findViewById(R.id.Logout_button);
        this.logoutButton.setOnClickListener(this);

        this.circularProgressIndicator = view.findViewById(R.id.loginLoadingSpinner);

        this.bottomMenu = view.findViewById(R.id.bottom_navigation_home);
        this.bottomMenu.setSelectedItemId(R.id.home_section);
        this.searchClientButton = view.findViewById(R.id.buscarClienteButton);
        this.searchClientButton.setOnClickListener(this);

        this.clientInput = view.findViewById(R.id.cliente_input);
        this.keyProductInput = view.findViewById(R.id.codigo_producto);
        this.weightProductInput = view.findViewById(R.id.peso_input);
        this.weightProductInput.getEditText().setFilters(new InputFilter[]{new NumberDecimalFilter()});
        this.carList = new ArrayList<>();
        this.listCarSale = view.findViewById(R.id.lista_carrito);
        this.amountTextView = view.findViewById(R.id.total);
        this.amountTextView.setText("Total: " + amount.toString());
        this.genPreSaleButton = view.findViewById(R.id.genPreSaleButton);
        this.genPreSaleButton.setOnClickListener(this);
        this.endDayButton = view.findViewById(R.id.end_day_button);
        this.endDayButton.setOnClickListener(this);
        this.printerUtil = new PrinterUtil(getContext());
        this.parser = new Gson();
        this.clientKeySae = view.findViewById(R.id.cliente_key_sae);
        this.clientName = view.findViewById(R.id.cliente_name);
        this.locationTrack = new LocationTrack(getContext());
        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_section:
                        // PANTALLA ACTUAL
                        break;
                    case R.id.visitas_section:
                        goToAnotherSection(2);
                        break;
                    case R.id.cliente_section:
                        goToAnotherSection(3);
                        break;
                    case R.id.history_section:
                        goToAnotherSection(4);
                        break;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.locationTrack.stopListener();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        this.userNameTextView.setText("Usuario: " + this.viewModelStore.getStore().getUsername());
        checkIfSincronizate();
        checkIfPrinterConfigured();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        dateForPresale = format.format(calendar.getTime());
    }

    void checkIfPrinterConfigured() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitial = conexion.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        if (userDataInitial != null && userDataInitial.printerMacAddress != null) {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (bluetoothAdapter.isEnabled()) {
                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                                    for (BluetoothDevice bluetoothDevice : pairedDevices) {
                                        if (bluetoothDevice.getAddress().equals(userDataInitial.printerMacAddress)) {
                                            printer = bluetoothDevice;
                                            printerConnected = true;
                                            printerConnected();
                                        }
                                    }
                                    if (!printerConnected) {
                                        printerDisconnected();
                                    }
                                    return;
                                }
                            } else {
                                genericMessage("Bluetooth", "Activa el bluetooth");
                            }
                        }
                    }
                });
            }
        });
    }

    private void printerConnected() {
        ImageViewCompat.setImageTintList(printerButton, ColorStateList.valueOf(Color.parseColor("#BDB5B5")));
    }

    private void printerDisconnected() {
        ImageViewCompat.setImageTintList(printerButton, ColorStateList.valueOf(Color.parseColor("#39ED20")));
    }

    void checkIfSincronizate() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitials = conexion.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (userDataInitials == null) {
                            genericMessage("Alerta Sistema", "Requiere de su primera sincronización");
                            sincronizateSession = false;
                        } else {
                            viewModelStore.setUserDataInitial(userDataInitials);
                            sincronizateSession = true;
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Logout_button:
                if (!logoutPicked) {
                    logoutPicked = true;
                    logout();
                }
                break;
            case R.id.buscarClienteButton:
                if (!findingClient) {
                    findingClient = true;
                    searchClient();
                }
                break;
            case R.id.AgregarProductoButton:
                if (!findingProduct) {
                    setStatusFindingProduct(true);
                    findProduct();
                }
                break;
            case R.id.genPreSaleButton:
                if (!isPaying) {
                    if (carList.size() > 0) {
                        isPaying = true;
                        payProducts();
                    } else {
                        Toast.makeText(getContext(), "No haz agregado productos", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.printerButton:
                if (this.printerConnected) {
                    this.printerConnected = false;
                    if (this.printerUtil != null) {
                        this.printerUtil.desconect();
                        this.printer = null;
                    }
                    this.printerDisconnected();
                } else {
                    if(!isPickingPrinter) {
                        isPickingPrinter = true;
                        activatePrinter();
                    }
                }
                break;
            case R.id.end_day_button:
                showDatePicker();
                break;
        }
    }

    void showDatePicker() {
        DatePickerFragment newFragment = new DatePickerFragment(this, DatePickerType.END_DAY);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    void checkAllVisitsForCurrentDay(String currentDate){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                Calendar calendar = Calendar.getInstance();
                int day =calendar.get(Calendar.DAY_OF_WEEK);
                List<Client> clients = new ArrayList<>();
                List<Client> withouPendingVisitRecord = new ArrayList<>();
                switch (day){
                    case Calendar.MONDAY:
                        clients = conexion.clientDao().getClientsMonday(viewModelStore.getStore().getSellerId());
                        break;
                    case Calendar.TUESDAY:
                        clients = conexion.clientDao().getClientsTuesday(viewModelStore.getStore().getSellerId());
                        break;
                    case Calendar.WEDNESDAY:
                        clients=conexion.clientDao().getClientsWednesday(viewModelStore.getStore().getSellerId());
                        break;
                    case Calendar.THURSDAY:
                        clients=conexion.clientDao().getClientsThursday(viewModelStore.getStore().getSellerId());
                        break;
                    case Calendar.FRIDAY:
                        clients = conexion.clientDao().getClientsFriday(viewModelStore.getStore().getSellerId());
                        break;
                    case Calendar.SATURDAY:
                        clients = conexion.clientDao().getClientsSaturday(viewModelStore.getStore().getSellerId());
                        break;
                }
                for(Client client : clients){
                    ClientVisit clientVisit=null;
                    if(client.clientRovId!=null && client.clientRovId!=0) {
                        clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(client.clientRovId,currentDate);
                    }else{
                        clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(client.clientMobileId,currentDate);
                    }
                    if(clientVisit==null){
                        withouPendingVisitRecord.add(client);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        findEndDay(currentDate, "CLOSE");
                        /*if(withouPendingVisitRecord.size()>0){
                            genericMessage("Visitas pendientes","Falta registrar visitas a clientes");
                        }else{
                            findEndDay(currentDate, "CLOSE");
                        }*/
                    }
                });
            }
        });
    }

    void activatePrinter() {
        if (!this.bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
            isPickingPrinter=false;
        } else {
            printerUtil = new PrinterUtil(getContext());
            final Set<BluetoothDevice> deviceList = printerUtil.findDevices();
            if (deviceList.size() > 0) {
                findPrinter(deviceList);
            }else{
                genericMessage("Sin dispositivos","Dispositivos bluetooth no encontrados");
            }
        }

    }

    public void findPrinter(Set<BluetoothDevice> devices) {

        List<BluetoothDevice> bluetoothDevicesMapped = new ArrayList<>();
        for (BluetoothDevice device : devices) {
            bluetoothDevicesMapped.add(device);
        }
        String[] bluetoothDevices = new String[devices.size()];
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            for (int i = 0; i < devices.size(); i++) {
                bluetoothDevices[i] = bluetoothDevicesMapped.get(i).getName();
            }
        }
        int checkedItem = 1;

        new MaterialAlertDialogBuilder(getContext()).setTitle("Selecciona la impresora bluetooth")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showErrorConnectingPrinter();
                        isPickingPrinter=false;
                    }
                }).setPositiveButton("Enlazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Index: "+which);
                        if(printerIndexSeleced!=-1) {
                            which=printerIndexSeleced;
                            System.out.println("Estableciendo conexión");
                            String printerName = bluetoothDevices[which];
                            printer = bluetoothDevicesMapped.get(which);
                            printerConnected = printerUtil.connectWithPrinter(printer);
                            if(printerConnected==true) {
                                connectionPrinterSuccess(printerName);
                                printerConnected();
                            }else {
                                showErrorConnectingPrinter();
                                printerDisconnected();
                            }
                            isPickingPrinter=false;
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    AppDatabase conexion = AppDatabase.getInstance(getContext());
                                    conexion.userDataInitialDao().updatePrinterAddress(viewModelStore.getStore().getSellerId(),printer.getAddress());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            System.out.println("Printer saved");
                                        }
                                    });
                                }
                            });

                        }
                    }
                }).setSingleChoiceItems(bluetoothDevices, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Se selecciono uno: "+which);
                        printerIndexSeleced=which;
                    }
                }).setCancelable(false).show();;
    }



    void payProducts() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateFormat = dateFormat.format(calendar.getTime());
        findEndDay(currentDateFormat, "PRESALE");
    }


    void findEndDay(String endDay, String type) { // METODO PARA VERIFICAR SI ES POSIBLE REALIZAR UNA VENTA SIEMPRE Y CUANDO NO SE HAYA CERRADO EL DIA
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                EndingDay endingDay = conexion.endingDayDao().getEndingDayByDate(endDay);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (type.equals("CLOSE")) {
                            if (endingDay != null) {
                                getEndDayTicketOffline(endDay);
                            } else {
                                showAlertEndDay(endDay);
                            }
                        } else if (type.equals("PRESALE")) {
                            if (endingDay == null) {
                                showAddressToAssign();
                            } else {
                                isPaying = false;
                                genericMessage("Alerta", "Ya no se pueden hacer ventas en la fecha actual");
                            }
                        }
                    }
                });
            }
        });
    }

    void showAlertEndDay(String currentDate) {// MODAL DE CIERRE DE DIA
        new MaterialAlertDialogBuilder(getContext()).setTitle("Cierre de día")
                .setMessage("¿Está seguro que desea cerrar las preventas del dia " + currentDate + ", (No podrá realizar otra preventa en esta fecha) ?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveEndDayRecord(currentDate);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isPaying = false;
            }
        }).show();

    }

    void saveEndDayRecord(String endDay) { // METODO DE GUARDADO DE FIN DE DIA
        executor.execute(new Runnable() {
            @Override
            public void run() {

                EndingDay endingDay = new EndingDay();
                endingDay.date = endDay;
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                conexion.endingDayDao().saveEndingDay(endingDay);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        presenter.sendEndDayRecord(endDay, viewModelStore.getStore().getSellerId());
                        getEndDayTicketOffline(endDay);
                    }
                });
            }
        });
    }


    void findDataForPreSale(boolean forSelect){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                UserDataInitial details = appDatabase.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                Integer currentFolio = details.count;
                String nomenclature = details.nomenclature;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(clientSelected!=null) {
                            doPreSaleModalConfirmation(nomenclature, currentFolio, forSelect);
                        }
                    }
                });
            }
        });
    }

    private Boolean preSaleUrgent=null;
    void doPreSaleModalConfirmation(String nomenclature,Integer currentFolio,boolean forSelect) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.modal_presale,null);
        TextView folio = view.findViewById(R.id.preSaleFolio);
        TextView clientDetails = view.findViewById(R.id.clientDetails);
        TextView amountTextView = view.findViewById(R.id.amountPreSale);
        TextView dateToDeliver = view.findViewById(R.id.dateToDeliverPreSale);
        ImageView calendarButton = view.findViewById(R.id.calendarIcon);
        Button cancel = view.findViewById(R.id.cancelPreSale);
        Button confirm = view.findViewById(R.id.genPreSaleConfirm);
        CheckBox checkboxUrgent = view.findViewById(R.id.checkboxUrgent);
        if(preSaleUrgent!=null){
            checkboxUrgent.setChecked(preSaleUrgent);
        }
        if(!forSelect){
            confirm.setEnabled(false);
            confirm.setBackgroundColor(Color.parseColor("#8EB8DA"));
        }

        folio.setText("Folio Preventa: "+nomenclature+(currentFolio+1));
        clientDetails.setText("Cliente: "+clientSelected.name);
        amountTextView.setText("Monto: $"+String.format("%.02f",amount));


        dateToDeliver.setText("Fecha de reparto: "+dateForPresale);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog modal =builder.show();
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preSaleUrgent=checkboxUrgent.isChecked();
                openCalendarForPreSale(nomenclature, currentFolio);
                modal.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPaying=false;
                preSaleUrgent=null;
                modal.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preSaleUrgent=checkboxUrgent.isChecked();
                modal.dismiss();
                saveSqlSale(amount,preSaleUrgent);
            }
        });
    }

    void openCalendarForPreSale(String nomenclature,Integer currentFolio){
        if(clientSelected!=null) {
            DatePickerFragment newFragment = new DatePickerFragment(this, DatePickerType.CREATION_PRESALE);
            newFragment.setCancelable(false);
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
    }

    void saveSqlSale(Float amount,Boolean urgent) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateFormat = dateFormat.format(calendar.getTime());
        executor.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitial = conexion.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                Map<String, Product> productsMap = new HashMap<>();
                Integer folioCount = userDataInitial.count;
                PreSale preSale = new PreSale();
                preSale.amount = amount;
                preSale.clientName = clientSelected.name;
                preSale.dateToDeliver=dateForPresale;
                preSale.urgent=urgent;
                ZonedDateTime zdt = ZonedDateTime.now();
                zdt = zdt.minusHours(5);
                String nowAsISO = zdt.format(DateTimeFormatter.ISO_INSTANT);
                preSale.date = nowAsISO;
                System.out.println("FECHA DE GUARDADO: "+nowAsISO);
                if (clientSelected.clientRovId == null || clientSelected.clientRovId == 0) {
                    preSale.keyClient = clientSelected.clientKeyTemp;
                    preSale.isTempKeyClient = true;
                } else {
                    preSale.keyClient = clientSelected.clientKey;
                    preSale.isTempKeyClient = false;
                }
                preSale.sellerId = viewModelStore.getStore().getSellerId();
                preSale.sincronized = false;
                preSale.statusStr = "ACTIVE";
                if (clientSelected.clientRovId != null && clientSelected.clientRovId != 0) {
                    preSale.clientId = clientSelected.clientRovId;
                } else {
                    preSale.clientId = clientSelected.clientMobileId;
                }
                preSale.folio = userDataInitial.nomenclature + (folioCount + 1);
                List<SubSale> subSales = new ArrayList<>();
                for (ProductRoviandaToSale product : carList) {
                    Product product1 = conexion.productDao().getProductByKey(product.getKeySae());
                    productsMap.put(product.getKeySae(), product1);
                    SubSale subSale = new SubSale();
                    subSale.folio = userDataInitial.nomenclature + (folioCount + 1);
                    subSale.price = product.getPrice() * product.getQuantity();
                    subSale.productKey = product.getKeySae();
                    subSale.productName = product.getNameProduct();
                    subSale.productPresentationType = product.getPresentationType();
                    subSale.quantity = product.getQuantity();
                    subSale.weightStandar = product.getWeightOriginal();
                    subSale.presentationId = product.getPresentationId();
                    subSale.productId = product.getProductId();
                    subSale.uniMed = product.isIsPz() ? "PZ" : "KG";
                    subSales.add(subSale);
                }
                conexion.preSaleDao().insertAll(preSale);
                conexion.userDataInitialDao().updateFolioCount(folioCount + 1, viewModelStore.getStore().getSellerId());
                for (SubSale subSale1 : subSales) {
                    conexion.subSalesDao().insertAllSubSales(subSale1);
                }
                ClientVisit clientVisit = null;

                if (clientSelected.clientRovId != null && clientSelected.clientRovId != 0) { // BUSCANDO LOS REGISTROS DE VISITAS
                    clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(clientSelected.clientRovId, currentDateFormat);
                } else {
                    clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(clientSelected.clientMobileId, currentDateFormat);
                }
                if (clientVisit == null) {
                    clientVisit = new ClientVisit();
                    clientVisit.isClientIdTemp = !(clientSelected.clientRovId != null && clientSelected.clientRovId != 0);
                    clientVisit.clientId = (clientSelected.clientRovId != null && clientSelected.clientRovId != 0) ? clientSelected.clientRovId : clientSelected.clientMobileId;
                    clientVisit.visited = true;
                    clientVisit.observations = "";
                    clientVisit.sincronized = false;
                    clientVisit.amount = preSale.amount;
                    clientVisit.date = currentDateFormat;
                    conexion.clientVisitDao().insertClientVisit(clientVisit);
                } else {
                    clientVisit.amount += preSale.amount;
                    clientVisit.sincronized = false;
                    clientVisit.visited = true;
                    clientVisit.observations = "";
                    conexion.clientVisitDao().updateClientVisit(clientVisit);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isPaying = false;
                        preSaleUrgent=null;
                        checkIfClientHasLocation(clientSelected);
                        doTicketSaleOffline(preSale, subSales, productsMap,clientSelected);
                        System.out.println("SaleSavedInSQL");
                        tryToUploadSales();
                    }
                });
            }
        });
    }

    void tryToUploadSales() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
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
                        modeOfflineSM.setUrgent(preSale.urgent);
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
                            showNotificationSincronization("HOME Sincronizando...");
                            presenter.sincronizePreSales(modeOfflineSMS);
                        } else {
                            showNotificationSincronization("Nada por sincronizar...");
                        }
                    }

                });
            }
        });
    }

    void doTicketSaleOffline(PreSale preSale, List<SubSale> subSales, Map<String, Product> productsMap,Client clientSelected) {
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        String dateParsed = dateFormat.format(cal.getTime());
        String ticket = "ROVIANDA SAPI DE CV\nAV.1 #5 Esquina Calle 1\nCongregación Donato Guerra\nParque Industrial Valle de Orizaba\nC.P 94780\nRFC 8607056P8\nTEL 272 72 46077, 72 4 5690\n";
        ticket += "Pago en una Sola Exhibición\nLugar de Expedición: Ruta\nNota No. " + preSale.folio + "\nFecha: " + dateParsed + "\nFecha de reparto: "+dateForPresale+"\n";
        ticket += "Vendedor:" + viewModelStore.getStore().getUsername() + "\n\nCliente: " + clientSelected.name + "\n" + (preSale.isTempKeyClient == true ? "Clave Temp:" : "Clave:") + clientSelected.clientKey + "\n";
        ticket += "Tipo de venta: PREVENTA"+((preSale.urgent!=null && preSale.urgent==true)?" URGENTE ":"")+" \n--------------------------------\nDESCR   PRECIO   CANT  IMPU.   IMPORTE \n--------------------------------\n";
        Float total = Float.parseFloat("0");
        Float totalImp = Float.parseFloat("0");
        for (SubSale product : subSales) {
            Product product1 = productsMap.get(product.productKey);
            Float singleIva = Float.parseFloat("0");
            Float singleIeps = Float.parseFloat("0");
            Float amount = (product.price / product.quantity);
            switch (product1.esqKey) {
                case 1:
                    singleIva = this.extractIva(amount);
                    break;
                case 4:
                    singleIva = this.extractIva(amount);
                    singleIeps = this.extractIeps((amount - this.extractIva(amount)), Float.parseFloat("8"));
                    break;
                case 5:
                    singleIva = this.extractIva(amount);
                    singleIeps = this.extractIeps((amount - this.extractIva(amount)), Float.parseFloat("25"));
                    break;
                case 6:
                    singleIva = this.extractIva(amount);
                    singleIeps = this.extractIeps((amount - this.extractIva(amount)), Float.parseFloat("50"));
                    break;
            }
            Float singlePrice = amount - (singleIva + singleIeps);
            totalImp += ((singleIva + singleIeps) * product.quantity);
            if (product.uniMed.equals("PZ")) {
                ticket += product.productName + " " + product.productPresentationType + "\n" + String.format("%.02f", singlePrice) + " " + Math.round(product.quantity) + "pz " + String.format("%.02f", (singleIeps + singleIva) * product.quantity) + " " + String.format("%.02f", product.price) + "\n";
            } else {
                ticket += product.productName + " " + product.productPresentationType + "\n" + Math.round(singlePrice) + " " + product.quantity + "kg " + String.format("%.02f", (singleIeps + singleIva) * product.quantity) + " " + String.format("%.02f", product.price) + "\n";
            }
            total += product.price;
        }
        ticket += "--------------------------------\n";
        ticket += "SUB TOTAL: $" + String.format("%.02f", total - totalImp) + "\n";
        ticket += "IMPUESTO:  $" + String.format("%.02f", totalImp) + "\n";
        ticket += "TOTAL: $ " + String.format("%.02f", total) + "\n\n\n";
        System.out.println(ticket);
        ticket += ticket;

        saleSuccess(ticket);
    }

    Float extractIva(Float amount) {
        return (amount / 116) * 16;
    }

    Float extractIeps(Float amount, Float percent) {
        return (amount / (100 + percent)) * percent;
    }

    void checkIfClientHasLocation(Client clientSelected) {
        if (clientSelected.latitude == null && clientSelected.longitude == null) {
            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationTrackModel loc = getLastKnownLocation();
                if (loc != null) {
                    clientSelected.latitude = loc.getLatitude();
                    clientSelected.longitude = loc.getLongitude();
                    clientSelected.sincronized = false;
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase conexion = AppDatabase.getInstance(getContext());
                            conexion.clientDao().updateClient(clientSelected);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // SE ACTUALIZA LAS COORDENADAS DEL CLIENTE
                                }
                            });
                        }
                    });
                } else {
                    genericMessage("Localización requerida", "Se requiere que se establezca la ubicación del cliente de forma manual.");
                }
            }
        }
    }

    private LocationTrackModel getLastKnownLocation() {
        LocationTrackModel locationTrackModel = null;
        if(locationTrack.canGetLocation()){
            locationTrackModel= new LocationTrackModel();
            locationTrackModel.setLatitude(locationTrack.getLatitude());
            locationTrackModel.setLongitude(locationTrack.getLongitude());
        }
        return locationTrackModel;
    }

    void findProduct() {
        if(clientSelected!=null) {
            String productKey = keyProductInput.getEditText().getText().toString();
            String quantity = weightProductInput.getEditText().getText().toString();
            if (!productKey.isEmpty() && !quantity.isEmpty()) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                        Product product = appDatabase.productDao().getProductByProductKeyAndSellerId(productKey, viewModelStore.getStore().getSellerId());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (product == null) {
                                    findByProductEnd("%" + productKey);
                                } else {
                                    addProductToSaleCar(product);
                                }
                            }
                        });
                    }
                });
            } else {
                if (productKey.isEmpty()) {
                    keyProductInput.getEditText().setError("El campo no puede ser vacio.");
                }
                if (quantity.isEmpty()) {
                    weightProductInput.getEditText().setError("El campo no puede ser vacio.");
                }
                setStatusFindingProduct(false);
            }
        }else{
            genericMessage("Sin cliente","Favor de seleccionar un cliente valido.");
        }
    }

    void findByProductEnd(String productKey) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                Product product = conexion.productDao().getProductByProductKeyAndSellerId(productKey, viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (product == null) {
                            setStatusFindingProduct(false);
                            keyProductInput.getEditText().setError("No existe el producto indicado");
                        } else {
                            addProductToSaleCar(product);
                        }
                    }
                });
            }
        });
    }

    private void setStatusFindingProduct(boolean flag) {
        if (flag) {
            findingProduct = true;
            circularProgressIndicator.setVisibility(View.VISIBLE);
        } else {
            findingProduct = false;
            circularProgressIndicator.setVisibility(View.GONE);
        }
    }

    private void addProductToSaleCar(Product product) {
        setStatusFindingProduct(false);
        Float countRequested  =0f;
        if (product.uniMed.toLowerCase().equals("pz")) {
            countRequested= Float.parseFloat(String.valueOf(Math.round(Float.parseFloat(weightProductInput.getEditText().getText().toString()))));
            if (countRequested == 0f) {
                genericMessage("El producto se vende por piezas", "Introduce un número valido");
                return;
            }
        } else {
            countRequested= Float.parseFloat(String.valueOf(Math.round(Float.parseFloat(weightProductInput.getEditText().getText().toString()))));
            countRequested=Float.parseFloat(String.format("%.02f",countRequested*product.weightOriginal));
        }
        if (countRequested != null && countRequested > 0) {
            Float totalResguarded = 0f;
            for (ProductRoviandaToSale productSaved : carList) {
                if (productSaved.getKeySae().equals(product.productKey) && productSaved.getPresentationId().equals(product.presentationId)) {
                    totalResguarded += productSaved.getWeight();
                }
            }
            Float totalResguardedTemp = totalResguarded + countRequested;

            int index = -1;
            for (int i = 0; i < carList.size(); i++) {
                ProductRoviandaToSale item = carList.get(i);
                if (item.getKeySae().equals(product.productKey) && item.getPresentationId() == product.presentationId) {
                    index = i;
                    item.setWeight(totalResguardedTemp);//contiene la suma de los productos del mismo codigo y presentacion
                    item.setQuantity(item.getQuantity() + countRequested); // le suma la cantidad de producto solicitada a lo ya almacenado
                }
            }
            if (index == -1) {
                ProductRoviandaToSale productRoviandaToSale = new ProductRoviandaToSale();
                productRoviandaToSale.setIsPz(product.uniMed.toLowerCase().equals("pz"));
                productRoviandaToSale.setKeySae(product.productKey);
                productRoviandaToSale.setNameProduct(product.name);
                productRoviandaToSale.setPresentationType(product.presentationName);
                productRoviandaToSale.setPrice(product.price);
                productRoviandaToSale.setPresentationId(product.presentationId);
                productRoviandaToSale.setProductId(product.productId);
                productRoviandaToSale.setWeightOriginal(product.weightOriginal);
                productRoviandaToSale.setWeight(countRequested);
                productRoviandaToSale.setQuantity(countRequested);
                carList.add(productRoviandaToSale);
            }
            this.keyProductInput.getEditText().setText(null);
            this.weightProductInput.getEditText().setText(null);
            fillList();
        }
        this.keyProductInput.requestFocus();
    }

    private void fillList() {
        amount = 0f;
        listCarSale.removeAllViews();
        for (int i = 0; i < carList.size(); i++) {
            ProductRoviandaToSale product = carList.get(i);
            amount += product.getPrice() * product.getWeight();

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater.inflate(R.layout.item_list_product_sale, null);
            TextView code = (TextView) view.findViewById(R.id.productCode);
            TextView name = (TextView) view.findViewById(R.id.productName);
            TextView weight = (TextView) view.findViewById(R.id.productWeight);
            code.setText(product.getKeySae());
            name.setText(product.getNameProduct() + "\n" + product.getPresentationType());
            weight.setText(String.valueOf(product.getWeight()));
            final int index = i;
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    carList.remove(index);
                    fillList();
                    return true;
                }
            });
            listCarSale.addView(view);
        }
        amountTextView.setText("Total: " + amount.toString());
    }

    private void searchClient() {
        if (!clientInput.getEditText().getText().toString().isEmpty()) {
            String code = clientInput.getEditText().getText().toString();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                    clientSelected = appDatabase.clientDao().getClientByKey(Integer.parseInt(code), viewModelStore.getStore().getSellerId());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            findingClient = false;
                            fillClientData();
                        }
                    });
                }
            });
        } else {
            findingClient = false;
            clientInput.getEditText().setError("Campo no puede ir vacio");
        }
    }

    void fillClientData() {
        if (clientSelected != null) {
            clientKeySae.setTextColor(Color.parseColor("#18ed34"));
            clientKeySae.setText("Cliente: " + clientSelected.clientKey);
            clientName.setTextColor(Color.parseColor("#18ed34"));
            clientName.setText("Nombre cliente: " + clientSelected.name);
        } else {
            clientKeySae.setTextColor(Color.WHITE);
            clientKeySae.setText("Cliente: ");
            clientName.setTextColor(Color.WHITE);
            clientName.setText("Nombre cliente: ");
        }
    }

    void logout() {
        new MaterialAlertDialogBuilder(getContext()).setTitle("Cerrar sesión")
                .setMessage("¿Está seguro que desea cerrar sesión?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase conexion = AppDatabase.getInstance(getContext());
                        conexion.userDataInitialDao().updateAllLogedInFalse();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                presenter.doLogout();
                            }
                        });
                    }
                });

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutPicked = false;
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }

    public void goToAnotherSection(int option) {
        if (clientSelected != null || carList.size() > 0) {
            new MaterialAlertDialogBuilder(getContext()).setTitle("¿Seguro que desea salir de la sección?")
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    navigate(option);
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            }).show();

        } else {
            navigate(option);
        }
    }

    void navigate(int section) {
        if (section == 2) {
            goToVisits();
        } else if (section == 3 && sincronizateSession) {
            goToClient();
        } else if (section == 4 && sincronizateSession) {
            goToHistory();
        }
    }

    @Override
    public void goToLogin() {
        this.navController.navigate(HomeViewDirections.actionHomeViewToLoginView());
    }

    public void goToVisits() {
        this.navController.navigate(HomeViewDirections.actionHomeViewToVisitsView());
    }

    private void goToClient(){
        this.navController.navigate(HomeViewDirections.actionHomeViewToClientsView());
    }
    private void goToHistory(){
        this.navController.navigate(HomeViewDirections.actionHomeViewToHistoryView());
    }

    @Override
    public void showErrorConnectingPrinter() {
        new MaterialAlertDialogBuilder(getContext()).setTitle("Error de enlace")
                .setMessage("No se pudo enlazar a la impresora ").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        printerDisconnected();
                    }
                }).show();
    }

    @Override
    public void connectionPrinterSuccess(String printerName) {
        new MaterialAlertDialogBuilder(getContext()).setTitle("Enlace de impresora")
                .setMessage("Enlace exitoso con impresora : " + printerName).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                printerConnected();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                printerConnected();
            }
        }).show();
    }

    int intentsToClose = 0;

    @Override
    public void saleSuccess(String ticket) {
        intentsToClose = 0;
        clientSelected = null;
        clientName.setText("");
        clientKeySae.setText("");
        this.carList = new ArrayList<>();
        this.fillList();
        this.keyProductInput.getEditText().setText(null);
        this.weightProductInput.getEditText().setText(null);
        this.clientInput.getEditText().setText(null);
        printTiket(ticket);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setTitle("Imprimir ticket")
                .setNeutralButton("Terminar", null)
                .setPositiveButton("Reimprimir", null)
                .setCancelable(false).show();
        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setTextColor(Color.parseColor("#000000"));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //connectPrinter();
                printTiket(ticket);
            }
        });

        Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutral.setTextColor(Color.parseColor("#000000"));
        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
    }

    void printTiket(String ticket){
        Toast.makeText(getContext(),"Imprimiendo",Toast.LENGTH_LONG).show();
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    printerUtil.connectWithPrinter(printer);
                    sleep(3000);
                    printerUtil.IntentPrint(ticket);
                }catch (InterruptedException e){
                    System.out.println("Exception: "+e.getMessage());
                }
            }
        }.start();
    }


    @Override
    public void genericMessage(String title, String msg) {
        new MaterialAlertDialogBuilder(getContext()).setTitle(title)
                .setMessage(msg).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                }).show();
    }

    public void genericMessageForPresale(String title, String msg) {
        new MaterialAlertDialogBuilder(getContext()).setTitle(title).setCancelable(false)
                .setMessage(msg).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                findDataForPreSale(false);
            }
        }).show();
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
    public void completeSincronzation(SincronizationPreSaleResponse sincronizationPreSaleResponse) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                for(PreSaleSincronizedResponse preSalesSincronized : sincronizationPreSaleResponse.getPreSalesSincronized()){
                    PreSale preSale = appDatabase.preSaleDao().getByFolio(preSalesSincronized.getFolio());
                    if(preSale!=null){
                        preSale.sincronized=true;
                        appDatabase.preSaleDao().updateSale(preSale);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });

    }

    void getEndDayTicketOffline(String date) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Double weightG = Double.parseDouble("0");
                int totalTickets=0;
                String ticket = "\nReporte de cierre\nVendedor: "+viewModelStore.getStore().getUsername()+"\nFecha: "+date+"\n------------------------\n";
                ticket+="ART.   DESC    CANT    PRECIO  IMPORTE\n";
                Map<String,String> skus = new HashMap<>();
                Map<String,Float> pricesBySku = new HashMap<>();
                Map<String,Float> weightTotal = new HashMap<>();
                Map<String,Float> piecesTotal = new HashMap<>();
                Map<String,Float> amountTotal = new HashMap<>();
                Float efectivo=Float.parseFloat("0");

                String clientsStr="";
                String devolutionProducts="PRODUCTO DEVUELTO: \n";
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<PreSale> preSalesOfday = conexion.preSaleDao().getAllPreSalesByDate(date+"T00:00:00.000Z",date+"T23:59:59.000Z");
                for(PreSale preSale : preSalesOfday){
                    totalTickets++;

                    List<SubSale> subSales = conexion.subSalesDao().getSubSalesBySale(preSale.folio);

                    Float amountOfSale=0f;
                    amountOfSale=preSale.amount;

                        for (SubSale subSale : subSales) {
                            String productName = skus.get(subSale.productKey);
                            if (productName == null) {
                                skus.put(subSale.productKey,subSale.productName + " " + subSale.productPresentationType);
                            }
                            Float weight = weightTotal.get(subSale.productKey);
                            if (weight == null) {
                                Float weightByProduct = (subSale.uniMed.toLowerCase().equals("pz") ? subSale.quantity * subSale.weightStandar : subSale.quantity);
                                weightTotal.put(subSale.productKey, weightByProduct);
                                weightG += weightByProduct;
                            } else {
                                weight += (subSale.uniMed.toLowerCase().equals("pz") ? subSale.quantity * subSale.weightStandar : subSale.quantity);
                                weightTotal.put(subSale.productKey, weight);
                                weightG += (subSale.uniMed.toLowerCase().equals("pz") ? subSale.quantity * subSale.weightStandar : subSale.quantity);
                            }

                            Float amountByProduct = pricesBySku.get(subSale.productKey);
                            if (amountByProduct == null) {
                                amount = subSale.price / subSale.quantity;
                                pricesBySku.put(subSale.productKey, amount);
                            }

                            Float amountSubSale = amountTotal.get(subSale.productKey);
                            if (amountSubSale == null) {
                                Float amount = subSale.price;
                                amountTotal.put(subSale.productKey, amount);
                            }else{
                                amountSubSale+=subSale.price;
                                amountTotal.put(subSale.productKey, amountSubSale);
                            }
                            if(subSale.uniMed.toLowerCase().equals("pz")){
                                Float piecesOfProduct = piecesTotal.get(subSale.productKey);
                                if(piecesOfProduct==null){
                                    piecesTotal.put(subSale.productKey,subSale.quantity);
                                }else{
                                    piecesTotal.put(subSale.productKey,(piecesOfProduct+subSale.quantity));
                                }
                            }
                        }

                        clientsStr += "\n" + preSale.folio +((preSale.urgent!=null && preSale.urgent==true)?" URGENTE ":"")+ " " + preSale.clientName +(preSale.isTempKeyClient==true?" Temp:":" ")+preSale.keyClient+ "\n $" + amountOfSale + " ";

                        efectivo += amountOfSale;

                    }

                List<String> allSkus = new ArrayList<>();
                for(String sku : skus.keySet()){
                    allSkus.add(sku);
                }
                Collections.sort(allSkus, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
                for(String sku : allSkus){
                    String productName = skus.get(sku);
                    Float weight = weightTotal.get(sku);
                    Float price = pricesBySku.get(sku);
                    Float amount = amountTotal.get(sku);
                    Float totalPieces = piecesTotal.get(sku);
                    if(sku.length()>5){
                        sku=sku.substring(sku.length()-5,sku.length());
                    }
                    if(totalPieces==null) {
                        ticket += "\n" + sku + " " + productName + "\n" + String.format("%.02f", weight) + " $" + price + " $" + String.format("%.02f", amount);
                    }else{
                        ticket += "\n" + sku + " " + productName + "\n"+Math.round(totalPieces)+ " pz " + String.format("%.02f", weight) + " $" + price + " $" + String.format("%.02f", amount);
                    }
                }
                ticket+="\n-----------------------------------------\nDOC NOMBRE  CLIENTE IMPORTE TIPOVENTA\n-----------------------------------------\n";
                ticket+=clientsStr+"\n\n";
                ticket+="Total de notas: "+totalTickets+"\n";
                ticket+=devolutionProducts+"\n\n";
                ticket+="TOTAL DE PREVENTAS \nMONTO: $"+String.format("%.02f",efectivo)+"\n";
                ticket+="TOTAL KILOS: "+String.format("%.02f",weightG)+"\nVENTA TOTAL:$ "+String.format("%.02f",efectivo)+"\n";
                ticket+="\n\n\n\n";
                String ticketCreated = ticket;
                ///adeudos pendientes
                System.out.println("Ticket: "+ticket);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        circularProgressIndicator.setVisibility(View.INVISIBLE);
                        printTiket(ticketCreated);
                    }
                });
            }
        });
    }

    void showAddressToAssign(){
        if((clientSelected.latitude==null && clientSelected.longitude==null) || (clientSelected.latitude==0 && clientSelected.longitude==0)) {
            LocationTrackModel location = getLastKnownLocation();
            if (location != null) {
                presenter.getAddressByCoordenates(location.getLatitude(), location.getLongitude(),clientSelected);
            } else {
                onDialogNegativeClick();
            }
        }else{
            findDataForPreSale(false);
        }
    }

    void showModalMap(){
        DialogFragment mapDialogFragment = new MapDialogFragment(this);
        mapDialogFragment.setCancelable(false);
        mapDialogFragment.show(getActivity().getSupportFragmentManager(), "MapDialogFragment");
    }


    @Override
    public void onDialogPositiveClick(Double latitude,Double longitude) {
        presenter.getAddressByCoordenates(latitude,longitude,clientSelected);
    }

    @Override
    public void onDialogNegativeClick() {

        genericMessageForPresale("Infomación","Favor de asignar direccion al cliente mas tarde");
    }

    @Override
    public void setAddressForConfirm(AddressCoordenatesResponse addressForConfirm,Double latitude,Double longitude,Client client) {
        System.out.println("Show dialog");
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        builder.setTitle("Detalles del cliente");
        View view =layoutInflater.inflate(R.layout.modal_confirm_direcction,null);
        Button confirmButton = view.findViewById(R.id.confirmButton);
        Button changeButton = view.findViewById(R.id.changeButton);
        TextInputLayout customerNameField = view.findViewById(R.id.customerNameField);
        TextInputLayout customerStreetField = view.findViewById(R.id.customerStreetField);
        TextInputLayout customerMunicipalityField = view.findViewById(R.id.customerMunicipalityField);
        TextInputLayout customerSuburbField = view.findViewById(R.id.customerSuburbField);
        TextInputLayout customerNoExtField = view.findViewById(R.id.customerNoExtField);
        TextInputLayout customerCpField = view.findViewById(R.id.customerCpField);
        TextInputLayout customerContactField = view.findViewById(R.id.customerContactField);
        TextInputLayout customerPhoneField = view.findViewById(R.id.customerPhoneField);
        TextInputLayout customerReferenceField = view.findViewById(R.id.customerReferenceField);
        customerNameField.getEditText().setText(client.name);
        customerCpField.getEditText().setText(client.cp);
        customerMunicipalityField.getEditText().setText(client.municipality);
        customerStreetField.getEditText().setText(client.street);
        customerSuburbField.getEditText().setText(client.suburb);
        customerNoExtField.getEditText().setText(client.noExterior);
        customerContactField.getEditText().setText(client.contact);
        customerPhoneField.getEditText().setText(client.phoneNumber);
        customerReferenceField.getEditText().setText(client.reference);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                isPaying=false;
                onDialogNegativeClick();
            }
        });
        builder.setView(view);
        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String name=customerNameField.getEditText().getText().toString();
                String street = customerStreetField.getEditText().getText().toString();
                String municipality = customerMunicipalityField.getEditText().getText().toString();
                String suburb = customerSuburbField.getEditText().getText().toString();
                String noExt = customerNoExtField.getEditText().getText().toString();
                String cp = customerCpField.getEditText().getText().toString();
                String contact = customerContactField.getEditText().getText().toString();
                String phone = customerPhoneField.getEditText().getText().toString();
                String reference = customerReferenceField.getEditText().getText().toString();
                saveCurrentLocationToClient(name,street,municipality,suburb,noExt,cp,contact,phone,reference,latitude,longitude);
            }
        });
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showModalMap();
            }
        });
    }

    void saveCurrentLocationToClient(String name,String street,String municipality,String suburb,String noExt,String cp,String contact,String phone,String reference,Double latitude,Double longitude) {
        clientSelected.name=name;
        clientSelected.street=street;
        clientSelected.municipality=municipality;
        clientSelected.suburb=suburb;
        clientSelected.noExterior=noExt;
        clientSelected.cp=cp;
        clientSelected.contact=contact;
        clientSelected.phoneNumber=phone;
        clientSelected.reference=reference;
        clientSelected.latitude = latitude;
        clientSelected.longitude = longitude;
        clientSelected.sincronized = false;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                conexion.clientDao().updateClient(clientSelected);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        findDataForPreSale(false);
                    }
                });
            }
        });

    }

    @Override
    public void onDateSet(String date,DatePickerType datePickerType) {
        if(datePickerType.equals(DatePickerType.END_DAY)) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDateFormat = dateFormat.format(calendar.getTime());
            if (currentDateFormat.equals(date)) {
                checkAllVisitsForCurrentDay(currentDateFormat);
            } else {
                getEndDayTicketOffline(date);
            }
        }else if(datePickerType.equals(DatePickerType.CREATION_PRESALE)){
            dateForPresale = date;
            findDataForPreSale(true);
        }
    }

    @Override
    public void onDateCancel(DatePickerType datePickerType) {
        if(datePickerType.equals(DatePickerType.END_DAY)) {

        }else if(datePickerType.equals(DatePickerType.CREATION_PRESALE)){
            findDataForPreSale(true);
        }
    }

}
