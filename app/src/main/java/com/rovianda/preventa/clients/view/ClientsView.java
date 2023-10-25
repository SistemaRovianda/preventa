package com.rovianda.preventa.clients.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.rovianda.preventa.R;
import com.rovianda.preventa.clients.presenter.ClientsPresenter;
import com.rovianda.preventa.clients.presenter.ClientsPresenterContract;
import com.rovianda.preventa.utils.ViewModelStore;
import com.rovianda.preventa.utils.bd.AppDatabase;
import com.rovianda.preventa.utils.bd.entities.Client;
import com.rovianda.preventa.utils.models.ClientToEditData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientsView extends Fragment implements ClientsViewContract,View.OnClickListener{


    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private ViewModelStore viewModelStore;
    private TextView userNameTextView;
    private ImageView printerButton;
    private Button logoutButton,endDayButton;
    private MaterialButton newClientButton,searchClientButton;
    private ListView clientsList;
    private ClientsPresenterContract presenter;
    private TextInputLayout inputSearch;
    private boolean logoutPicked=false;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.clients,null);
        this.userNameTextView=view.findViewById(R.id.userName);
        this.navController = NavHostFragment.findNavController(this);
        this.bottomNavigationView = view.findViewById(R.id.bottom_navigation_client);
        this.bottomNavigationView.setSelectedItemId(R.id.cliente_section);
        this.printerButton = view.findViewById(R.id.printerButton);
        this.printerButton.setOnClickListener(this);
        this.printerButton.setVisibility(View.INVISIBLE);
        this.logoutButton = view.findViewById(R.id.Logout_button);
        this.logoutButton.setOnClickListener(this);
        this.logoutButton.setGravity(Gravity.RIGHT);
        this.newClientButton = view.findViewById(R.id.addNewClientButton);
        this.newClientButton.setOnClickListener(this);
        this.clientsList = view.findViewById(R.id.listaClientes);
        this.presenter = new ClientsPresenter(getContext(),this);
        this.searchClientButton = view.findViewById(R.id.buscarClienteButton);
        this.searchClientButton.setOnClickListener(this);
        this.inputSearch = view.findViewById(R.id.cliente_input_search);
        this.endDayButton = view.findViewById(R.id.end_day_button);
        this.endDayButton.setOnClickListener(this);
        this.endDayButton.setVisibility(View.GONE);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_section:
                        goToHome();
                        break;
                    case R.id.visitas_section:
                        goToVisits();
                        break;
                    case R.id.cliente_section:
                        // PANTALLA ACTUAL
                        break;
                    case R.id.history_section:
                        goToHistoryPreSales();
                        break;

                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void goToHome() {
        this.navController.navigate(ClientsViewDirections.actionClientsViewToHomeView());
    }

    @Override
    public void goToVisits() {
        this.navController.navigate(ClientsViewDirections.actionClientsViewToVisitsView());
    }

    @Override
    public void goToHistoryPreSales() {
        this.navController.navigate(ClientsViewDirections.actionClientsViewToHistoryView());
    }

    @Override
    public void goToLogin() {
        this.navController.navigate(ClientsViewDirections.actionClientsViewToLoginView());
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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.viewModelStore =  new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        this.userNameTextView.setText("Usuario: "+this.viewModelStore.getStore().getUsername());
        setClientsVisits("",false);
    }

    public void setClientsVisits(String hint,boolean filtered){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                String uid = viewModelStore.getStore().getSellerId();
                List<Client> clientsToVisit = appDatabase.clientDao().getClientsBySellerUid(uid);
                List<Client> clientsFiltered = new ArrayList<>();
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
                                    tv.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View view) {
                                            Client client = finalClientsFiltered.get(position);
                                            showModalToEdit(client);
                                            return false;
                                        }
                                    });
                                    return view;
                                }
                            };
                            clientsList.setAdapter(customAdapter);
                        }
                    }
                });
            }
        });
    }

    private void showModalToEdit(Client client){
        new MaterialAlertDialogBuilder(getContext()).setTitle("Editar cliente")
                .setMessage("¿Seguro que desea editar a este cliente ("+client.clientKey+")?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToEditClient(client.clientRovId,client.clientMobileId);
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buscarClienteButton:
                searchClient();
                break;
            case R.id.addNewClientButton:
                goToCreateClient();
                break;
            case R.id.Logout_button:
                if(!logoutPicked){
                    logoutPicked=true;
                    logout();
                }
                break;
        }
    }
    public void goToEditClient(Integer clientRovId,Integer clientMobileId){
        ClientToEditData clientToEditData =new ClientToEditData();
        clientToEditData.setRovId(clientRovId);
        clientToEditData.setMobileId(clientMobileId);
        clientToEditData.setLatitude(null);
        clientToEditData.setLongitude(null);
        viewModelStore.setClientToEditData(clientToEditData);
        this.navController.navigate(ClientsViewDirections.actionClientsViewToClientGeneralDataView());
    }

    void goToCreateClient(){
        this.navController.navigate(ClientsViewDirections.actionClientsViewToClientGeneralDataView());
    }

    private void searchClient(){
        String hint = this.inputSearch.getEditText().getText().toString();
        setClientsVisits(hint,true);
    }
}
