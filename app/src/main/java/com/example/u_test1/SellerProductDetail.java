package com.example.u_test1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.u_test1.adapter.ProductViewPagerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class SellerProductDetail extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE =100 ;
    String pId,pCategory,pName,pDescription,pPrize,product_Likes,pTime,pImage1,pImage2,pImage3,uId,uName,uEmail,uAddress,uDivision,uPhone,uDp;

    //View pager
    ViewPager viewPager;
    LinearLayout sliderDot;
    private int dotsCount;
    private ImageView[] dots;
    ProductViewPagerAdapter viewpagerAdapter;

    //view bind
    TextView product_Name,product_Price,product_Description,product_Time,user_Name,user_Address,user_Division,user_Phone,favourite;
    ImageView backproduct,btn_Favourite,btn_Menu;
    FloatingActionButton fab;
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    private DatabaseReference likeRef;
    private DatabaseReference postsRef;
    boolean mProcessLike=false;
    String mUid;
    String likes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_detail);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Products");
        firebaseStorage = FirebaseStorage.getInstance();
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        viewPager=findViewById(R.id.viewPagerProduct);
        sliderDot=findViewById(R.id.productSliderDot);
        product_Name=findViewById(R.id.pName);
        product_Price=findViewById(R.id.pPrice);
        product_Description=findViewById(R.id.pDescription);
        product_Time=findViewById(R.id.pTime);
        backproduct=findViewById(R.id.backProduct);
        user_Name=findViewById(R.id.uName);
        btn_Favourite=findViewById(R.id.products_Favourite);
        favourite=findViewById(R.id.products_FavouriteTv);
        user_Address=findViewById(R.id.uAddress);
        user_Division=findViewById(R.id.uDivision);
        user_Phone=findViewById(R.id.uPhone);
        fab=findViewById(R.id.fab);
        btn_Menu=findViewById(R.id.sMenuDetail);
        progressDialog = new ProgressDialog(SellerProductDetail.this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProductDialog();
            }
        });
        backproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent i = getIntent();
        pId=i.getExtras().getString("pId");
        pImage1=i.getExtras().getString("pImage1");
        pImage2=i.getExtras().getString("pImage2");
        pImage3=i.getExtras().getString("pImage3");
        pCategory=i.getExtras().getString("pCategory");
        pName=i.getExtras().getString("pName");
        pPrize=i.getExtras().getString("pPrize");
        pDescription=i.getExtras().getString("pDescription");
        pTime=i.getExtras().getString("pTime");
        uId=i.getExtras().getString("uId");
        uName=i.getExtras().getString("uName");
        uEmail=i.getExtras().getString("uEmail");
        uAddress=i.getExtras().getString("uAddress");
        uDivision=i.getExtras().getString("uDivision");
        uPhone=i.getExtras().getString("uPhone");
        uDp=i.getExtras().getString("uDp");
        likes=i.getExtras().getString("pLikes");
        mUid=i.getExtras().getString("currentUid");
        btn_Menu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                showMoreOption(btn_Menu);
            }
        });

        if (user != null) {
            Query query = postsRef.orderByChild("pid").equalTo(pId);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        product_Likes = "" + ds.child("pLikes").getValue();
                        favourite.setText(product_Likes+"Favourites");

                        Log.d("Infunction plike",""+product_Likes);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            favourite.setText(likes+"Favourites");

        }
        btn_Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user!=null){
                    Log.d("productlike", "onClick: "+product_Likes);
                    final int pLikes = Integer.parseInt(product_Likes);
                    mProcessLike = true;
                    final String productId = pId;
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessLike) {
                                if (dataSnapshot.child(productId).hasChild(mUid)) {
                                    postsRef.child(productId).child("pLikes").setValue("" + (pLikes - 1));
                                    likeRef.child(productId).child(mUid).removeValue();
                                    mProcessLike = false;
                                } else {
                                    postsRef.child(productId).child("pLikes").setValue("" + (pLikes + 1));
                                    likeRef.child(productId).child(mUid).setValue("Liked");
                                    mProcessLike = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(SellerProductDetail.this,""+databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    Toast.makeText(SellerProductDetail.this,"Please Register or Login",Toast.LENGTH_LONG).show();
                }
            }
        });


        if (user!=null){
            setLikes(mUid);
        }
        product_Name.setText(pName);
        product_Price.setText(pPrize);
        product_Description.setText(pDescription);
        justify(product_Description);
        user_Name.setText(uName);
        user_Address.setText(uAddress);
        user_Division.setText(uDivision);
        user_Phone.setText(uPhone);
        product_Time.setText(pTime);
        user_Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int REQUEST_PHONE_CALL=1;
                String number=user_Phone.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+number));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(SellerProductDetail.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SellerProductDetail.this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                    }
                    else {
                        startActivity(callIntent);
                    }
                }

            }
        });

        if (!pImage1.equals(" ") && !pImage2.equals(" ") && !pImage3.equals(" ")){
            String[] urls=new String[]{pImage1,pImage2,pImage3};
            viewpagerAdapter=new ProductViewPagerAdapter(SellerProductDetail.this,urls);
            viewPager.setAdapter(viewpagerAdapter);
            dotsCount=viewpagerAdapter.getCount();
        }
        else if (!pImage1.equals(" ") && !pImage2.equals(" ") && pImage3.equals(" ")){
            String[] urls=new String[]{pImage1,pImage2};
            viewpagerAdapter=new ProductViewPagerAdapter(SellerProductDetail.this,urls);
            viewPager.setAdapter(viewpagerAdapter);
            dotsCount=viewpagerAdapter.getCount();
        }
        else if (!pImage1.equals(" ") && pImage2.equals(" ") && pImage3.equals(" ")){
            String[] urls=new String[]{pImage1};
            viewpagerAdapter=new ProductViewPagerAdapter(SellerProductDetail.this,urls);
            viewPager.setAdapter(viewpagerAdapter);
            dotsCount=viewpagerAdapter.getCount();
        }

        dots =new ImageView[dotsCount];

        for (int count=0 ; count<dotsCount; count++){
            dots[count]=new ImageView(SellerProductDetail.this);
            dots[count].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonactive_circle));
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,0,8,0);
            sliderDot.addView(dots[count],params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_circle));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i<dotsCount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(SellerProductDetail.this, R.drawable.nonactive_circle));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(SellerProductDetail.this, R.drawable.active_circle));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SellerProductDetail.MyTimerTask(),2000,4000);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOption(ImageView btn_menu) {
        PopupMenu popupMenu = new PopupMenu(this,btn_menu,Gravity.END);
        popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SellerProductDetail.this);

                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            beginDelete( );
                            dialog.dismiss();

                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                return false;
            }
        });
        popupMenu.show();
    }


    private void beginDelete( ) {
        if (pImage1.equals("noImage")){
            deleteWithoutImage();
        }
        else {

            delectWithImgae(pImage1);

        }
    }


    private void delectWithImgae(String pImage1) {
        final ProgressDialog pd= new ProgressDialog(this);
        pd.setMessage("Deleting...");
        firebaseStorage.getReferenceFromUrl(pImage1).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Query fquery= FirebaseDatabase.getInstance().getReference("Products").orderByChild("pid").equalTo(pId);
                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(SellerProductDetail.this,"Delete successfully...", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(SellerProductDetail.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


    private void deleteWithoutImage() {
        final ProgressDialog pd= new ProgressDialog(this);
        pd.setMessage("Deleting...");
        Query fquery= FirebaseDatabase.getInstance().getReference("Products").orderByChild("pID").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(SellerProductDetail.this,"Delete successfully...", Toast.LENGTH_LONG).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showEditProductDialog() {
        String options[] = {"Edit Product Name", "Edit Product Category", "Edit Product Description", "Edit Product Price"};

        AlertDialog.Builder builder = new AlertDialog.Builder(SellerProductDetail.this);
        builder.setTitle("Choose Action");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //Edit profile image clicked
                    progressDialog.setMessage("Updating Product Name");
                    showEditDialog("pName","Name");
                } else if (i == 1) {
                    //Edit Name Clicked
                    progressDialog.setMessage("Updating Product Category");
                    showEditDialog("pCategory","Category");
                } else if (i == 2) {
                    //Edit Email Clicked
                    progressDialog.setMessage("Updating Product Description");
                    showEditDialog("pDescription","Description");
                } else if (i == 3) {
                    //Edit Address Clicked
                    progressDialog.setMessage("Updating Product Price");
                    showPrizeEditDialog("pPrize","Prize");
                }
            }
        });
        builder.create().show();
    }

    private void showEditDialog(final String key, String title) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Update "+ title);

        LinearLayout linearLayout=new LinearLayout(this);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(60,30,60,10);

        final EditText editText=new EditText(this);
        editText.setHint("Enter "+title);
        editText.setTextSize(14);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

                String value=editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)){
                    HashMap<String , Object> hashMap=new HashMap<>();
                    hashMap.put(key,value);
                    Log.d("Value",value);
                    Log.d("Key",key);
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Products");
                    reference.child(pId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialogInterface.dismiss();
                            Toast.makeText(SellerProductDetail.this,"Updated...",Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialogInterface.dismiss();
                            Toast.makeText(SellerProductDetail.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    Toast.makeText(SellerProductDetail.this,"Please enter "+key,Toast.LENGTH_LONG).show();
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
    private void showPrizeEditDialog(final String key, String title) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Update "+ title);

        LinearLayout linearLayout=new LinearLayout(this);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(60,30,60,10);

        final EditText editText=new EditText(this);
        editText.setHint("Enter "+title);
        editText.setTextSize(14);
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

                String value=editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)){
                    HashMap<String , Object> hashMap=new HashMap<>();
                    hashMap.put(key,value);
                    Log.d("Value",value);
                    Log.d("Key",key);
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Products");
                    reference.child(pId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialogInterface.dismiss();
                            Toast.makeText(SellerProductDetail.this,"Updated...",Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialogInterface.dismiss();
                            Toast.makeText(SellerProductDetail.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    Toast.makeText(SellerProductDetail.this,"Please enter "+key,Toast.LENGTH_LONG).show();
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



    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if (SellerProductDetail.this!=null){
                SellerProductDetail.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() == 0){
                            viewPager.setCurrentItem(1);
                        }
                        else if (viewPager.getCurrentItem() == 1){
                            viewPager.setCurrentItem(2);
                        }
                        else if (viewPager.getCurrentItem()==2){
                            viewPager.setCurrentItem(3);
                        }
                        else{
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        }

    }
    public static void justify(final TextView textView) {

        final AtomicBoolean isJustify = new AtomicBoolean(false);

        final String textString = textView.getText().toString();

        final TextPaint textPaint = textView.getPaint();

        final SpannableStringBuilder builder = new SpannableStringBuilder();

        textView.post(new Runnable() {
            @Override
            public void run() {

                if (!isJustify.get()) {

                    final int lineCount = textView.getLineCount();
                    final int textViewWidth = textView.getWidth();

                    for (int i = 0; i < lineCount; i++) {

                        int lineStart = textView.getLayout().getLineStart(i);
                        int lineEnd = textView.getLayout().getLineEnd(i);

                        String lineString = textString.substring(lineStart, lineEnd);

                        if (i == lineCount - 1) {
                            builder.append(new SpannableString(lineString));
                            break;
                        }

                        String trimSpaceText = lineString.trim();
                        String removeSpaceText = lineString.replaceAll(" ", "");

                        float removeSpaceWidth = textPaint.measureText(removeSpaceText);
                        float spaceCount = trimSpaceText.length() - removeSpaceText.length();

                        float eachSpaceWidth = (textViewWidth - removeSpaceWidth) / spaceCount;

                        SpannableString spannableString = new SpannableString(lineString);
                        for (int j = 0; j < trimSpaceText.length(); j++) {
                            char c = trimSpaceText.charAt(j);
                            if (c == ' ') {
                                Drawable drawable = new ColorDrawable(0x00ffffff);
                                drawable.setBounds(0, 0, (int) eachSpaceWidth, 0);
                                ImageSpan span = new ImageSpan(drawable);
                                spannableString.setSpan(span, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                        builder.append(spannableString);
                    }

                    textView.setText(builder);
                    isJustify.set(true);
                }
            }
        });
    }
    private void setLikes(final String uId) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(pId).hasChild(uId)){
                    btn_Favourite.setImageResource(R.drawable.heart_button_like);

                }
                else {
                    btn_Favourite.setImageResource(R.drawable.heart_button);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SellerProductDetail.this,""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
