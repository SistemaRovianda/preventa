package com.rovianda.preventa.login.view;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;


import com.google.android.material.textfield.TextInputEditText;
import com.rovianda.preventa.R;
import com.rovianda.preventa.login.presenter.LoginPresenter;
import com.rovianda.preventa.utils.ViewModelStore;
import com.rovianda.preventa.utils.bd.AppDatabase;
import com.rovianda.preventa.utils.bd.entities.UserDataInitial;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginView extends Fragment implements LoginViewPresenter,View.OnClickListener{

    private TextInputEditText emailInput,passwordInput;
    private ProgressBar progressBar;
    private Button Loginbutton;
    private LoginPresenter presenter;
    ViewModelStore viewModelStore=null;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    private NavController navController=null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loginfragment,null);

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        progressBar = view.findViewById(R.id.progressBar);
        Loginbutton = view.findViewById(R.id.Loginbutton);
        Loginbutton.setOnClickListener(this);

        this.presenter = new LoginPresenter(getContext(),this);
        navController = NavHostFragment.findNavController(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        this.checkIfAlreadyLogedIn();
    }


    @Override
    public void goToHome() {
        navController.navigate(LoginViewDirections.actionLoginViewToHomeView());
    }

    @Override
    public void setLoginData(String nameUser, String uid) {
        this.viewModelStore.getStore().setSellerId(uid);
        this.viewModelStore.getStore().setUsername(nameUser);

    }


    @Override
    public void setEmailInputError(String msg) {
        this.emailInput.setError(msg);
    }

    @Override
    public void setPasswordInputError(String msg) {
        this.passwordInput.setError(msg);
    }



    @Override
    public void disableButtonLogin(Boolean disable) {
        if(disable){
            this.Loginbutton.setVisibility(View.GONE);
            this.Loginbutton.setEnabled(false);
            this.progressBar.setVisibility(View.VISIBLE);
        }else{
            this.Loginbutton.setVisibility(View.VISIBLE);
            this.Loginbutton.setEnabled(true);
            this.progressBar.setVisibility(View.GONE);
        }
    }



    @Override
    public void modalMessageOperation(String msg){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.modal_success_operation,null);
        TextView messageLoad = view.findViewById(R.id.message_load);
        Button btnAccept = view.findViewById(R.id.acceptButtonModal);
        messageLoad.setText(msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog modalInfo= builder.create();
        modalInfo.show();
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalInfo.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Loginbutton:
                    if(validateInputs()){
                        login();
                    }
                break;
        }
    }
    void login(){
        disableButtonLogin(true);
        // revision offline
        checkCredentialsOffline(this.emailInput.getText().toString(),this.passwordInput.getText().toString());
    }
    void checkCredentialsOffline(String email,String password){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitial= conexion.userDataInitialDao().getByEmailAndPasswordOffline("%"+email+"%","%"+password+"%");
                if(userDataInitial!=null) {
                    conexion.userDataInitialDao().updateAllLogedInTrue(userDataInitial.uid);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(userDataInitial!=null){
                            disableButtonLogin(false);
                            viewModelStore.getStore().setSellerId(userDataInitial.uid);
                            viewModelStore.getStore().setUsername(userDataInitial.name);
                            goToHome();
                        }else{
                            // si no esta offline, se requiere login por red
                            presenter.doLogin(email,password);
                        }
                    }
                });
            }
        });
    }
    void checkIfAlreadyLogedIn(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<UserDataInitial> userDataInitial = conexion.userDataInitialDao().getAnyLogedIn();
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        if(userDataInitial.size()>0) {
                            setLoginData(userDataInitial.get(0).name,userDataInitial.get(0).uid);
                            goToHome();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean validateInputs() {
        boolean valid=true;
        if(emailInput.getText().toString().trim().isEmpty()){
            setEmailInputError("El email no puede ser vacio.");
            valid=false;
        }
        if(passwordInput.getText().toString().isEmpty()){
            setPasswordInputError("La contraseña no puede ser vacia.");
            valid=false;
        }
        return valid;
    }
}
