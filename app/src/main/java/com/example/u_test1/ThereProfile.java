package com.example.u_test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.u_test1.adapter.AdapterProduct;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ThereProfile extends AppCompatActivity {
    FirebaseAuth mAuth;
    RecyclerView postsRecyclerView;
    List<productPost> postList;
    AdapterProduct adapterProduct;
    String uId;

    ActionBar actionBar;
    TextView textview;
    TextView profile_Name, profile_Email,profile_Address,profile_Division, profile_Phone;
    ImageView profile_Image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);
        actionBar=getSupportActionBar();

        textview = new TextView(ThereProfile.this);

        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        textview.setLayoutParams(layoutparams);

        textview.setText("User Profile");
        textview.setPadding(20,0,0,0);
        textview.setTextColor(Color.WHITE);
        textview.setTextSize(16);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(textview);
        actionBar.setLogo(R.drawable.ic_home_black);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        postsRecyclerView=findViewById(R.id.currentUser_ProductRecy);
        postsRecyclerView.setNestedScrollingEnabled(true);
        profile_Image =findViewById(R.id.profile_Image);
        profile_Name = findViewById(R.id.profile_Name);
        profile_Email = findViewById(R.id.profile_Email);
        profile_Phone = findViewById(R.id.profile_Phone);
        profile_Address=findViewById(R.id.profile_Address);
        profile_Division=findViewById(R.id.profile_Division);

        mAuth=FirebaseAuth.getInstance();
        postList=new ArrayList<>();
        Intent intent =getIntent();
        uId = intent.getStringExtra("uId");
        FirebaseUser user=mAuth.getCurrentUser();
        if (user != null) {
            Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uId);
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
        }
        loadHistPost();
        checkUserStatus();
    }

    private void loadHistPost() {
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(ThereProfile.this,2);
        postsRecyclerView.setLayoutManager(linearLayoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Products");
        Query query = ref.orderByChild("uid").equalTo(uId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    productPost pProducts=ds.getValue(productPost.class);
                    postList.add(pProducts);
                    adapterProduct = new AdapterProduct(ThereProfile.this,postList);
                    postsRecyclerView.setAdapter(adapterProduct);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfile.this,""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void searchHistPost(final String searchQuery){
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(ThereProfile.this,2);
        postsRecyclerView.setLayoutManager(linearLayoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Products");
        Query query = ref.orderByChild("uid").equalTo(uId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    productPost pProducts=ds.getValue(productPost.class);
                    if (pProducts.getpName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            pProducts.getpCategory().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(pProducts);
                    }
                    adapterProduct = new AdapterProduct(ThereProfile.this,postList);
                    postsRecyclerView.setAdapter(adapterProduct);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfile.this,""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.Search_action);
        final androidx.appcompat.widget.SearchView searchView =(androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty( query)){
                    searchHistPost(query);
                }
                else {
                    loadHistPost();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty( newText)){
                    searchHistPost(newText);
                }
                else {
                    loadHistPost();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
    private void checkUserStatus(){
        FirebaseUser user=mAuth.getCurrentUser();
        if (user != null){

        }else {
            startActivity(new Intent(ThereProfile.this,MainActivity.class));
            finish();
        }
    }
}
