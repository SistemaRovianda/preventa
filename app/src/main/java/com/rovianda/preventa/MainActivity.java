package com.rovianda.preventa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.rovianda.preventa.utils.ModeOfflineModel;
import com.rovianda.preventa.utils.SincronizationWorker;
import com.rovianda.preventa.utils.ViewModelStore;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewModelStore model = new ViewModelProvider(this).get(ViewModelStore.class);
        ModeOfflineModel modeOfflineModel = new ModeOfflineModel();
        modeOfflineModel.setUsername("Usuario de prueba");
        model.saveStore(modeOfflineModel);
        checkPermission();
        PeriodicWorkRequest synchronizationRovianda =
                new PeriodicWorkRequest.Builder(SincronizationWorker.class,PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS,PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,TimeUnit.MILLISECONDS)
                        .setBackoffCriteria(BackoffPolicy.LINEAR,PeriodicWorkRequest.MIN_BACKOFF_MILLIS,TimeUnit.MILLISECONDS)
                        .build();
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("roviPreSalesSync", ExistingPeriodicWorkPolicy.KEEP,synchronizationRovianda);
    }

    public void checkPermission()
    {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] { android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Permisos denegados",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return false;
        }else{
            return true;
        }
    }
}