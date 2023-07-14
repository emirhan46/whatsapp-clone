package com.example.wpclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText editTextTelefonNumarasi,editTextKod;
    Button buttonGirisYap,buttonKodDogrula;
    FirebaseAuth auth;
    PhoneAuthOptions options;
    String code;

    FirebaseFirestore db;
    String verificationID ;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    FirebaseUser user;

    PhoneAuthProvider.ForceResendingToken mResendToken;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db=FirebaseFirestore.getInstance();

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        editTextTelefonNumarasi=findViewById(R.id.editTextTelefonNumarasi);



        editTextKod=findViewById(R.id.editTextKod);
        buttonGirisYap=findViewById(R.id.buttonGirisYap);
        buttonKodDogrula=findViewById(R.id.buttonKodDogrula);

        buttonKodDogrula.setVisibility(View.INVISIBLE);
        editTextKod.setVisibility(View.INVISIBLE);

        auth=FirebaseAuth.getInstance();

        user= auth.getInstance().getCurrentUser();

        if (user != null) {
            //Kişi Daha önce giriş yapmış ise.
            Intent intent=new Intent(MainActivity.this,KisilerActivity.class);
            startActivity(intent);
        }else{
            //Kişi giriş yapmamış ise
        }
    }


    public void girisYap_OnClick(View view){

        String gelenTelefonNumarasi="+90"+editTextTelefonNumarasi.getText().toString();


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                //Doğrulama Tamamlandığında
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                //Doğrulama Başarısız olduğunda

                Toast.makeText(MainActivity.this, "İşlem başarılı olamadı", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                verificationID=verificationId;
                mResendToken=token;
                Toast.makeText(MainActivity.this, "kod Gönderildi", Toast.LENGTH_SHORT).show();
                editTextTelefonNumarasi.setVisibility(View.INVISIBLE);
                buttonGirisYap.setVisibility(View.INVISIBLE);
                editTextKod.setVisibility(View.VISIBLE);
                buttonKodDogrula.setVisibility(View.VISIBLE);

            }
        };


        options=PhoneAuthOptions.newBuilder(auth).setPhoneNumber(gelenTelefonNumarasi).setTimeout(60L, TimeUnit.SECONDS).setActivity(MainActivity.this).setCallbacks(mCallbacks).build();
        //PhoneAuthProvider.getInstance(auth).verifyPhoneNumber(gelenTelefonNumarasi,60,TimeUnit.SECONDS,MainActivity.this,mCallbacks);
        PhoneAuthProvider.verifyPhoneNumber(options);

//***********************************************************

    }

    public void kodDogrula_OnClick(View view){
        String girilenKod=editTextKod.getText().toString();
        if (girilenKod.isEmpty()){
            Toast.makeText(MainActivity.this, "Lütfen Doğrulama kodunu giriniz", Toast.LENGTH_SHORT).show();
        }else{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, girilenKod);
            signInWithPhoneAuthCredential(credential);
        }

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // İşlem başarılı ise gidilecek veya yapılacak işlemler...
                            Intent intent=new Intent(MainActivity.this,KayitActivity.class);
                            startActivity(intent);

                        } else {
                            //İşlem Başarısız ise
                            Toast.makeText(MainActivity.this, "Bir hata ile karşılaşıldı ...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}