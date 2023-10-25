package com.rovianda.preventa.login.view;

public interface LoginViewPresenter {
    void goToHome();
    void setLoginData(String nameUser, String uid);
    void setEmailInputError(String msg);
    void setPasswordInputError(String msg);
    void disableButtonLogin(Boolean disable);
    void modalMessageOperation(String msg);
    boolean validateInputs();
}
