package com.rovianda.preventa.clients.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.rovianda.preventa.R;
import com.rovianda.preventa.clients.presenter.ClientGeneralDataPresenter;
import com.rovianda.preventa.clients.presenter.ClientGeneralDataPresenterContract;
import com.rovianda.preventa.utils.ViewModelStore;
import com.rovianda.preventa.utils.bd.AppDatabase;
import com.rovianda.preventa.utils.bd.entities.Client;
import com.rovianda.preventa.utils.models.AddressCoordenatesResponse;
import com.rovianda.preventa.utils.models.ClientToEditData;
import com.rovianda.preventa.utils.models.ClientV2Request;
import com.rovianda.preventa.utils.models.ClientV2Response;
import com.rovianda.preventa.utils.models.ClientV2UpdateRequest;
import com.rovianda.preventa.utils.models.ClientV2UpdateResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientGeneralDataView extends Fragment implements ClientGeneralDataViewContract,View.OnClickListener{


    private ViewModelStore viewModelStore;
    private ClientToEditData clientToEditData=null;

    private LocationManager locManager;
    private Location loc;

    private TextInputLayout customerNameField, customerStreetField, customerMunicipalityField, customerSuburbField, customerNoExtField, customerCpField,customerContactField,customerPhoneField,customerReferenceField;
    private ClientGeneralDataPresenterContract presenter;
    private Button registerButton, cancelButton,goToMap;
    private NavController navController;
    private Double currentLatitude = null;
    private Double currentLongitude = null;
    private Integer currentClientMobileId=null;
    private Integer currentClientRovId=null;
    private CheckBox checkBoxModay, checkBoxTuesday, checkBoxWednesday, checkBoxThursday, checkBoxFriday, checkBoxSaturday;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    private TextView latitudText, longitudText;
    private boolean isLoading = false;

    private boolean coordsEdited=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client_generaldata,null);
        this.customerNameField = view.findViewById(R.id.customerNameField);
        this.customerStreetField = view.findViewById(R.id.customerStreetField);
        this.customerMunicipalityField = view.findViewById(R.id.customerMunicipalityField);
        this.customerSuburbField = view.findViewById(R.id.customerSuburbField);
        this.customerNoExtField = view.findViewById(R.id.customerNoExtField);
        this.customerCpField = view.findViewById(R.id.customerCpField);
        this.customerContactField = view.findViewById(R.id.customerContactField);
        this.customerPhoneField = view.findViewById(R.id.customerPhoneField);
        this.customerReferenceField = view.findViewById(R.id.customerReferenceField);
        this.registerButton = view.findViewById(R.id.registerButton);
        this.registerButton.setOnClickListener(this);
        this.cancelButton = view.findViewById(R.id.cancelButton);
        this.cancelButton.setOnClickListener(this);
        this.checkBoxModay = view.findViewById(R.id.checkBoxModay);
        this.checkBoxTuesday = view.findViewById(R.id.checkBoxTuesday);
        this.checkBoxWednesday = view.findViewById(R.id.checkBoxWednesday);
        this.checkBoxThursday = view.findViewById(R.id.checkBoxThursday);
        this.checkBoxFriday = view.findViewById(R.id.checkBoxFriday);
        this.checkBoxSaturday = view.findViewById(R.id.checkBoxSaturday);

        this.goToMap = view.findViewById(R.id.goToMap);
        this.goToMap.setOnClickListener(this);
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        this.presenter = new ClientGeneralDataPresenter(getContext(), this) ;

        this.navController = NavHostFragment.findNavController(this);
        this.latitudText = view.findViewById(R.id.latitudText);
        this.longitudText = view.findViewById(R.id.longitudText);
        this.latitudText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getCoordenates();
                return true;
            };
        });
        return view;
    }

    private void fillData() {
        if(currentClientRovId!=null && currentClientRovId!=0) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase conexion = AppDatabase.getInstance(getContext());
                    Client client= conexion.clientDao().getClientBydId(currentClientRovId);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            customerNameField.getEditText().setText(client.name);
                            customerStreetField.getEditText().setText(client.street);
                            customerMunicipalityField.getEditText().setText(client.municipality);
                            customerSuburbField.getEditText().setText(client.suburb);
                            if (client.noExterior != null) {
                                customerNoExtField.getEditText().setText(client.noExterior);
                            }
                            customerCpField.getEditText().setText(client.cp);
                            customerContactField.getEditText().setText(client.contact);
                            customerPhoneField.getEditText().setText(client.phoneNumber);
                            customerReferenceField.getEditText().setText(client.reference);
                            checkBoxModay.setChecked(client.monday);
                            checkBoxTuesday.setChecked(client.tuesday);
                            checkBoxWednesday.setChecked(client.wednesday);
                            checkBoxThursday.setChecked(client.thursday);
                            checkBoxFriday.setChecked(client.friday);
                            checkBoxSaturday.setChecked(client.saturday);
                            if (client.latitude != null) {
                                latitudText.setText("Latitud: " + client.latitude);
                                if(!coordsEdited) {
                                    currentLatitude = client.latitude;
                                    latitudText.setText("Latitud: " + currentLatitude);
                                }
                            }
                            if (client.longitude != null) {
                                longitudText.setText("Longitud: " + client.longitude);
                                if(!coordsEdited) {
                                    currentLongitude = client.longitude;
                                    longitudText.setText("Latitud: " + currentLongitude);
                                }
                            }
                        }
                    });
                }
            });
        }else{
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase conexion = AppDatabase.getInstance(getContext());
                    Client client=conexion.clientDao().getClientByClientIdMobile(currentClientMobileId);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            customerNameField.getEditText().setText(client.name);
                            customerStreetField.getEditText().setText(client.street);
                            customerMunicipalityField.getEditText().setText(client.municipality);
                            customerSuburbField.getEditText().setText(client.suburb);
                            if (client.noExterior != null) {
                                customerNoExtField.getEditText().setText(client.noExterior);
                            }
                            customerCpField.getEditText().setText(client.cp);
                            customerContactField.getEditText().setText(client.contact);
                            customerPhoneField.getEditText().setText(client.phoneNumber);
                            customerReferenceField.getEditText().setText(client.reference);
                            checkBoxModay.setChecked(client.monday);
                            checkBoxTuesday.setChecked(client.tuesday);
                            checkBoxWednesday.setChecked(client.wednesday);
                            checkBoxThursday.setChecked(client.thursday);
                            checkBoxFriday.setChecked(client.friday);
                            checkBoxSaturday.setChecked(client.saturday);
                            if (client.latitude != null) {
                                latitudText.setText("Latitud: " + client.latitude);
                                if(!coordsEdited) {
                                    currentLatitude = client.latitude;
                                    latitudText.setText("Latitud: " + currentLatitude);
                                }
                            }
                            if (client.longitude != null) {
                                longitudText.setText("Longitud: " + client.longitude);
                                if(!coordsEdited) {
                                    currentLongitude = client.longitude;
                                    longitudText.setText("Latitud: " + currentLongitude);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancelButton:
                goBackToClients();
                break;
            case R.id.goToMap:
                goToMap();
                break;
            case R.id.registerButton:
                if(currentClientRovId==null && currentClientMobileId==null){
                    saveNewClient();
                }else{
                    editClientByType();
                }
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        this.clientToEditData = this.viewModelStore.getClientToEditData();
        if(this.clientToEditData!=null){
            this.currentClientMobileId = this.clientToEditData.getMobileId();
            this.currentClientRovId = this.clientToEditData.getRovId();
            if(this.clientToEditData.getLatitude()!=null && this.clientToEditData.getLongitude()!=null) {
                this.coordsEdited=true;
                this.currentLatitude = this.clientToEditData.getLatitude();
                this.currentLongitude = this.clientToEditData.getLongitude();
                System.out.println("coordenadas seleccionadas");
                System.out.println("latitud: "+currentLatitude);
                System.out.println("longitud: "+currentLongitude);
                getCoordenates();
            }else {
                fillData();
            }
        }else{
            getCoordenates();
        }
    }

    void getCoordenates() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            loc =getLastKnownLocation();
            this.showModalCoordenates();
            this.isLoading = true;
            if(coordsEdited){
                if(loc==null){
                    loc=new Location("");
                }
                loc.setLatitude(currentLatitude);
                loc.setLongitude(currentLongitude);
            }
            if(loc!=null) {
                this.presenter.getAddressByCoordenates(loc.getLatitude(), loc.getLongitude());
            }else{
                closeModalLoadingCoords();
                showModalCoordenatesCustom("Error", "No se pudo obtener información de localización.",false);
            }
            return;
        }
    }
    AlertDialog modalLocation=null;
    void showModalCoordenates() {
        this.isLoading = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.client_modal_coordenates, null);
        builder.setCancelable(false);
        builder.setView(view);
        modalLocation= builder.show();
        Button acceptButtonModalCoordeantes = view.findViewById(R.id.acceptCoordenatesButton);
        acceptButtonModalCoordeantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalLocation.dismiss();
            }
        });

    }

    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                l = locationManager.getLastKnownLocation(provider);
            }

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }


    @Override
    public void setAddressByCoordenates(AddressCoordenatesResponse addressCoordenatesResponse, Double latitude, Double longitude) {
        System.out.println("Street: "+addressCoordenatesResponse.getStreet());
        System.out.println("Street: "+addressCoordenatesResponse.getMunicipality());
        System.out.println("Street: "+addressCoordenatesResponse.getSuburb());
        System.out.println("Street: "+addressCoordenatesResponse.getCp());
        this.customerStreetField.getEditText().setText(addressCoordenatesResponse.getStreet());
        this.customerMunicipalityField.getEditText().setText(addressCoordenatesResponse.getMunicipality());
        this.customerSuburbField.getEditText().setText(addressCoordenatesResponse.getSuburb());
        this.customerCpField.getEditText().setText(addressCoordenatesResponse.getCp());
        this.customerContactField.getEditText().setText(null);
        this.customerPhoneField.getEditText().setText(null);
        this.customerReferenceField.getEditText().setText(null);
        this.currentLatitude=latitude;
        this.currentLongitude=longitude;
        String msg = "Latitud: "+latitude+" Longitud: "+longitude;
        this.latitudText.setText("Latitud: "+latitude);
        this.longitudText.setText("Longitud: "+longitude);
        showModalCoordenatesCustom("Localizado",msg,false);
    }

    @Override
    public void showModalCoordenatesCustom(String title, String msg,boolean forClose) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.client_modal_coordenates, null);
        ProgressBar progressBarModalCoordenates = view.findViewById(R.id.coodenatesModalProgress);
        TextView coordenatesModalTitle = view.findViewById(R.id.coodenatesModalTitle);
        TextView coordenatesModalMsg = view.findViewById(R.id.coordenatesModalMsg);
        builder.setCancelable(true);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button acceptButtonModalCoordeantes = view.findViewById(R.id.acceptCoordenatesButton);
        acceptButtonModalCoordeantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if(forClose){
                    goBackToClients();
                }
            }
        });
        coordenatesModalTitle.setText(title);
        coordenatesModalMsg.setText(msg);
        progressBarModalCoordenates.setVisibility(View.GONE);
    }

    @Override
    public void cannotTakeCoordenatesInfo() {
        showModalCoordenatesCustom("Localización fallo","No se pudo obtener la información de ubicación.",false);
    }

    @Override
    public void goToMap() {
        if(currentLatitude!=null && currentLongitude!=null){
            ClientToEditData clientToEditData = new ClientToEditData();
            clientToEditData.setLatitude(currentLatitude);
            clientToEditData.setLongitude(currentLongitude);
            clientToEditData.setMobileId(currentClientMobileId);
            clientToEditData.setRovId(currentClientRovId);
            viewModelStore.setClientToEditData(clientToEditData);
        }
        this.navController.navigate(ClientGeneralDataViewDirections.actionClientGeneralDataViewToClientRegisterMapView());
    }

    @Override
    public void goBackToClients() {
        viewModelStore.setClientToEditData(null);
        this.navController.navigate(ClientGeneralDataViewDirections.actionClientGeneralDataViewToClientsView());
    }

    public boolean validFields(){
        if(!this.customerNameField.getEditText().getText().toString().isEmpty()
                && !this.customerStreetField.getEditText().getText().toString().isEmpty()
                && !this.customerMunicipalityField.getEditText().getText().toString().isEmpty()
                && !this.customerSuburbField.getEditText().getText().toString().isEmpty()
                && !this.customerCpField.getEditText().getText().toString().isEmpty()
                && (this.checkBoxModay.isChecked() || this.checkBoxTuesday.isChecked() || this.checkBoxWednesday.isChecked() || this.checkBoxThursday.isChecked() || this.checkBoxFriday.isChecked() || this.checkBoxSaturday.isChecked()) ){
            return true;
        }else{
            return false;
        }
    }

    AlertDialog modalRegisteringUpdating=null;
    void showModalRegisteringUpdating(String title,String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.client_modal_coordenates, null);
        ProgressBar progressBarModalCoordenates = view.findViewById(R.id.coodenatesModalProgress);
        TextView coordenatesModalTitle = view.findViewById(R.id.coodenatesModalTitle);
        coordenatesModalTitle.setText(title);
        TextView coordenatesModalMsg = view.findViewById(R.id.coordenatesModalMsg);
        coordenatesModalMsg.setText(msg);
        builder.setCancelable(false);
        builder.setView(view);
        this.modalRegisteringUpdating = builder.create();
        this.modalRegisteringUpdating.show();
        Button acceptButtonModalCoordeantes = view.findViewById(R.id.acceptCoordenatesButton);
        acceptButtonModalCoordeantes.setVisibility(View.GONE);
    }

    private void saveNewClient(){
        boolean isValid = validFields();
        if(!isValid){
            if(this.checkBoxModay.isChecked() || this.checkBoxTuesday.isChecked() || this.checkBoxWednesday.isChecked() || this.checkBoxThursday.isChecked() || this.checkBoxFriday.isChecked() || this.checkBoxSaturday.isChecked()){
                showModalCoordenatesCustom("Faltan dias de visita","Favor de seleccionar dias de visita",false);
            } else{
                showModalCoordenatesCustom("Faltan campos","Llenar los campos obligatorios",false);
            }
        }else{
            this.isLoading=true;
            showModalRegisteringUpdating("Registrando","Enviando registro a servidor");
            // is valid
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase conexion=AppDatabase.getInstance(getContext());
                    Client lastClient = conexion.clientDao().getLastClient();
                    int lastKey = 1;
                    if(lastClient!=null){
                        lastKey = lastClient.clientKey + 1;
                    }
                    Client client = new Client();
                    client.isTemp=true;
                    client.name=customerNameField.getEditText().getText().toString();
                    client.clientKeyTemp=lastKey;
                    client.clientKey=lastKey;
                    client.clientMobileId=lastKey;
                    client.clientRovId=null;
                    client.street=customerStreetField.getEditText().getText().toString();
                    client.type="CONTADO";
                    client.cp=customerCpField.getEditText().getText().toString();
                    client.municipality=customerMunicipalityField.getEditText().getText().toString();
                    client.suburb=customerSuburbField.getEditText().getText().toString();
                    client.contact = customerContactField.getEditText().getText()!=null?customerContactField.getEditText().getText().toString():null;
                    client.phoneNumber= customerPhoneField.getEditText().getText()!=null?customerPhoneField.getEditText().getText().toString():null;
                    client.reference = customerReferenceField.getEditText().getText()!=null?customerReferenceField.getEditText().getText().toString():null;
                    client.latitude=currentLatitude;
                    client.longitude=currentLongitude;
                    client.monday=checkBoxModay.isChecked();
                    client.tuesday=checkBoxTuesday.isChecked();
                    client.wednesday=checkBoxWednesday.isChecked();
                    client.thursday=checkBoxThursday.isChecked();
                    client.friday=checkBoxFriday.isChecked();
                    client.saturday=checkBoxSaturday.isChecked();
                    client.sincronized=false;
                    client.registeredInMobile=true;
                    client.noExterior=customerNoExtField.getEditText().getText().toString();
                    client.uid=viewModelStore.getStore().getSellerId();
                    client.estatus="ACTIVE";
                    conexion.clientDao().insertClient(client);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ClientV2Request clientV2Request = new ClientV2Request();
                            clientV2Request.setClientCp(client.cp);
                            clientV2Request.setClientName(client.name);
                            clientV2Request.setClientRovId(client.clientRovId);
                            clientV2Request.setClientMobileId(client.clientMobileId);
                            clientV2Request.setClientType(client.type);
                            clientV2Request.setClientStreet(client.street);
                            clientV2Request.setClientSuburb(client.suburb);
                            clientV2Request.setClientMunicipality(client.municipality);
                            clientV2Request.setClientExtNumber(client.noExterior);
                            clientV2Request.setClientSellerUid(viewModelStore.getStore().getSellerId());
                            clientV2Request.setClientContact(client.contact);
                            clientV2Request.setClientPhoneNumber(client.phoneNumber);
                            clientV2Request.setClientReference(client.reference);
                            clientV2Request.setTemp(client.isTemp);
                            if(client.latitude!=null){
                                clientV2Request.setLatitude(client.latitude);
                            }
                            if(client.longitude!=null){
                                clientV2Request.setLongitude(client.longitude);
                            }
                            clientV2Request.setMonday(client.monday);
                            clientV2Request.setTuesday(client.tuesday);
                            clientV2Request.setWednesday(client.wednesday);
                            clientV2Request.setThursday(client.thursday);
                            clientV2Request.setFriday(client.friday);
                            clientV2Request.setSaturday(client.saturday);
                            presenter.tryRegisterClient(clientV2Request);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void failConnectionServiceEdit() {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase conexion = AppDatabase.getInstance(getContext());
                    Client client=null;
                    if(currentClientRovId!=null) {
                        client = conexion.clientDao().getClientBydId(currentClientRovId);
                    }else {
                        client = conexion.clientDao().getClientByClientIdMobile(currentClientMobileId);
                    }
                    if(client!=null) {
                        client.sincronized = false;
                        conexion.clientDao().updateClient(client);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            });

    }

    void editClientByType() {
        boolean isValid = validFields();
        if (!isValid) {
            if (this.checkBoxModay.isChecked() || this.checkBoxTuesday.isChecked() || this.checkBoxWednesday.isChecked() || this.checkBoxThursday.isChecked() || this.checkBoxFriday.isChecked() || this.checkBoxSaturday.isChecked()) {
                showModalCoordenatesCustom("Faltan dias de visita", "Favor de seleccionar dias de visita",false);
            } else {
                showModalCoordenatesCustom("Faltan campos", "Llenar los campos obligatorios",false);
            }
        } else {
            showModalRegisteringUpdating("Actualizando","Enviando registro a servidor");
            this.isLoading = true;
            System.out.println("ClientMobileId: "+currentClientMobileId);
            System.out.println("ClientRovId: "+currentClientRovId);
            if(currentClientRovId!=null){
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase conexion = AppDatabase.getInstance(getContext());
                        Client client=conexion.clientDao().getClientBydId(currentClientRovId);
                        client.name = customerNameField.getEditText().getText().toString();
                        client.street = customerStreetField.getEditText().getText().toString();
                        client.cp = customerCpField.getEditText().getText().toString();
                        client.municipality = customerMunicipalityField.getEditText().getText().toString();
                        client.suburb = customerSuburbField.getEditText().getText().toString();
                        client.contact = customerContactField.getEditText().getText()!=null?customerContactField.getEditText().getText().toString():null;
                        client.phoneNumber= customerPhoneField.getEditText().getText()!=null?customerPhoneField.getEditText().getText().toString():null;
                        client.reference = customerReferenceField.getEditText().getText()!=null?customerReferenceField.getEditText().getText().toString():null;
                        client.latitude = currentLatitude;
                        client.longitude = currentLongitude;
                        client.monday = checkBoxModay.isChecked();
                        client.tuesday = checkBoxTuesday.isChecked();
                        client.wednesday = checkBoxWednesday.isChecked();
                        client.thursday = checkBoxThursday.isChecked();
                        client.friday = checkBoxFriday.isChecked();
                        client.saturday = checkBoxSaturday.isChecked();
                        client.sincronized = false;
                        client.noExterior = customerNoExtField.getEditText().getText().toString();
                        conexion.clientDao().updateClient(client);

                        List<ClientV2UpdateRequest> clientV2UpdateRequestList = new ArrayList<>();
                        ClientV2UpdateRequest clientV2UpdateRequest = new ClientV2UpdateRequest();
                        clientV2UpdateRequest.setClientId(currentClientRovId);
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

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                presenter.updateCustomerV2(clientV2UpdateRequestList);
                            }
                        });
                    }
                });
            }else{
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase conexion = AppDatabase.getInstance(getContext());
                        Client client =conexion.clientDao().getClientByClientIdMobile(currentClientMobileId);
                        client.name = customerNameField.getEditText().getText().toString();
                        client.street = customerStreetField.getEditText().getText().toString();
                        client.cp = customerCpField.getEditText().getText().toString();
                        client.municipality = customerMunicipalityField.getEditText().getText().toString();
                        client.suburb = customerSuburbField.getEditText().getText().toString();
                        client.contact = customerContactField.getEditText().getText()!=null?customerContactField.getEditText().getText().toString():null;
                        client.phoneNumber= customerPhoneField.getEditText().getText()!=null?customerPhoneField.getEditText().getText().toString():null;
                        client.reference = customerReferenceField.getEditText().getText()!=null?customerReferenceField.getEditText().getText().toString():null;
                        client.latitude = currentLatitude;
                        client.longitude = currentLongitude;
                        client.monday = checkBoxModay.isChecked();
                        client.tuesday = checkBoxTuesday.isChecked();
                        client.wednesday = checkBoxWednesday.isChecked();
                        client.thursday = checkBoxThursday.isChecked();
                        client.friday = checkBoxFriday.isChecked();
                        client.saturday = checkBoxSaturday.isChecked();
                        client.sincronized = false;
                        client.noExterior = customerNoExtField.getEditText().getText().toString();
                        conexion.clientDao().updateClient(client);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                closeModal();
                                showModalCoordenatesCustom("Registro completado con detalles","Se registro y guardo el cliente en telefono, pero no se envio a servidor.",true);
                            }
                        });
                    }
                });
            }

        }
    }

    @Override
    public void closeModal() {
        if(this.modalRegisteringUpdating!=null && this.modalRegisteringUpdating.isShowing()){
            this.modalRegisteringUpdating.dismiss();
        }
    }

    @Override
    public void closeModalLoadingCoords(){
        if(this.modalLocation!=null && this.modalLocation.isShowing()){
            this.modalLocation.dismiss();
        }
    }

    @Override
    public void updateClientInServer(List<ClientV2UpdateResponse> clientV2UpdateResponse) {
        this.isLoading=true;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion=AppDatabase.getInstance(getContext());
                for(ClientV2UpdateResponse clientV2UpdateResponse1 : clientV2UpdateResponse) {
                    Client client = conexion.clientDao().getClientBydId(clientV2UpdateResponse1.getClientId());
                    if (client != null) {
                        client.sincronized = true;
                        conexion.clientDao().updateClient(client);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isLoading=false;
                    }
                });
            }
        });
    }

    @Override
    public void updateClientRegisteredInServer(ClientV2Response clientV2Response) {
        this.isLoading=true;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion=AppDatabase.getInstance(getContext());
                Client client = conexion.clientDao().getClientByClientIdMobile(clientV2Response.getClientMobileId());
                if(client!=null) {
                    client.sincronized = true;
                    client.isTemp=false;
                    client.clientRovId = clientV2Response.getClientId();
                    client.clientKey=clientV2Response.getClientId();
                    client.clientKeyTemp=clientV2Response.getClientId();
                    conexion.clientDao().updateClient(client);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isLoading=false;
                    }
                });
            }
        });
    }
}
