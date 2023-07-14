package com.example.wpclone.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wpclone.R;

import java.util.ArrayList;

public class GrupKurmaSayfasiAdapter extends RecyclerView.Adapter<GrupKurmaSayfasiAdapter.GrupKurmaSayfasiNesneleriTutucu>{

    ArrayList<String> kisiListesi;
    String kullaniciAdi;
    public static ArrayList<String> gruptaOlacakKisiler;

    public GrupKurmaSayfasiAdapter(ArrayList<String> kisiListesi, String kullaniciAdi) {
        this.kisiListesi = kisiListesi;
        this.kullaniciAdi = kullaniciAdi;
        gruptaOlacakKisiler=new ArrayList<>();
    }

    @NonNull
    @Override
    public GrupKurmaSayfasiNesneleriTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grup_kurma_satir,parent,false);
        return new GrupKurmaSayfasiNesneleriTutucu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupKurmaSayfasiNesneleriTutucu holder, @SuppressLint("RecyclerView") int position) {
        holder.textViewGrupKisiAdi.setText(kisiListesi.get(position));

        holder.checkBoxEklemeIslemi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                System.out.println("kişi ekleme işlemi ..."+isChecked);

                if(isChecked){
                    gruptaOlacakKisiler.add(kisiListesi.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return kisiListesi.size();
    }

    public class GrupKurmaSayfasiNesneleriTutucu extends RecyclerView.ViewHolder {
        TextView textViewGrupKisiAdi;
        CheckBox checkBoxEklemeIslemi;
        View grupKurmaSatiri;

        public GrupKurmaSayfasiNesneleriTutucu(@NonNull View itemView) {
            super(itemView);
            textViewGrupKisiAdi=itemView.findViewById(R.id.textViewGrupKisiAdi);
            checkBoxEklemeIslemi=itemView.findViewById(R.id.checkBoxEklemeIslemi);
            grupKurmaSatiri=itemView.findViewById(R.id.grupKurmaSatiri);
        }
    }



}
