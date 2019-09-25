package com.example.u_test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Instrumentation;
import android.app.ProgressDialog;
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
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.u_test1.adapter.ProductViewPagerAdapter;
import com.example.u_test1.adapter.ViewpagerAdapter;
import com.facebook.login.LoginManager;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class Product_Detail extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE =100 ;
    String pId,pCategory,pName,pDescription,pPrize,product_Likes,pTime,uId,uName,uEmail,uAddress,uDivision,uPhone,uDp;

    ProgressDialog pd;

    //View pager
    ViewPager viewPager;
    LinearLayout sliderDot;
    private int dotsCount;
    private ImageView[] dots;
    ProductViewPagerAdapter viewpagerAdapter;

    //view bind
    TextView product_Name,product_Price,product_Description,product_Time,user_Name,user_Address,user_Division,user_Phone,checkProfile,favourite;
    ImageView backproduct,btn_Favourite;

    FirebaseAuth mAuth;
    FirebaseUser users;
    private DatabaseReference likeRef;
    private DatabaseReference postsRef;
    private DatabaseReference usersRef;
    boolean mProcessLike=false;
    String mUid;
    String likes;

    //comment
    EditText comment_Edt;
    ImageButton comment_Send;
    com.blogspot.atifsoftwares.circularimageview.CircularImageView comment_Img;
    String myProfile,myEmail,myName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__detail);
        getSupportActionBar().hide();

        viewPager=findViewById(R.id.viewPagerProduct);
        sliderDot=findViewById(R.id.productSliderDot);
        product_Name=findViewById(R.id.pName);
        product_Price=findViewById(R.id.pPrice);
        product_Description=findViewById(R.id.pDescription);
        product_Time=findViewById(R.id.pTime);
        backproduct=findViewById(R.id.backProduct);
        checkProfile=findViewById(R.id.checkProfile);
        user_Name=findViewById(R.id.uName);
        user_Address=findViewById(R.id.uAddress);
        user_Division=findViewById(R.id.uDivision);
        user_Phone=findViewById(R.id.uPhone);
        btn_Favourite=findViewById(R.id.products_Favourite);
        favourite=findViewById(R.id.products_FavouriteTv);
        comment_Edt=findViewById(R.id.comment_Tv);
        comment_Send=findViewById(R.id.comment_Send);
        comment_Img=findViewById(R.id.comment_ProfileImg);
        mAuth=FirebaseAuth.getInstance();
        users=mAuth.getCurrentUser();
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        backproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent i = getIntent();
        pId=i.getExtras().getString("pId");
        String pImage1=i.getExtras().getString("pImage1");
        String pImage2=i.getExtras().getString("pImage2");
        String pImage3=i.getExtras().getString("pImage3");
        pCategory=i.getExtras().getString("pCategory");
        pName=i.getExtras().getString("pName");
        pPrize=i.getExtras().getString("pPrize");
        pDescription=i.getExtras().getString("pDescription");
        pTime=i.getExtras().getString("pTime");
        likes=i.getExtras().getString("pLikes");
        uId=i.getExtras().getString("uId");
        mUid=i.getExtras().getString("currentUid");
        uName=i.getExtras().getString("uName");
        uEmail=i.getExtras().getString("uEmail");
        uAddress=i.getExtras().getString("uAddress");
        uDivision=i.getExtras().getString("uDivision");
        uPhone=i.getExtras().getString("uPhone");
        uDp=i.getExtras().getString("uDp");

        comment_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });
        checkProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Product_Detail.this,ThereProfile.class);
                intent.putExtra("uId",uId);
                startActivity(intent);
            }
        });
        if (users != null) {
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
        if (users != null){
            Query query=usersRef.orderByChild("uid").equalTo(mUid);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        myProfile=""+ds.child("image").getValue();
                        myEmail=""+ds.child("email").getValue();
                        myName=""+ds.child("name").getValue();
                        try {

                            Picasso.get().load(myProfile).fit().into(comment_Img);

                        }catch (Exception e){
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        btn_Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (users!=null){
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
                            Toast.makeText(Product_Detail.this,""+databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    Toast.makeText(Product_Detail.this,"Please Register or Login",Toast.LENGTH_LONG).show();
                }
            }
        });


         if (users!=null){
            setLikes(mUid);
        }
        product_Name.setText(pName);
        product_Price.setText(pPrize);
        product_Description.setText(pDescription);
        product_Time.setText(pTime);
        justify(product_Description);
        user_Name.setText(uName);
        user_Address.setText(uAddress);
        user_Division.setText(uDivision);
        user_Phone.setText(uPhone);
        user_Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int REQUEST_PHONE_CALL=1;
                String number=user_Phone.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+number));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(Product_Detail.this, Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(Product_Detail.this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                    }
                    else {
                        startActivity(callIntent);
                    }
                }

            }
        });

        if (!pImage1.equals(" ") && !pImage2.equals(" ") && !pImage3.equals(" ")){
            String[] urls=new String[]{pImage1,pImage2,pImage3};
            viewpagerAdapter=new ProductViewPagerAdapter(Product_Detail.this,urls);
            viewPager.setAdapter(viewpagerAdapter);
            dotsCount=viewpagerAdapter.getCount();
        }
        else if (!pImage1.equals(" ") && !pImage2.equals(" ") && pImage3.equals(" ")){
            String[] urls=new String[]{pImage1,pImage2};
            viewpagerAdapter=new ProductViewPagerAdapter(Product_Detail.this,urls);
            viewPager.setAdapter(viewpagerAdapter);
            dotsCount=viewpagerAdapter.getCount();
        }
        else if (!pImage1.equals(" ") && pImage2.equals(" ") && pImage3.equals(" ")){
            String[] urls=new String[]{pImage1};
            viewpagerAdapter=new ProductViewPagerAdapter(Product_Detail.this,urls);
            viewPager.setAdapter(viewpagerAdapter);
            dotsCount=viewpagerAdapter.getCount();
        }

        dots =new ImageView[dotsCount];

        for (int count=0 ; count<dotsCount; count++){
            dots[count]=new ImageView(Product_Detail.this);
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
                    dots[i].setImageDrawable(ContextCompat.getDrawable(Product_Detail.this, R.drawable.nonactive_circle));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(Product_Detail.this, R.drawable.active_circle));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Product_Detail.MyTimerTask(),2000,4000);

    }

    private void postComment() {
        pd = new ProgressDialog(this);
        pd.setMessage("Adding comment...");


        final String comment=comment_Edt.getText().toString().trim();
        if (TextUtils.isEmpty(comment)){
            Toast.makeText(this,"Comment is empty...",Toast.LENGTH_LONG).show();
            return;
        }
        String timeStamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Products").child(pId).child("Comments");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("cId",timeStamp);
        hashMap.put("comment",comment);
        hashMap.put("timestamp",timeStamp);
        hashMap.put("uid",mUid);
        hashMap.put("uEmail",myEmail);
        hashMap.put("uDp",myProfile);
        hashMap.put("uName",myName);

        ref.child(timeStamp).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(Product_Detail.this,"Comment added....",Toast.LENGTH_LONG).show();
                comment_Edt.setText("");
                updateCommentCount();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Product_Detail.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }
    boolean myProcessComment = false;
    private void updateCommentCount() {
        myProcessComment = true;
        final DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Products").child(pId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (myProcessComment){
                    String comments=""+dataSnapshot.child("pComments").getValue();
                    int newCommentBal = Integer.parseInt(comments)+1;
                    reference.child("pComments").setValue(""+newCommentBal);
                    myProcessComment=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                Toast.makeText(Product_Detail.this,""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


    public class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            if (Product_Detail.this!=null){
                Product_Detail.this.runOnUiThread(new Runnable() {
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

}
