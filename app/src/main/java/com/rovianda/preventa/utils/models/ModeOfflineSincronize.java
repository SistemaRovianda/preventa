package com.rovianda.preventa.utils.models;

import java.util.List;

public class ModeOfflineSincronize {

    private List<ModeOfflineSM> salesMaked;
    private List<ModeOfflineS> sales;


    public List<ModeOfflineSM> getSalesMaked() {
        return salesMaked;
    }

    public void setSalesMaked(List<ModeOfflineSM> salesMaked) {
        this.salesMaked = salesMaked;
    }

    public List<ModeOfflineS> getSales() {
        return sales;
    }

    public void setSales(List<ModeOfflineS> sales) {
        this.sales = sales;
    }

}
