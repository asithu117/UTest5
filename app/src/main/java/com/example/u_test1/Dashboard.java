package com.example.u_test1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.u_test1.adapter.AdapterProduct;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class Dashboard extends AppCompatActivity {
    //Firebae Auth
    FirebaseAuth mAuth;
    ActionBar actionBar;
    TextView textview;

    //Recycler
    androidx.recyclerview.widget.RecyclerView recyclerView;
    List<productPost> postList;
    AdapterProduct adapterProduct;


    //Views
    MenuItem Search;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar=getSupportActionBar();

        textview = new TextView(Dashboard.this);

        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        textview.setLayoutParams(layoutparams);

        textview.setText("Home");
        textview.setPadding(20,0,0,0);
        textview.setTextColor(Color.WHITE);
        textview.setTextSize(16);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(textview);
        actionBar.setLogo(R.drawable.ic_home_black);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);



        mAuth=FirebaseAuth.getInstance();

        BottomNavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        textview.setText("Home");
        HomeFragment fragmentHome=new HomeFragment();
        FragmentManager fgM=getSupportFragmentManager();
        FragmentTransaction fgT=fgM.beginTransaction();
        fgT.replace(R.id.frame_container,fragmentHome,"");
        fgT.commit();


    }


    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){

                        case R.id.nav_home:
                            textview.setText("Home");
                            actionBar.setLogo(R.drawable.ic_home_black);
                            actionBar.setDisplayUseLogoEnabled(true);
                            actionBar.setDisplayShowHomeEnabled(true);
                            HomeFragment fragmentHome=new HomeFragment();
                            FragmentManager fgM=getSupportFragmentManager();
                            FragmentTransaction fgT=fgM.beginTransaction();
                            fgT.replace(R.id.frame_container,fragmentHome,"");
                            fgT.commit();
                            return true;
                        case R.id.nav_Users:
                            textview.setText("UsersList");
                            actionBar.setLogo(R.drawable.users1);
                            actionBar.setDisplayUseLogoEnabled(true);
                            actionBar.setDisplayShowHomeEnabled(true);
                            UsersFragment usersFragment=new UsersFragment();
                            FragmentManager fgM3=getSupportFragmentManager();
                            FragmentTransaction fgT3=fgM3.beginTransaction();
                            fgT3.replace(R.id.frame_container,usersFragment,"");
                            fgT3.commit();
                            return true;
                        case R.id.nav_addproduct:
                            textview.setText("Your Products");
                            actionBar.setLogo(R.drawable.ic_product);
                            actionBar.setDisplayUseLogoEnabled(true);
                            actionBar.setDisplayShowHomeEnabled(true);
                            AddProductFragment fragmentProduct=new AddProductFragment();
                            FragmentManager fgM1=getSupportFragmentManager();
                            FragmentTransaction fgT1=fgM1.beginTransaction();
                            fgT1.replace(R.id.frame_container,fragmentProduct,"");
                            fgT1.commit();
                            return true;
                        case R.id.nav_profile:
                            textview.setText("Profile");
                            actionBar.setLogo(R.drawable.ic_user);
                            actionBar.setDisplayUseLogoEnabled(true);
                            actionBar.setDisplayShowHomeEnabled(true);
                            ProfileFragment fragmentProfile=new ProfileFragment();
                            FragmentManager fgM2=getSupportFragmentManager();
                            FragmentTransaction fgT2=fgM2.beginTransaction();
                            fgT2.replace(R.id.frame_container,fragmentProfile,"");
                            fgT2.commit();
                            return true;
                    }
                    return false;
                }
            };


    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
