package com.example.wpclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.wpclone.Adapter.GrupKurmaSayfasiAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GrupKurmaActivity extends AppCompatActivity {
    GrupKurmaSayfasiAdapter adapter;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;
    RecyclerView recyclerViewGrupKurma;
    ArrayList<String> kisilerArrayList;
    ArrayList<String> kisilerNumaraArrayList;
    String kisiAd="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup_kurma);
        //Burada grup kurulurken seçilecek olan kişilerin seçim ve kayıt işlemleri yapılacaktır.

        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user=auth.getCurrentUser();
        kisilerArrayList=new ArrayList<>();
        kisilerNumaraArrayList=new ArrayList<>();
        recyclerViewGrupKurma=findViewById(R.id.grupKurmaSayfasiRecyclerView);


        db.collection("kisiler").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        if(!user.getPhoneNumber().equals(document.get("kisiTelNo"))){
                            kisilerArrayList.add((String) document.get("kisiAd"));
                            kisilerNumaraArrayList.add((String) document.get("kisiTelNo"));
                        }else{
                            kisiAd+=(String) document.get("kisiAd");
                            System.out.println("Kullanıcı adi"+kisiAd);
                        }
                        System.out.println(document.get("kisiAd"));
                        System.out.println(document.get("kisiTelNo"));
                        adapter.notifyDataSetChanged();
                    }

                }else {
                    Toast.makeText(GrupKurmaActivity.this, "Bir hata ile karşılaşıldı", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Kişi bilgisini sayfa geçişinde verilecek...
        adapter=new GrupKurmaSayfasiAdapter(kisilerArrayList,kisiAd);
        recyclerViewGrupKurma.setLayoutManager(new LinearLayoutManager(GrupKurmaActivity.this));
        recyclerViewGrupKurma.setHasFixedSize(true);
        recyclerViewGrupKurma.setAdapter(adapter);
        adapter.notifyDataSetChanged();



    }

    //Burada ise menu tanımlaması yapılmaktadır.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grup_kurma_sayfasi_menu, menu);
        return true;
    }

    //Buradaki işlemle menü üzerindeki seçeneklerden hangisi seçildiğini ve seçilen işlem ne işe yaraadığını anlatır.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuGrupKur:
                //Grup sayfasına gidilecektir.
                Intent intent=new Intent(GrupKurmaActivity.this,GrupProfilActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuIptal:
                //Profilden çıkış yapma işlemi yapılacak.
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }





}