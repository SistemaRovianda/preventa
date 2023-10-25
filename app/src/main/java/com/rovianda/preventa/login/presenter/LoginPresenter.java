package com.rovianda.preventa.login.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.rovianda.preventa.login.view.LoginView;
import com.rovianda.preventa.login.view.LoginViewPresenter;
import com.rovianda.preventa.utils.Constants;
import com.rovianda.preventa.utils.GsonRequest;
import com.rovianda.preventa.utils.models.UserDetails;

import java.util.HashMap;
import java.util.Map;

public class LoginPresenter implements LoginPresenterContract {
    Context context;
    LoginViewPresenter view;
    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url = Constants.URL;
    private RequestQueue requestQueue;
    private FirebaseAuth firebaseAuth;
    public LoginPresenter(Context context, LoginView loginView){
        this.context=context;
        this.view=loginView;
        this.firebaseAuth = FirebaseAuth.getInstance();
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser = new Gson();
    }

    @Override
    public void doLogin(String emailStr,String passwordStr) {
                this.firebaseAuth.signInWithEmailAndPassword(emailStr.toLowerCase().trim(), passwordStr.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            getDetailsOfUser(task.getResult().getUser().getUid());
                        } else {
                            if(task.getException()!=null && task.getException().getMessage().contains("A network error")) {
                                view.disableButtonLogin(false);
                                view.modalMessageOperation("Error de red");
                            }else {
                                view.disableButtonLogin(false);
                                view.modalMessageOperation("Error con las credenciales");
                            }
                        }
                    }
                });
    }

    void getDetailsOfUser(String uid){
        Map<String,String> headers = new HashMap<>();
        GsonRequest<UserDetails> presentationsgGet = new GsonRequest<UserDetails>
                (url+"/rovianda/user/"+uid, UserDetails.class,headers,
                        new Response.Listener<UserDetails>(){
                            @Override
                            public void onResponse(UserDetails response) {
                                view.disableButtonLogin(false);
                                if(response.getRol().equals("PRESALE")){
                                    view.setLoginData(response.getName(),uid);
                                    view.goToHome();
                                }else{
                                    view.modalMessageOperation("Usuario no autorizado/Rol incorrecto");
                                }
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.disableButtonLogin(false);
                        view.modalMessageOperation("El usuario no existe en el sistema");
                    }
                }   , Request.Method.GET,null
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
                view.disableButtonLogin(false);
                view.modalMessageOperation(error.getMessage());
            }
        });
    }



}
