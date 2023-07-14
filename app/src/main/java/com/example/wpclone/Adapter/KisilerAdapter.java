package com.example.wpclone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wpclone.KisilerActivity;
import com.example.wpclone.MesajActivity;
import com.example.wpclone.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class KisilerAdapter extends RecyclerView.Adapter<KisilerAdapter.KisilerNesneleriTutucu>{

    FirebaseFirestore db;
    FirebaseAuth auth;
    List<String> kisilerArryList;
    List<String> kisilerNumaraArryList;
    Context mContext;

    public KisilerAdapter(List<String> kisilerArryList,List<String> kisilerNumaraArryList ,Context mContext) {
        this.kisilerArryList = kisilerArryList;
        this.mContext=mContext;
        this.kisilerNumaraArryList=kisilerNumaraArryList;
    }

    public KisilerAdapter() {
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        kisilerArryList=new ArrayList<>();

        db.collection("kisiler").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot document:queryDocumentSnapshots){
                    System.out.println(document.get("kisiAd") +" " +document.get("kisiTelNo"));
                    kisilerArryList.add((String) document.get("kisiAd"));
                }

            }
        });
    }

    @NonNull
    @Override
    public KisilerNesneleriTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.kisiler_layout,parent,false);

        return new KisilerNesneleriTutucu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KisilerNesneleriTutucu holder, @SuppressLint("RecyclerView") int position) {
        holder.textViewKisiAdlari.setText(kisilerArryList.get(position));
        holder.kisilerLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, MesajActivity.class);
                //Gönderilen kişi bilgisinin gönderilmesi.
                intent.putExtra("kisiNo",kisilerNumaraArryList.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kisilerArryList.size();
    }

    public class KisilerNesneleriTutucu extends RecyclerView.ViewHolder {
        TextView textViewKisiAdlari;
        LinearLayout kisilerLinearLayout;
        public KisilerNesneleriTutucu(@NonNull View itemView) {
            super(itemView);
            textViewKisiAdlari=itemView.findViewById(R.id.textViewKisiAdlari);
            kisilerLinearLayout=itemView.findViewById(R.id.kisilerLinearLayout);
        }
    }

}
