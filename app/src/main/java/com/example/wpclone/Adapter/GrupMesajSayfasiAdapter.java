package com.example.wpclone.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wpclone.GrupBilgileri;
import com.example.wpclone.KisilerActivity;
import com.example.wpclone.R;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GrupMesajSayfasiAdapter extends RecyclerView.Adapter<GrupMesajSayfasiAdapter.GrupMesajNesneleriTutucu>{

    List<GrupBilgileri> grupMesajArrayList;
    String kullaniciAdi;

    public GrupMesajSayfasiAdapter(List<GrupBilgileri> grupMesajArrayList, String kullaniciAdi) {
        this.grupMesajArrayList = grupMesajArrayList;
        this.kullaniciAdi = kullaniciAdi;
    }

    @NonNull
    @Override
    public GrupMesajNesneleriTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.grup_mesaj_satir,parent,false);
        return new GrupMesajNesneleriTutucu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupMesajNesneleriTutucu holder, @SuppressLint("RecyclerView") int position) {
        Thread thread;
        if(grupMesajArrayList.get(position).gonderenAd.equals(KisilerActivity.telefonKullaniciAdi)){

            if(grupMesajArrayList.get(position).resimBilgisi.equals("var")){
                holder.textViewGidenMesaj.setVisibility(View.GONE);
                holder.textViewGelenMesaj.setVisibility(View.GONE);
                holder.imageViewGrupResimGelen.setVisibility(View.GONE);
                holder.textViewGelenAdi.setVisibility(View.GONE);
                Picasso.get().load(grupMesajArrayList.get(position).mesaj).into(holder.imageViewGrupResimGiden);

            }else{
                holder.textViewGidenMesaj.setText(grupMesajArrayList.get(position).mesaj);
                holder.textViewGelenMesaj.setVisibility(View.GONE);
                holder.textViewGelenAdi.setVisibility(View.GONE);
                holder.imageViewGrupResimGelen.setVisibility(View.GONE);
                holder.imageViewGrupResimGiden.setVisibility(View.GONE);
            }
        }else {
            if(grupMesajArrayList.get(position).resimBilgisi.equals("var")){
                holder.textViewGidenMesaj.setVisibility(View.GONE);
                holder.textViewGelenMesaj.setVisibility(View.GONE);
                holder.imageViewGrupResimGiden.setVisibility(View.GONE);
                holder.textViewGonderenAdi.setVisibility(View.GONE);
                holder.textViewGelenAdi.setText(grupMesajArrayList.get(position).gonderenAd);
                Picasso.get().load(grupMesajArrayList.get(position).mesaj).into(holder.imageViewGrupResimGelen);
            }else {
                holder.imageViewGrupResimGelen.setVisibility(View.GONE);
                holder.imageViewGrupResimGiden.setVisibility(View.GONE);
                holder.textViewGidenMesaj.setVisibility(View.GONE);
                holder.textViewGonderenAdi.setVisibility(View.GONE);
                holder.textViewGelenMesaj.setText(grupMesajArrayList.get(position).mesaj);
                holder.textViewGelenAdi.setText(grupMesajArrayList.get(position).gonderenAd);
            }

        }
    }

    @Override
    public int getItemCount() {
        return grupMesajArrayList.size();
    }

    class GrupMesajNesneleriTutucu  extends RecyclerView.ViewHolder {
        TextView textViewGelenAdi;
        TextView textViewGonderenAdi;
        TextView textViewGelenMesaj;
        TextView textViewGidenMesaj;
        ImageView imageViewGrupResimGelen,imageViewGrupResimGiden;
        public GrupMesajNesneleriTutucu(@NonNull View itemView) {
            super(itemView);
            textViewGidenMesaj=itemView.findViewById(R.id.textViewGidenMesaj);
            textViewGelenAdi=itemView.findViewById(R.id.textViewGelenAdi);
            textViewGonderenAdi=itemView.findViewById(R.id.textViewGonderenAdi);
            textViewGelenMesaj=itemView.findViewById(R.id.textViewGelenMesaj);
            imageViewGrupResimGelen=itemView.findViewById(R.id.imageViewGrupResimGelen);
            imageViewGrupResimGiden=itemView.findViewById(R.id.imageViewGrupResimGiden);
        }
    }


}
