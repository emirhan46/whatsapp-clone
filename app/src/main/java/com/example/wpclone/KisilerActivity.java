package com.example.wpclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.wpclone.Adapter.KisilerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class KisilerActivity extends AppCompatActivity {
    public static String telefonKullaniciAdi;
    FirebaseAuth auth;
    FirebaseFirestore db;
    RecyclerView kisilerRecyclerView;
    KisilerAdapter adapter;
    List<String> kisilerArrayList;
    List<String> kisilerNumaraArryList;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kisiler);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user=auth.getCurrentUser();

        kisilerArrayList=new ArrayList<>();
        kisilerNumaraArryList=new ArrayList<>();

        kisilerRecyclerView=findViewById(R.id.kisilerRecyclerView);

        db.collection("kisiler").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        if(!user.getPhoneNumber().equals(document.get("kisiTelNo"))){
                            kisilerArrayList.add((String) document.get("kisiAd"));
                            kisilerNumaraArryList.add((String) document.get("kisiTelNo"));
                        }else {
                            telefonKullaniciAdi=document.get("kisiAd").toString();
                        }
                        System.out.println(document.get("kisiAd"));
                        System.out.println(document.get("kisiTelNo"));
                        adapter.notifyDataSetChanged();
                    }

                }else {
                    Toast.makeText(KisilerActivity.this, "Bir hata ile karşılaşıldı", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter=new KisilerAdapter(kisilerArrayList,kisilerNumaraArryList,KisilerActivity.this);
        kisilerRecyclerView.setLayoutManager(new LinearLayoutManager(KisilerActivity.this));
        kisilerRecyclerView.setHasFixedSize(true);
        kisilerRecyclerView.setAdapter(adapter);
    }

    //Burada ise menu tanımlaması yapılmaktadır.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.giris_menu, menu);
        return true;
    }

    //Buradaki işlemle menü üzerindeki seçeneklerden hangisi seçildiğini ve seçilen işlem ne işe yaraadığını anlatır.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.grupSayfasi:
                //Grup sayfasına gidilecektir.
                Intent intent=new Intent(KisilerActivity.this,GrupActivity.class);
                startActivity(intent);
                return true;
            case R.id.cikis:
                //Profilden çıkış yapma işlemi yapılacak.
                auth.signOut();
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}