package com.rovianda.preventa.visits.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.rovianda.preventa.R;
import com.rovianda.preventa.visits.models.ClientVisitListItem;
import com.rovianda.preventa.visits.view.VisitMapView;
import com.rovianda.preventa.visits.view.VisitMapViewContract;

import java.util.List;

public class ClientVisitListItemAdapter extends BaseAdapter {
    Context context;
    List<ClientVisitListItem> visits;
    VisitMapViewContract mainView;
    public ClientVisitListItemAdapter(Context appContext, List<ClientVisitListItem> visits, VisitMapView visitsMapView){
        this.context=appContext;
        this.visits=visits;
        this.mainView = visitsMapView;
    }

    @Override
    public int getCount() {
        return this.visits.size();
    }

    @Override
    public Object getItem(int position) {
        return this.visits.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View view= layoutInflater.inflate(R.layout.item_list_client_visit, null);
        CheckedTextView checkedTextView = view.findViewById(R.id.checkedTextView);
        String addText ="";
        ClientVisitListItem client = this.visits.get(position);

        if(client.getLatitude()==null && client.getLongitude()==null){
            addText="- S/Ubic.";
        }
        checkedTextView.setText(client.getClientName()+addText);
        checkedTextView.setChecked(client.isSelected());
        if(client.isVisited()) {
            checkedTextView.setTextColor(Color.parseColor("#6FEA74"));
        }else{
            checkedTextView.setTextColor(Color.RED);
            if(!client.isAvailableForRecordOfVisit()){
                checkedTextView.setTextColor(Color.YELLOW);
            }
        }

        checkedTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mainView.setSelection(position,client.isVisited(),client.isAvailableForRecordOfVisit());
                return false;
            }
        });
        return  view;
    }

}


