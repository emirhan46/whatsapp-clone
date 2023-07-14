package com.example.wpclone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wpclone.GrupBilgileri;
import com.example.wpclone.GrupMesajActivity;
import com.example.wpclone.R;

import java.util.ArrayList;

public class GrupAdapter extends RecyclerView.Adapter<GrupAdapter.GrupSatirNesneTutucu> {
    ArrayList<String> grupAdlari;
    String kullaniciNumarasi;
    Context mContext;

    public GrupAdapter(ArrayList<String> grupAdlari, String kullaniciNumarasi,Context mContext) {
        this.grupAdlari = grupAdlari;
        this.kullaniciNumarasi = kullaniciNumarasi;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public GrupSatirNesneTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grup_adi_satir,parent,false);
        return new GrupSatirNesneTutucu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupSatirNesneTutucu holder, @SuppressLint("RecyclerView") int position) {
        holder.textViewGrupAdi.setText(grupAdlari.get(position));
        holder.layoutGrupSatir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, GrupMesajActivity.class);
                intent.putExtra("grupAdi",grupAdlari.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return grupAdlari.size();
    }

    public class GrupSatirNesneTutucu extends RecyclerView.ViewHolder {

        private TextView textViewGrupAdi;
        ConstraintLayout layoutGrupSatir;

        public GrupSatirNesneTutucu(@NonNull View itemView) {
            super(itemView);
            textViewGrupAdi=itemView.findViewById(R.id.textViewGrupAdi);
            layoutGrupSatir=itemView.findViewById(R.id.grupAdiLayout);
        }
    }

}
