package com.example.u_test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {
    Spinner spinnerCountry;
    checkEdtLength checkLength;
    Button btn_Rnext, btn_Send;
    EditText edtPhoneNum, edtSMS;
    TextView login;



    ProgressBar progressBar;
    private String verificatinID;
    private FirebaseAuth mAuth;
    UserProfile user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        user=new UserProfile();
        spinnerCountry = findViewById(R.id.spinnerCountry);
        spinnerCountry.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, CountryCode.countryNames));
        checkLength = new checkEdtLength();


        login=findViewById(R.id.txt_Login);
        edtPhoneNum = findViewById(R.id.edtPhoneNum);
        edtSMS = findViewById(R.id.edtSMS);
        btn_Rnext = findViewById(R.id.btn_Pregister);
        btn_Send = findViewById(R.id.btnSend);
        progressBar = findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();



        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode=CountryCode.countryAreaCode[spinnerCountry.getSelectedItemPosition()];
                boolean checkPhonegreater=checkLength.checkPhoneNumgreater(edtPhoneNum.getText().toString());
                boolean checkPhoneless=checkLength.checkPhoneNumless(edtPhoneNum.getText().toString());
                String phoneNumber="+"+countryCode+edtPhoneNum.getText().toString();
                if (checkPhonegreater==true && checkPhoneless==true){
                    sendVerificationCode(phoneNumber);

                }
                else {
                    edtPhoneNum.setError("Check your phone number!");
                }
            }
        });
        btn_Rnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = edtSMS.getText().toString();
                if (code.isEmpty() || code.length()<6){
                    Toast.makeText(Register.this,"Enter code...",Toast.LENGTH_LONG).show();
                    edtSMS.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });


    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }




    @Override
    protected void onStart() {
        super.onStart();
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificatinID,code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String countryCode=CountryCode.countryAreaCode[spinnerCountry.getSelectedItemPosition()];
                    String phoneNumber="+"+countryCode+edtPhoneNum.getText().toString();

                    Intent intent= new Intent(Register.this,User_Register.class);
                    Toast.makeText(Register.this, "Data Inserted Successful", Toast.LENGTH_SHORT).show();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("PhoneNum",phoneNumber);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(Register.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void sendVerificationCode(String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE  );
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificatinID = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                edtSMS.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Register.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

    public void txtLogin(View view) {
        Intent intent=new Intent(Register.this,MainActivity.class);
        startActivity(intent);
    }
}

