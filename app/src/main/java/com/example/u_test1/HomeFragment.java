package com.example.u_test1;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.u_test1.adapter.AdapterProduct;
import com.example.u_test1.adapter.ViewpagerAdapter;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    //Firebase
    FirebaseAuth mAuth;
    androidx.recyclerview.widget.RecyclerView recyclerView;
    List<productPost> postList;
    AdapterProduct adapterProduct;



    //ImageSLide
    ViewPager viewPager;
    LinearLayout sliderDot;
    private int dotsCount;
    private ImageView[] dots;
    private String[] urls=new String[]{"https://ual-media-res.cloudinary.com/image/fetch/c_fill,f_auto,g_auto,h_337,q_auto,w_600/w_auto:breakpoints/https://www.arts.ac.uk/__data/assets/image/0018/63234/CHILDRkz3a_Childrenswear_Design_Online.jpg",
            "https://www.kochiesbusinessbuilders.com.au/wp-content/uploads/2018/12/online-store.jpg",
            "https://regularpay.com/wp-content/uploads/2014/04/article-images-min.jpg",
            "https://www.hreplicawatches.org/wp-content/uploads/2016/05/Smart-Shopping-Online-Tips.jpeg",
            "https://www.techlicious.com/images/misc/online-clothes-shopping-concept-700px.jpg",
            "https://cdn01.vulcanpost.com/wp-uploads/2014/07/spoilt-for-choice.jpg"};

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth=FirebaseAuth.getInstance();

        //Slide
        viewPager=view.findViewById(R.id.viewPager);
        sliderDot=view.findViewById(R.id.sliderDot);
        ViewpagerAdapter viewpagerAdapter=new ViewpagerAdapter(getContext(),urls);
        viewPager.setAdapter(viewpagerAdapter);
        dotsCount=viewpagerAdapter.getCount();
        dots =new ImageView[dotsCount];

        for (int i=0 ; i<dotsCount; i++){
            dots[i]=new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_circle));
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,0,8,0);
            sliderDot.addView(dots[i],params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_circle));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i<dotsCount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_circle));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_circle));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(),2000,4000);


        //recycler
        recyclerView=view.findViewById(R.id.product_RcyView);
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(true);
        postList=new ArrayList<>();
        LoaPosts();
        return view;
    }
    public class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            if (getActivity()!=null){
                getActivity().runOnUiThread(new Runnable() {
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
                        else if (viewPager.getCurrentItem()==3){
                            viewPager.setCurrentItem(4);
                        }
                        else if (viewPager.getCurrentItem()==4){
                            viewPager.setCurrentItem(5);
                        }
                        else if (viewPager.getCurrentItem()==5){
                            viewPager.setCurrentItem(6);
                        }
                        else{
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        }

    }

    private void LoaPosts() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Products");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    productPost pProducts=ds.getValue(productPost.class);
                    postList.add(pProducts);
                    adapterProduct = new AdapterProduct(getActivity(),postList);
                    recyclerView.setAdapter(adapterProduct);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void searchPosts(final String searchQuery){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Products");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    productPost pProducts=ds.getValue(productPost.class);
                    if (pProducts.getpCategory().toLowerCase().contains(searchQuery.toLowerCase()) || pProducts.getpName().contains(searchQuery.toLowerCase())){
                        postList.add(pProducts);
                    }
                    adapterProduct = new AdapterProduct(getActivity(),postList);
                    recyclerView.setAdapter(adapterProduct);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
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
        final androidx.appcompat.widget.SearchView searchView =(androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    searchPosts(query);
                }else {
                    LoaPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchPosts(newText);
                }else {
                    LoaPosts();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.logout_action){
            LoginManager.getInstance().logOut();
            FirebaseUser user=mAuth.getCurrentUser();
            if (user != null){
                mAuth.signOut();
            }else {
                startActivity(new Intent(getActivity(),MainActivity.class));
            }

            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
    private void checkUserStatus(){
        FirebaseUser user=mAuth.getCurrentUser();
        if (user != null){

        }else {
            startActivity(new Intent(getActivity(),MainActivity.class));
        }
    }
}
