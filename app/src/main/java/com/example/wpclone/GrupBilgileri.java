package com.example.wpclone;

import java.util.ArrayList;
import java.util.Comparator;
public class GrupBilgileri {

    public String gonderenTelNo;
    public String gonderenAd;
    public String mesaj;
    long zaman;
    public String resimBilgisi;

    public GrupBilgileri(String gonderenTelNo, String gonderenAd, String mesaj,long zaman,String resimBilgisi) {
        this.gonderenTelNo = gonderenTelNo;
        this.gonderenAd = gonderenAd;
        this.mesaj = mesaj;
        this.zaman=zaman;
        this.resimBilgisi=resimBilgisi;
    }
}
