package com.example.wpclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.wpclone.Adapter.GrupAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GrupActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;
    RecyclerView grupRecyclerView;
    FloatingActionButton floatingActionButtonGrupEkle;
    ArrayList <String> grupArrayList;
    GrupAdapter adapter;


    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user=auth.getCurrentUser();
        grupArrayList=new ArrayList<>();
        floatingActionButtonGrupEkle=findViewById(R.id.floatingActionButtonGrupEkle);
        grupRecyclerView=findViewById(R.id.grupReyclerView);

        adapter=new GrupAdapter(grupArrayList,user.getPhoneNumber(),GrupActivity.this);
        grupRecyclerView.setLayoutManager(new LinearLayoutManager(GrupActivity.this));
        grupRecyclerView.setHasFixedSize(true);
        grupRecyclerView.setAdapter(adapter);

        db.collection("kisiGruplari").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot data : value) {
                    //Grup içerisindeki kişilerin çekileceği alan...
                    if (data.get("kisiTelNo").toString().equals(user.getPhoneNumber())) {
                        grupArrayList.add(data.get("grupAdi").toString());

                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        floatingActionButtonGrupEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GrupActivity.this,GrupKurmaActivity.class);
                startActivity(intent);
            }
        });
    }
}