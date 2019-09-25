package com.example.u_test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    Button fb_Login,btn_Login;
    private FirebaseAuth mAuth;
    checkEdtLength checkLength;

    EditText edtEmail,edtPassword;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        checkLength = new checkEdtLength();
        mAuth = FirebaseAuth.getInstance();
        //for firebase
        edtEmail=findViewById(R.id.edt_Email);
        edtPassword=findViewById(R.id.edt_Password);
        btn_Login=findViewById(R.id.btn_Login);

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edtEmail.setError("Invalid Email");
                    edtEmail.setFocusable(true);
                }
                else {
                    loginUser(email,password);
                }
            }
        });

        //for firebase

        //for facebook login
        fb_Login = findViewById(R.id.fb_Login);
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

            }
        });

    progressDialog=new ProgressDialog(this);

    }

    private void loginUser(String email, String password) {
        progressDialog.setMessage("Loggin In...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(MainActivity.this, Dashboard.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                updateUI(user);
                            }

                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(MainActivity.this, Dashboard.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }
    private void updateUI(FirebaseUser user) {
        if (user!=null) {
            String email=user.getEmail();
            String uid=user.getUid();
            String name=user.getDisplayName();
            HashMap<Object, String> hashMap =new HashMap<>();

            hashMap.put("name",name);
            hashMap.put("email",email);
            hashMap.put("uid",uid);
            hashMap.put("address","");
            hashMap.put("division","");
            hashMap.put("phone","");
            hashMap.put("image","");

            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
            DatabaseReference  reference = firebaseDatabase.getReference("Users");
            reference.child(uid).setValue(hashMap);

        }
        else {
            ReturnLogin();

        }
    }
    private void ReturnLogin(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void onClick(View view) {
        loginButton.performClick();
    }
    //for facebooklogin


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }



    public void onCLick_ForgotPass(View view) {
        showPasswordResetDialog();
    }

    private void showPasswordResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Pssword");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText edt_Email=new EditText(this);
        edt_Email.setHint("Enter your email");
        edt_Email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        edt_Email.setMinEms(16);
        edt_Email.setTextSize(14);


        linearLayout.addView(edt_Email);
        linearLayout.setPadding(60,30,60,10);
        builder.setView(linearLayout);
        //button reset
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email=edt_Email.getText().toString().trim();
                beginReset(email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginReset(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Please check your email",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this,"Failed...",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClick_Register(View view) {
        Intent i=new Intent(MainActivity.this,Register.class);
        startActivity(i);
        finish();
    }

    public void txtLogin(View view) {
    }
}
