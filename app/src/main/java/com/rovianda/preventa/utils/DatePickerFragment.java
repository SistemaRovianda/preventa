package com.rovianda.preventa.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.rovianda.preventa.history.view.HistoryView;
import com.rovianda.preventa.history.view.HistoryViewContract;
import com.rovianda.preventa.home.DatePickerType;
import com.rovianda.preventa.home.view.HomeView;
import com.rovianda.preventa.home.view.HomeViewContract;
import com.rovianda.preventa.visits.view.VisitsView;
import com.rovianda.preventa.visits.view.VisitsViewContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private HomeViewContract viewM;
    private HistoryViewContract historyViewM;
    private VisitsViewContract visitsViewM;
    private boolean isDateSet=false;
    private String dateSelected;
    private DatePickerType datePickerType;
    public DatePickerFragment(HomeView viewM, DatePickerType datePickerType){
        this.viewM=viewM;
        this.datePickerType=datePickerType;
    }

    public DatePickerFragment(HistoryView viewM, DatePickerType datePickerType){
        this.historyViewM=viewM;
        this.datePickerType=datePickerType;
    }

    public DatePickerFragment(VisitsView viewM, DatePickerType datePickerType){
        this.visitsViewM=viewM;
        this.datePickerType=datePickerType;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstance){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog= new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);
        if(this.datePickerType.equals(DatePickerType.CREATION_PRESALE)) {
            dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        }
        return dialog;
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        String monthStr = String.valueOf(month + 1);
        String day = String.valueOf(dayOfMonth);
        if ((month + 1) < 10) monthStr = "0" + monthStr;
        if (dayOfMonth < 10) day = "0" + day;
        this.dateSelected = year + "-" + monthStr + "-" + day;
        this.isDateSet=true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        System.out.println("Dissmiss: "+this.isDateSet+" "+this.datePickerType.toString());
        if(isDateSet){
            if(this.datePickerType.equals(DatePickerType.CHANGE_DATE)){
                historyViewM.onDateSet(this.dateSelected,this.datePickerType);
            }else if(this.datePickerType.equals(DatePickerType.RESINCRONIZATION)) {
                visitsViewM.onDateSet(this.dateSelected,this.datePickerType);
            }else if(this.datePickerType.equals(DatePickerType.END_DAY) || this.datePickerType.equals(DatePickerType.CREATION_PRESALE)){
                viewM.onDateSet(this.dateSelected,this.datePickerType);
            }
        }else{
            if(this.datePickerType.equals(DatePickerType.CHANGE_DATE)){
                historyViewM.onDateCancel();
            }else if(this.datePickerType.equals(DatePickerType.RESINCRONIZATION)) {
                visitsViewM.onDateCancel();
            }else if(this.datePickerType.equals(DatePickerType.END_DAY) || this.datePickerType.equals(DatePickerType.CREATION_PRESALE)){
                viewM.onDateCancel(this.datePickerType);
            }
        }
    }
}
