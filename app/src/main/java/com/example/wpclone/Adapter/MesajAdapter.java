package com.example.wpclone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wpclone.KisilerMesaj;
import com.example.wpclone.R;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class MesajAdapter extends RecyclerView.Adapter<MesajAdapter.MesajNesneTutucu>{

    String gonderen,alan;
    List<KisilerMesaj> kisilerMesajList;

    public MesajAdapter(List<KisilerMesaj> kisilerMesajList,String gonderen,String alan) {
        this.kisilerMesajList = kisilerMesajList;
        this.alan=alan;
        this.gonderen=gonderen;
    }

    @NonNull
    @Override
    public MesajNesneTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.mesaj_layout,parent,false);
        return new MesajNesneTutucu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MesajNesneTutucu holder, @SuppressLint("RecyclerView") int position) {

        if(kisilerMesajList.get(position).alanTelNo.equals(alan)){
            if(kisilerMesajList.get(position).resimBilgisi.equals("var")){
                //resim varsa yapılacak işlemler...
                holder.textViewMesajGelen.setVisibility(View.GONE);
                holder.textViewMesajGiden.setVisibility(View.GONE);
                holder.imageViewGelen.setVisibility(View.GONE);
                Picasso.get().load(kisilerMesajList.get(position).mesaj).into(holder.imageViewGiden);
            }else{
                //Resim yoksa yapılacak işlemler...
                holder.textViewMesajGelen.setVisibility(View.GONE);
                holder.imageViewGelen.setVisibility(View.GONE);
                holder.imageViewGiden.setVisibility(View.GONE);
                holder.textViewMesajGiden.setText(kisilerMesajList.get(position).mesaj);

            }
        }else{
            if(kisilerMesajList.get(position).resimBilgisi.equals("var")){
                //Resim varsa ...
                holder.textViewMesajGelen.setVisibility(View.GONE);
                holder.textViewMesajGelen.setVisibility(View.GONE);
                holder.imageViewGiden.setVisibility(View.GONE);
                Picasso.get().load(kisilerMesajList.get(position).mesaj).into(holder.imageViewGelen);

            }else {
                //Resim yoksa
                holder.textViewMesajGiden.setVisibility(View.GONE);
                holder.imageViewGelen.setVisibility(View.GONE);
                holder.imageViewGiden.setVisibility(View.GONE);
                holder.textViewMesajGelen.setText(kisilerMesajList.get(position).mesaj);

            }
        }
    }

    @Override
    public int getItemCount() {
        return kisilerMesajList.size();
    }

    class MesajNesneTutucu extends RecyclerView.ViewHolder{
        TextView textViewMesajGelen;
        ImageView imageViewGiden;
        ImageView imageViewGelen;
        TextView textViewMesajGiden;
        public MesajNesneTutucu(@NonNull View itemView) {
            super(itemView);
            textViewMesajGelen=itemView.findViewById(R.id.textViewMesajGelen);
            textViewMesajGiden=itemView.findViewById(R.id.textViewMesajGiden);
            imageViewGiden=itemView.findViewById(R.id.imageViewGiden);
            imageViewGelen=itemView.findViewById(R.id.imageViewGelen);
        }
    }

}
