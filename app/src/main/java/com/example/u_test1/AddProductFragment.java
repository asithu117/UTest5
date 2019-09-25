package com.example.u_test1;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.u_test1.adapter.AdapterOwnProduct;
import com.example.u_test1.adapter.AdapterProduct;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {

    //Firebase
    FirebaseAuth mAuth;


    //Floating Button
    FloatingActionButton fab;
    RecyclerView postsRecyclerView;
    List<productPost> postList;
    AdapterOwnProduct adapterProduct;
    String uId;
    public AddProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        //init firebase
        mAuth = FirebaseAuth.getInstance();
        postList=new ArrayList<>();

        fab = view.findViewById(R.id.fab);
        postsRecyclerView=view.findViewById(R.id.currentUser_ProductRecy);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i=new Intent(getActivity(),Add_Product_Detail.class);
               startActivity(i);
            }
        });
        checkUserStatus();
        loadMyposts();
        return view;
    }

    private void loadMyposts() {
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getActivity(),2);
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
                    adapterProduct = new AdapterOwnProduct(getActivity(),postList);
                    postsRecyclerView.setAdapter(adapterProduct);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void searchMyposts(final String searchQuery) {
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getActivity(),2);
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
                    adapterProduct = new AdapterOwnProduct(getActivity(),postList);
                    postsRecyclerView.setAdapter(adapterProduct);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user=mAuth.getCurrentUser();
        if (user != null){
            uId=user.getUid();
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
        MenuItem item = menu.findItem(R.id.Search_action);
        final androidx.appcompat.widget.SearchView searchView =(androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty( query)){
                    searchMyposts(query);
                }
                else {
                    loadMyposts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty( newText)){
                    searchMyposts(newText);
                }
                else {
                    loadMyposts();
                }
                return false;
            }
        });
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
