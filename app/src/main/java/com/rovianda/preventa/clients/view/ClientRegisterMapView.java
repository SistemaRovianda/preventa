package com.rovianda.preventa.clients.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rovianda.preventa.R;
import com.rovianda.preventa.utils.ViewModelStore;
import com.rovianda.preventa.utils.models.ClientToEditData;

public class ClientRegisterMapView extends Fragment implements View.OnClickListener,ClientRegisterMapViewContract, OnMapReadyCallback {


    private GoogleMap mMap;
    private Double latitude,longitude;
    private TextView latitudeTextView,longitudeTextView;
    private Button saveCoords,cancelCoords;
    private String username=null,action=null;
    private Integer currentClientId=null;
    private Integer currentClientRovId=null;
    private NavController navController;
    private ViewModelStore viewModelStore=null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client_register_map_view,container,false);
        latitudeTextView = view.findViewById(R.id.latitudeTextView);
        longitudeTextView = view.findViewById(R.id.longitudeTextView);
        saveCoords = view.findViewById(R.id.saveCoords);
        saveCoords.setOnClickListener(this);
        cancelCoords=view.findViewById(R.id.cancelCoord);
        cancelCoords.setOnClickListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.navController = NavHostFragment.findNavController(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        ClientToEditData clientToEditData = viewModelStore.getClientToEditData();
            if(clientToEditData!=null) {
                if (clientToEditData.getLatitude() != null && clientToEditData.getLongitude() != null) {
                    System.out.println("Latitud: " + clientToEditData.getLatitude());
                    System.out.println("Longitud: " + clientToEditData.getLongitude());
                    latitudeTextView.setText("Latitud: " + clientToEditData.getLatitude());
                    longitudeTextView.setText("Longitud: " + clientToEditData.getLongitude());
                    latitude = clientToEditData.getLatitude();
                    longitude = clientToEditData.getLongitude();
                }
            }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saveCoords:
                backToDetails();
                break;
            case R.id.cancelCoord:
                cancel();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(latitude!=null && longitude!=null) {
            LatLng clientLocation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(clientLocation)
                    .title("Ubicación del cliente"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clientLocation, 15));
        }else{
            mMap.clear();
            LatLng orizaba = new LatLng(18.849463,-97.098212);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(orizaba,15));
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Ubicación del cliente");
                latitudeTextView.setText("Latitud: "+latLng.latitude);
                longitudeTextView.setText("Longitud: "+latLng.longitude);
                latitude=latLng.latitude;
                longitude=latLng.longitude;
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);
            }
        });
    }

    @Override
    public void backToDetails() {
        ClientToEditData clientToEditData = viewModelStore.getClientToEditData();
        if(clientToEditData==null) {
            clientToEditData = new ClientToEditData();
        }
        if(latitude!=null && longitude!=null){
        clientToEditData.setLatitude(latitude);
        clientToEditData.setLongitude(longitude);
        viewModelStore.setClientToEditData(clientToEditData);
        }

       this.navController.navigate(ClientRegisterMapViewDirections.actionClientRegisterMapViewToClientGeneralDataView());
    }

    @Override
    public void cancel() {
        ClientToEditData clientToEditData = viewModelStore.getClientToEditData();
        if(clientToEditData!=null){
            clientToEditData.setLatitude(null);
            clientToEditData.setLongitude(null);
        }
        this.navController.navigate(ClientRegisterMapViewDirections.actionClientRegisterMapViewToClientGeneralDataView());
    }
}
