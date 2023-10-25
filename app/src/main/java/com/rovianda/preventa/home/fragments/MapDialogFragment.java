package com.rovianda.preventa.home.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rovianda.preventa.R;
import com.rovianda.preventa.home.view.HomeView;
import com.rovianda.preventa.home.view.HomeViewContract;

public class MapDialogFragment extends DialogFragment implements OnMapReadyCallback {

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
    }

    private Double latitude,longitude;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.clear();
        LatLng orizaba = new LatLng(18.849463,-97.098212);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(orizaba,15));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Ubicación del cliente");
                latitude=latLng.latitude;
                longitude=latLng.longitude;
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.addMarker(markerOptions);
            }
        });
    }

    HomeViewContract viewM;

    public MapDialogFragment(HomeView viewM){
        this.viewM = viewM;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_location_selector_modal,null);
        Button cancelButton = view.findViewById(R.id.modalMapCancelButton);
        Button confirmButton = view.findViewById(R.id.modalMapConfirmButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewM.onDialogNegativeClick();
                dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewM.onDialogPositiveClick(latitude,longitude);
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.map, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
    }
}
