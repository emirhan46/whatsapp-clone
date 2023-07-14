package com.example.wpclone;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wpclone.Adapter.MesajAdapter;
import com.google.android.gms.tasks.Continuation;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MesajActivity extends AppCompatActivity {
    String gelenKisiTelNo;
    FirebaseUser user;
    RecyclerView rv;
    FirebaseAuth auth;
    MesajAdapter adapter;
    FirebaseFirestore db;
    EditText editTextMesaj;
    List<KisilerMesaj> kisilerMesajList;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    Uri uri;
    FirebaseStorage storage;
    long zaman;
    Timestamp timestamp;
    StorageReference storageReference;
    UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj);
        Intent intent=getIntent();
        gelenKisiTelNo=intent.getStringExtra("kisiNo");
        kisilerMesajList=new ArrayList<>();
        

        editTextMesaj=findViewById(R.id.editTextMesaj);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();

        user= auth.getInstance().getCurrentUser();
        System.out.println("Kendi telefon numaram :"+user.getPhoneNumber());
        System.out.println("Mesajı alan kişi No :"+gelenKisiTelNo);

        galeriIslemleri();

        //mesajlariCek();
        MesajActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mesajlariGuncelle();
            }
        });
        adapter=new MesajAdapter(kisilerMesajList,user.getPhoneNumber(),gelenKisiTelNo);
        rv=findViewById(R.id.mesajRecyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    public void gonder_OnClick(View view){
        //Bir mesajın gönderilmesi için gerekli olan işlemler
        String gonderilecekMesaj=editTextMesaj.getText().toString().trim();
        timestamp=Timestamp.now();//Firebaseden zamanı çekerek farklı cihazlarda da uyum sağlamasını sağlamış olduk.
        zaman=timestamp.getSeconds();
        Map<String,Object> Mesaj=new HashMap<>();
        Mesaj.put("gonderenKullaniciAdi",auth.getCurrentUser().getPhoneNumber());
        Mesaj.put("aliciKullaniciAdi",gelenKisiTelNo);
        Mesaj.put("zaman",zaman);

        Mesaj.put("resim","yok");//Resimleri indirme bağlantısını ayrı olarak ekleme işlemi için.
        if(!gonderilecekMesaj.isEmpty()){
            Mesaj.put("gonderilenMesaj", gonderilecekMesaj);

            db.collection("mesaj").add(Mesaj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                @Override
                public void onSuccess(DocumentReference documentReference) {
                    //Toast.makeText(MesajActivity.this, "Mesaj gönderildi", Toast.LENGTH_SHORT).show();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MesajActivity.this, "Bir hata ile karşılaşıldı", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(this, "Lütfen bir mesaj giriniz.", Toast.LENGTH_SHORT).show();
        }
        editTextMesaj.setText("");
        mesajlariGuncelle();
        //***********************
        adapter.notifyDataSetChanged();
    }

    public void resimGonder_OnClick(View view){
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                //.setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                .build());


    }
    /*public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }*/
    /*public void mesajlariCek(){
    kisilerMesajList.clear();
    //Burada bir defa mesajları çeker ve sayfa yenilenene kadar bilgileri değiştirmez
        //Burada karşı taraftan gelen mesajları çekme işlemi yapacağız.
        db.collection(user.getPhoneNumber()+"->"+gelenKisiTelNo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String mesaj=document.get("gonderilenMesaj").toString();
                        String gonderenTelNo=document.get("gonderenKullaniciAdi").toString();
                        String aliciTelNo=document.get("aliciKullaniciAdi").toString();
                        long zaman=Long.decode(document.get("zaman").toString());
                        KisilerMesaj km=new KisilerMesaj(gonderenTelNo,aliciTelNo,zaman,mesaj);
                        kisilerMesajList.add(km);
                    }
                }else{
                    Toast.makeText(MesajActivity.this, "Bir hata ile karşılaşıldı.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Burada karşı tarafa gönderilen mesajları çekme işlemi, yapacağız
        db.collection(gelenKisiTelNo+"->"+user.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String mesaj=document.get("gonderilenMesaj").toString();
                        String gonderenTelNo=document.get("gonderenKullaniciAdi").toString();
                        String aliciTelNo=document.get("aliciKullaniciAdi").toString();
                        long zaman=Long.decode(document.get("zaman").toString());
                        KisilerMesaj km=new KisilerMesaj(gonderenTelNo,aliciTelNo,zaman,mesaj);
                        kisilerMesajList.add(km);
                    }
                }else{
                    Toast.makeText(MesajActivity.this, "Bir hata ile karşılaşıldı.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/
    public void mesajlariGuncelle(){

        //Buradaki fonksiyon sayesinde veri her değiştiğinde sayfayı yeniler.
        //**************************************
        db.collection("mesaj").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                System.out.println("*******************************************");
                kisilerMesajList.clear();
                for (QueryDocumentSnapshot document : value) {
                    String gonderenTelNo=document.get("gonderenKullaniciAdi").toString();
                    String aliciTelNo=document.get("aliciKullaniciAdi").toString();
                    long zaman=Long.decode(document.get("zaman").toString());
                    String resimBilgisi=(String) document.get("resim").toString();
                    if(user.getPhoneNumber().equals(gonderenTelNo) && gelenKisiTelNo.equals(aliciTelNo)){
                        String mesaj=document.get("gonderilenMesaj").toString();
                        KisilerMesaj km=new KisilerMesaj(gonderenTelNo,aliciTelNo,zaman,mesaj,resimBilgisi);
                        kisilerMesajList.add(km);
                        System.out.println(gonderenTelNo+"->"+km.mesaj);
                    }
                    if(gonderenTelNo.equals(gelenKisiTelNo) && gelenKisiTelNo.equals(gonderenTelNo)){
                        String mesaj=document.get("gonderilenMesaj").toString();
                        KisilerMesaj km=new KisilerMesaj(gonderenTelNo,aliciTelNo,zaman,mesaj,resimBilgisi);
                        kisilerMesajList.add(km);
                        System.out.println(gonderenTelNo+"->"+km.mesaj);
                    }
                }
                kisilerMesajList.sort(new Comparator<KisilerMesaj>() {
                    @Override
                    public int compare(KisilerMesaj o1, KisilerMesaj o2) {
                        return (o1.zaman < o2.zaman) ? -1 : ((o1.zaman == o2.zaman) ? 0 :1 );
                    }
                });
                adapter.notifyDataSetChanged();
                adapter=new MesajAdapter(kisilerMesajList,user.getPhoneNumber(),gelenKisiTelNo);
                rv=findViewById(R.id.mesajRecyclerView);
                rv.setHasFixedSize(true);
                rv.setLayoutManager(new LinearLayoutManager(MesajActivity.this));
                rv.setAdapter(adapter);
            }
        });
        //-***********************************
    }


    public void veriEkle(String mesaj,String resimBilgisi){
        String gonderilecekMesaj=mesaj;
        timestamp=Timestamp.now();//Firebaseden zamanı çekerek farklı cihazlarda da uyum sağlamasını sağlamış olduk.
        zaman=timestamp.getSeconds();
        Map<String,Object> Mesaj=new HashMap<>();
        Mesaj.put("gonderenKullaniciAdi",auth.getCurrentUser().getPhoneNumber());
        Mesaj.put("aliciKullaniciAdi",gelenKisiTelNo);
        Mesaj.put("zaman",zaman);
        Mesaj.put("resim",resimBilgisi);//Resimleri indirme bağlantısını ayrı olarak ekleme işlemi için.
        if(!gonderilecekMesaj.isEmpty()){
            Mesaj.put("gonderilenMesaj", gonderilecekMesaj);
            db.collection("mesaj").add(Mesaj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    //İşlem başarıyla sonuçlanırsa yapılacak işlemler.
                    //Toast.makeText(MesajActivity.this, "Mesaj gönderildi", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MesajActivity.this, "Bir hata ile karşılaşıldı", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(this, "Lütfen bir mesaj giriniz.", Toast.LENGTH_SHORT).show();
        }
        editTextMesaj.setText("");
        //***********************
        adapter.notifyDataSetChanged();
    }

    public void galeriIslemleri(){
        pickMedia = registerForActivityResult(new PickVisualMedia(), uri -> {
            if (uri != null) {
                //System.out.println(uri);
                this.uri=uri;
                //*******************************
                timestamp= Timestamp.now();
                //Burada resime verilecek ismi belirliyoruz....
                String resimIsmi=String.valueOf(timestamp.getSeconds());
                storageReference = storage.getReference(uri.toString());
                //Burada dosyanın adını oluşturup kaydetme işlemini yapıyoruz.
                StorageReference riversRef = storageReference.child(resimIsmi);
                uploadTask = riversRef.putFile(uri);

                // Register observers to listen for when the download is done or if it fails
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
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
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
}