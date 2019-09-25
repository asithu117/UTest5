package com.example.u_test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class User_Register extends AppCompatActivity {
    Spinner spinnerDivision;
    checkEdtLength checkLength;
    Button btn_Register;
    EditText fName,sName,email,address,password,cPassword;
    String Name,f_Name,s_Name,Email,Address,Division,Password,c_Password,Phone;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_user__register);
        setupUIView();
        checkLength=new checkEdtLength();
        mAuth=FirebaseAuth.getInstance();

        btn_Register=findViewById(R.id.btn_Register);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_email = email.getText().toString().trim();
                String user_password = password.getText().toString().trim();

                if (validate()) {
                    boolean check_Name = checkLength.checkName(fName.getText().toString(), sName.getText().toString());
                    boolean check_Mail = checkLength.checkMail(email.getText().toString());
                    boolean check_Address = checkLength.checkAddress(address.getText().toString());
                    boolean check_Passwrod = checkLength.checkPassword(password.getText().toString(), cPassword.getText().toString());
                    if (Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {
                        if (check_Name == true && check_Mail == true && check_Address == true && check_Passwrod == true) {
                            if (password.getText().toString().equals(cPassword.getText().toString())) {
                                userRegister(user_email, user_password);
                            } else {
                                Toast.makeText(User_Register.this, "Password doest not match", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            email.setError("Invalid Email");
                            email.setFocusable(true);
                        }
                    }

                    else if (check_Name == false){
                        fName.setError("Type more than 1 characters");
                        sName.setError("Type more than 1 characters");
                    }
                    else if (check_Mail == false){
                        email.setError("Type more than 14 characters");
                    }
                    else if (check_Address == false){
                        address.setError("Type more than 4 characters");
                    }
                    else if (check_Passwrod == false){
                        password.setError("Type more than 7 characters ");
                    }
                }
            }
        });
    }

    private void userRegister(String user_email, String user_password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(user_email, user_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success,
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            String email=user.getEmail();
                            String uid=user.getUid();
                            f_Name=fName.getText().toString();
                            s_Name=sName.getText().toString();
                            Address=address.getText().toString();
                            Division=DivisionCode.spinner_Division[spinnerDivision.getSelectedItemPosition()];
                            Intent i = getIntent();
                            String phone=i.getExtras().getString("PhoneNum");

                            HashMap<Object, String> hashMap =new HashMap<>();

                            hashMap.put("name",f_Name+" "+s_Name);
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("address",Address);
                            hashMap.put("division",Division);
                            hashMap.put("phone",phone);
                            hashMap.put("image","");

                            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                            DatabaseReference  reference = firebaseDatabase.getReference("Users");
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(User_Register.this,""+user.getEmail()+user.getUid(), Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(User_Register.this, Dashboard.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(User_Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

                Toast.makeText(User_Register.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
        
    }

    private void setupUIView(){
        fName=findViewById(R.id.edt_fName);
        sName=findViewById(R.id.edt_sName);
        email=findViewById(R.id.edt_Email);
        address=findViewById(R.id.edt_Address);
        password=findViewById(R.id.edt_Password);
        cPassword=findViewById(R.id.edt_cPassword);
        spinnerDivision=findViewById(R.id.spinnerDivision);
        spinnerDivision.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,DivisionCode.spinner_Division));

    }
    private boolean validate() {
        Boolean result=false;
        f_Name=fName.getText().toString();
        s_Name=sName.getText().toString();
        Email=email.getText().toString();
        Address=address.getText().toString();
        Division=DivisionCode.spinner_Division[spinnerDivision.getSelectedItemPosition()];
        Password=password.getText().toString();
        c_Password=cPassword.getText().toString();
        if (f_Name.isEmpty() && s_Name.isEmpty() && Email.isEmpty() && Address.isEmpty() && Password.isEmpty() && c_Password.isEmpty()){
            Toast.makeText(User_Register.this,"Please enter all the details",Toast.LENGTH_LONG).show();
        }
        else {
            result = true;
        }
        return result;
    }

}
