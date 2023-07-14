package com.example.wpclone;

import java.util.Comparator;

public class KisilerMesaj  {
    public String gonderenTelNo;
    public String alanTelNo;
    public long zaman;
    public String mesaj;
    public String resimBilgisi;

    public KisilerMesaj(String gonderenTelNo, String alanTelNo, long zaman, String mesaj,String resimBilgisi) {
        this.gonderenTelNo = gonderenTelNo;
        this.alanTelNo = alanTelNo;
        this.zaman = zaman;
        this.mesaj = mesaj;
        this.resimBilgisi=resimBilgisi;
    }


}
