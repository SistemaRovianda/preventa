package com.rovianda.preventa.utils;

import androidx.lifecycle.ViewModel;

import com.rovianda.preventa.utils.bd.AppDatabase;
import com.rovianda.preventa.utils.bd.entities.UserDataInitial;
import com.rovianda.preventa.utils.models.ClientToEditData;

public class ViewModelStore extends ViewModel {
    private UserDataInitial userDataInitial;
    private ClientToEditData clientToEditData;

    public ClientToEditData getClientToEditData() {
        return clientToEditData;
    }

    public void setClientToEditData(ClientToEditData clientToEditData) {
        this.clientToEditData = clientToEditData;
    }

    public ModeOfflineModel getModeOfflineModel() {
        return modeOfflineModel;
    }

    public void setModeOfflineModel(ModeOfflineModel modeOfflineModel) {
        this.modeOfflineModel = modeOfflineModel;
    }

    public void setUserDataInitial(UserDataInitial userDataInitial){
        this.userDataInitial = userDataInitial;
    }
    public UserDataInitial getUserDataInitial(){
        return this.userDataInitial;
    }
    private ModeOfflineModel modeOfflineModel;
    public ModeOfflineModel getStore(){
        return  this.modeOfflineModel;
    }

    public  void saveStore(ModeOfflineModel modeOfflineModel){
        this.modeOfflineModel = modeOfflineModel;
    }

}
