package com.example.wpclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class KayitActivity extends AppCompatActivity {

    Button buttonGirisYap;
    EditText editTextKullaniciAdi;
    FirebaseAuth auth;
    FirebaseFirestore db ;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);

        buttonGirisYap=findViewById(R.id.buttonGirisYap);
        editTextKullaniciAdi=findViewById(R.id.editTextKullaniciAdi);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user= auth.getInstance().getCurrentUser();


        db.collection("kisiler").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String kisiAd=document.get("kisiAd").toString();
                        String kisiTelNo=document.get("kisiTelNo").toString();
                        if(user.getPhoneNumber().equals(kisiTelNo)){
                            editTextKullaniciAdi.setText(kisiAd);
                            Intent intent=new Intent(KayitActivity.this,KisilerActivity.class);
                            startActivity(intent);
                        }
                    }
                }else{
                    Toast.makeText(KayitActivity.this, "Bir hata ile karşılaşıldı.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void kullaniciGirisi_OnClick(View view){
        Map<String , Object> kisiler=new HashMap<>();
        String kisiTelNo=auth.getCurrentUser().getPhoneNumber();

        if (editTextKullaniciAdi.getText().toString().trim()==""){
            Toast.makeText(KayitActivity.this, "Lütfen bir kullanıcı adı giriniz.", Toast.LENGTH_SHORT).show();
        }else {
            String kisiAd=editTextKullaniciAdi.getText().toString();
            kisiler.put("kisiTelNo",kisiTelNo);
            kisiler.put("kisiAd",kisiAd);

            db.collection("kisiler").add(kisiler).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    //İşlem başarıyla sonlanırsa...
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //İşlem yapılırken bir hata çıktıysa
                    Toast.makeText(KayitActivity.this, "Bir hata çıktı lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                }
            });





            Intent intent=new Intent(KayitActivity.this,KisilerActivity.class);
            startActivity(intent);
        }
    }
}