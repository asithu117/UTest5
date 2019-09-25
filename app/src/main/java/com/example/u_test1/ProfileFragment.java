package com.example.u_test1;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    //Firebase
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //FirebaseStorage
    StorageReference storageReference;
    //path where images of user profile
    String storagePath ="Users_Profile_Images/";

    //View
    ImageView profile_Image;
    TextView profile_Name,profile_Name1, profile_Email,profile_Email1, profile_Phone,profile_Phone1,profile_Address,profile_Division;
    Button btn_SignupLogin;
    FloatingActionButton fab;

    //progress dialog
    ProgressDialog progressDialog;

    //permission constant
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLARY_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;

    //array of permission to be requested
    String cameraPermission[];
    String storagePermission[];

    //Uri of picked image
    Uri image_uri;

    //for checkiing for profile
    String profilePhoto;



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        //init firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference();

        //
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init views
        profile_Image = view.findViewById(R.id.profile_Image);
        profile_Name = view.findViewById(R.id.profile_Name);
        profile_Email = view.findViewById(R.id.profile_Email);
        profile_Phone = view.findViewById(R.id.profile_Phone);
        profile_Name1 = view.findViewById(R.id.profile_Name1);
        profile_Email1 = view.findViewById(R.id.profile_Email1);
        profile_Phone1 = view.findViewById(R.id.profile_Phone1);
        profile_Address = view.findViewById(R.id.profile_Address);
        profile_Division = view.findViewById(R.id.profile_Division);
        btn_SignupLogin=view.findViewById(R.id.btn_SignupLogin);

        fab = view.findViewById(R.id.fab);

        //init progress dialog
        progressDialog = new ProgressDialog(getActivity());


        FirebaseUser user=mAuth.getCurrentUser();
        if (user != null){
            Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = "" + ds.child("name").getValue();
                        String email = "" + ds.child("email").getValue();
                        String address = "" + ds.child("address").getValue();
                        String division = "" + ds.child("division").getValue();
                        String phone = "" + ds.child("phone").getValue();
                        String image = "" + ds.child("image").getValue();

                        //setData
                        profile_Name.setText(name);
                        profile_Email.setText(email);
                        profile_Phone.setText(phone);
                        profile_Name1.setText(name);
                        profile_Email1.setText(email);
                        profile_Phone1.setText(phone);
                        profile_Address.setText(address);
                        profile_Division.setText(division);
                        try {
                            //if image is received then set
                            Picasso.get().load(image).fit().into(profile_Image);

                        } catch (Exception e) {
                            //if there is any exception while getting image then set default
                            Picasso.get().load(R.drawable.ic_add_image).fit().into(profile_Image);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            btn_SignupLogin.setText("Logout");
            btn_SignupLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginManager.getInstance().logOut();
                    mAuth.signOut();
                    Intent i = new Intent(getActivity(),MainActivity.class);
                    startActivity(i);
                }
            });

        }else {
            btn_SignupLogin.setText("Signup/Login");
            btn_SignupLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(),MainActivity.class);
                    startActivity(i);
                }
            });
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });


        return view;
    }

    //storeage permission
    private boolean checkStorgePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }
    //storage Permission

    //camera Permission
    private boolean checkCameraPermission() {
        boolean camera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean gallary = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return camera && gallary;
    }

    private void requestCameraPermission() {

        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }

    //camera Permission
    private void showEditProfileDialog() {
        String options[] = {"Edit Profile Image", "Edit Name", "Edit Email", "Edit Address", "Edit Division", "Edit Phone"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //Edit profile image clicked
                    progressDialog.setMessage("Updating Profile");
                    profilePhoto ="image";
                    showImagePicDialog();
                } else if (i == 1) {
                    //Edit Name Clicked
                    progressDialog.setMessage("Updating Name");
                    showEditDialog("name");
                } else if (i == 2) {
                    //Edit Email Clicked
                    progressDialog.setMessage("Updating Email");
                    showEditDialog("email");
                } else if (i == 3) {
                    //Edit Address Clicked
                    progressDialog.setMessage("Updating Address");
                    showEditDialog("address");
                } else if (i == 4) {
                    //Edit Division Clicked
                    progressDialog.setMessage("Updating Division");
                    showEditDivisionDialog("division");
                } else if (i == 5) {
                    //Edit Phone Clicked
                    progressDialog.setMessage("Updating Phone");
                    showEditDialog("phone");
                }
            }
        });
        builder.create().show();
    }

    private void showEditDivisionDialog(final String division) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+ division);

        LinearLayout linearLayout=new LinearLayout(getActivity());

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50,70,50,50);

        final Spinner spinnerDivision=new Spinner(getActivity());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, DivisionCode.spinner_Division);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDivision.setAdapter(adapter);
        linearLayout.addView(spinnerDivision);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String value=DivisionCode.spinner_Division[spinnerDivision.getSelectedItemPosition()];
                if (!TextUtils.isEmpty(value)){
                    progressDialog.show();
                    HashMap<String,Object> result=new HashMap<>();
                    result.put(division,value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(),"Updated...",Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                }else {
                    Toast.makeText(getActivity(),"Please enter "+division,Toast.LENGTH_LONG).show();
                }

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

    private void showEditDialog(final String key) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+ key);

        LinearLayout linearLayout=new LinearLayout(getActivity());

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(60,30,60,10);

        final EditText editText=new EditText(getActivity());
        editText.setHint("Enter "+key);
        editText.setTextSize(14);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String value=editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)){
                    progressDialog.show();
                    HashMap<String,Object> result=new HashMap<>();
                    result.put(key,value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(),"Updated...",Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                }else {
                    Toast.makeText(getActivity(),"Please enter "+key,Toast.LENGTH_LONG).show();
                }

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

    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallary"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image From");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //Camera clicked
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else pickFromCamera();

                } else if (i == 1) {
                    //Gallary Clicked
                    if (!checkStorgePermission()){
                        requestStoragePermission();
                    }
                    else pickFromGallary();

                }

            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getActivity(), "Please enable camera & storage permission...", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (writeStorageAccepted) {
                        pickFromGallary();
                    } else {
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_LONG).show();
                    }
                }

            }
           break;
        }

    }

    private void pickFromCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        //put image uri
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //
        Intent cameraIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_REQUEST_CODE);
    }

    private void pickFromGallary() {
        Intent gallaryIntent=new Intent(Intent.ACTION_PICK);
        gallaryIntent.setType("image/*");
        startActivityForResult(gallaryIntent,IMAGE_PICK_GALLARY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLARY_REQUEST_CODE){
                image_uri=data.getData();
                uploadProfilePhoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE){
                uploadProfilePhoto(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(Uri uri) {
        progressDialog.show();

        String filePathAndName=storagePath+ ""+ profilePhoto +"_"+ user.getUid();

        StorageReference storageReference1=storageReference.child(filePathAndName);

        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri=uriTask.getResult();

                if (uriTask.isSuccessful()){
                    HashMap<String, Object> results= new HashMap<>();
                    results.put(profilePhoto, downloadUri.toString());

                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(),"Image Updated...",Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(),"Error Updating Image...",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"Some error occured",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void checkUserStatus(){
        FirebaseUser user=mAuth.getCurrentUser();
        if (user != null){

        }else {
            startActivity(new Intent(getActivity(),MainActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkUserStatus();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        MenuItem item=menu.findItem(R.id.Search_action);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.logout_action){
            LoginManager.getInstance().logOut();
            mAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}