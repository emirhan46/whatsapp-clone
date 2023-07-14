package com.example.wpclone;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wpclone.Adapter.GrupMesajSayfasiAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrupMesajActivity extends AppCompatActivity {
//Burada gruptaki mesajlaşma işlemini yapacak yapı
    long zaman;
    Timestamp timestamp;
    String gelenGrupAdi,kullaniciAdi;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseStorage storage;
    RecyclerView recyclerViewGrupMesaj;
    Button buttonGrupMesajGonder;
    Button buttonGrupResimGonder;
    EditText editTextGrupMesaji;
    List<GrupBilgileri> grupMesajArrayList;
    GrupMesajSayfasiAdapter adapter;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    UploadTask uploadTask;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup_mesaj);
        grupMesajArrayList =new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();

        recyclerViewGrupMesaj=findViewById(R.id.recyclerViewGrupMesaj);
        buttonGrupMesajGonder=findViewById(R.id.buttonGrupMesajGonder);
        buttonGrupResimGonder=findViewById(R.id.buttonGrupResimGonder);
        editTextGrupMesaji=findViewById(R.id.editTextGrupMesaji);


        Intent gelenVeri=getIntent();
        gelenGrupAdi=gelenVeri.getStringExtra("grupAdi");


        grupMesajGuncelle();

        galeriIslemleri();

        db.collection("kisiler").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot data :task.getResult()){
                        if(user.getPhoneNumber().equals(data.get("kisiTelNo"))){
                            kullaniciAdi=data.get("kisiAd").toString();
                        }
                    }
                }
            }
        });

        System.out.println(gelenGrupAdi);

        adapter=new GrupMesajSayfasiAdapter(grupMesajArrayList,kullaniciAdi);
        recyclerViewGrupMesaj.setHasFixedSize(true);
        recyclerViewGrupMesaj.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewGrupMesaj.setAdapter(adapter);

    }

    public void grupMesajGuncelle(){

        db.collection(gelenGrupAdi).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    grupMesajArrayList.clear();
                    for(QueryDocumentSnapshot data: value){
                        String gonderenTelNo=data.get("gondericiTelNo").toString();
                        String mesaj=data.get("mesaj").toString();
                        String gonderenAd=data.get("gonderenAd").toString();
                        long gelenZaman=Long.decode(data.get("zaman").toString());
                        String resimBilgisi=data.get("resimBilgisi").toString();
                        GrupBilgileri grupBilgileri=new GrupBilgileri(gonderenTelNo,gonderenAd,mesaj,gelenZaman,resimBilgisi);
                        grupMesajArrayList.add(grupBilgileri);
                    }
                System.out.println(grupMesajArrayList.size());
                   grupMesajArrayList.sort(new Comparator<GrupBilgileri>() {
                       @Override
                       public int compare(GrupBilgileri o1, GrupBilgileri o2) {
                           return (o1.zaman < o2.zaman) ? -1 : ((o1.zaman == o2.zaman) ? 0 :1 );
                       }
                   });

                adapter=new GrupMesajSayfasiAdapter(grupMesajArrayList,kullaniciAdi);
                recyclerViewGrupMesaj.setHasFixedSize(true);
                recyclerViewGrupMesaj.setLayoutManager(new LinearLayoutManager(GrupMesajActivity.this));
                recyclerViewGrupMesaj.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void grupMesajGonder_OnClick(View view){

        Map<String,String> gonderilenGrupMesaj=new HashMap<>();
        timestamp= Timestamp.now();//Firebaseden zamanı çekerek farklı cihazlarda da uyum sağlamasını sağlamış olduk.
        zaman=timestamp.getSeconds();

        if(!editTextGrupMesaji.getText().toString().isEmpty()){
            gonderilenGrupMesaj.put("gondericiTelNo",user.getPhoneNumber());
            gonderilenGrupMesaj.put("mesaj",editTextGrupMesaji.getText().toString().trim());
            gonderilenGrupMesaj.put("gonderenAd",kullaniciAdi);
            gonderilenGrupMesaj.put("resimBilgisi","yok");
            gonderilenGrupMesaj.put("zaman", String.valueOf(zaman));

            db.collection(gelenGrupAdi).add(gonderilenGrupMesaj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    adapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GrupMesajActivity.this, "Bir hata ile karşılasıldı", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "Lütfen bir mesaj giriniz.", Toast.LENGTH_SHORT).show();
        }
        editTextGrupMesaji.setText("");
        adapter.notifyDataSetChanged();

    }

    public void grupResimGonder_OnClick(View view){
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                //.setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }


    public void galeriIslemleri(){
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                //System.out.println(uri);
                this.uri=uri;
                //*******************************
                timestamp= Timestamp.now();
                //Burada resime verilecek ismi belirliyoruz....
                String resimIsmi=String.valueOf(timestamp.getSeconds());
                StorageReference storageReference = storage.getReference(uri.toString());
                //Burada dosyanın adını oluşturup kaydetme işlemini yapıyoruz.
                StorageReference riversRef = storageReference.child(resimIsmi);
                uploadTask = riversRef.putFile(uri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        System.out.println("gönderirken bir hata ile karşılaşıldı....");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Aşağıdaki yapı sayesinde bir resmi hem veritabanına kaydedip hem de indirme bağlantısını alıyoruz.
                        storageReference.child(resimIsmi).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Burada indirme bağlantısını veriyor.
                                System.out.println(uri.toString());
                                //**********************************
                                veriEkle(uri.toString(),"var");
                                //**********************************
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                        // ...
                    }
                });
                //*******************************
            } else {
                System.out.println("PhotoPicker"+ "No media selected");
                this.uri=null;
            }

        });
    }

    public void veriEkle(String mesaj,String resimBilgisi){
        Map<String,String> gonderilenGrupMesaj=new HashMap<>();
        timestamp= Timestamp.now();//Firebaseden zamanı çekerek farklı cihazlarda da uyum sağlamasını sağlamış olduk.
        zaman=timestamp.getSeconds();
        if(!uri.toString().isEmpty()){
            gonderilenGrupMesaj.put("gondericiTelNo",user.getPhoneNumber());
            gonderilenGrupMesaj.put("mesaj",mesaj);
            gonderilenGrupMesaj.put("gonderenAd",kullaniciAdi);
            gonderilenGrupMesaj.put("resimBilgisi",resimBilgisi);
            gonderilenGrupMesaj.put("zaman", String.valueOf(zaman));

            db.collection(gelenGrupAdi).add(gonderilenGrupMesaj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    adapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GrupMesajActivity.this, "Bir hata ile karşılasıldı", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "Lütfen bir mesaj giriniz.", Toast.LENGTH_SHORT).show();
        }
    }
}