package com.example.wpclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wpclone.Adapter.GrupKurmaSayfasiAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class GrupProfilActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    EditText editTextGrupAdi;
    TextView textViewGrupKatilimciAdlari;
    Button buttonGrupKur;
    String kisiler="";

    //Burada gruplardaki kişiler ve kişilerin olduğu gruplar diye iki kısım oluşturulacak


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup_profil);

        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user=auth.getCurrentUser();

        editTextGrupAdi=findViewById(R.id.editTextTextGrupAdi);
        textViewGrupKatilimciAdlari=findViewById(R.id.textViewGrupKatilimciAdlari);
        buttonGrupKur=findViewById(R.id.buttonGrupKur);

        Thread birinci=new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("kisiler").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(int i =0;i<GrupKurmaSayfasiAdapter.gruptaOlacakKisiler.size();i++){
                            kisiler+=GrupKurmaSayfasiAdapter.gruptaOlacakKisiler.get(i)+",";
                        }
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot data :task.getResult()){
                                if(auth.getCurrentUser().getPhoneNumber().equals(data.get("kisiTelNo"))){
                                    kisiler+=data.get("kisiAd");
                                }
                            }
                        }
                        textViewGrupKatilimciAdlari.setText(kisiler);
                    }
                });
            }
        });
        birinci.start();

    }
    public void grupKur_OnClick(View view){

        //Burada gruptaki katılımcıları firebase kaydediyoruz.
        Map<String,String> grupKatilimcilari=new HashMap<>();

        if(!editTextGrupAdi.getText().toString().isEmpty()){
            grupKatilimcilari.put("grupKatilimcilari",textViewGrupKatilimciAdlari.getText().toString());
            grupKatilimcilari.put("grupAdi",editTextGrupAdi.getText().toString());

            db.collection("GrupKatilimcilari").add(grupKatilimcilari).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(GrupProfilActivity.this, "Grup Başarıyla Kuruldu ", Toast.LENGTH_SHORT).show();
                    finish();//Burada kişiler sayfasına gidip arka plandaki tüm sayfaları kapatacağız.
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GrupProfilActivity.this, "Grup kurulurken bir hata ile karşılaşıldı", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(GrupProfilActivity.this, "Lütfen grupKatilimcilari adını boş bırakmayınız.", Toast.LENGTH_SHORT).show();
        }

        //Burada kişilerin dahil olduğu grular vardır.
        Map<String,String> kisiGruplari=new HashMap<>();

        if(!editTextGrupAdi.getText().toString().isEmpty()){
            String kisiler[]=textViewGrupKatilimciAdlari.getText().toString().split(",");
            System.out.println(kisiler.length);
            for(int i =0;i<kisiler.length; i++){

                int finalI = i;
                db.collection("kisiler").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot data : task.getResult()){
                                if(data.get("kisiAd").toString().equals(kisiler[finalI])){
                                    kisiGruplari.put("kisiTelNo",data.get("kisiTelNo").toString());
                                    kisiGruplari.put("kisiAd",kisiler[finalI]);
                                    kisiGruplari.put("grupAdi",editTextGrupAdi.getText().toString());
                                    db.collection("kisiGruplari").add(kisiGruplari).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(GrupProfilActivity.this, "Bir hata ile karşılaşıldı", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        }


    }
}