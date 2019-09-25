package com.example.u_test1.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.u_test1.Product_Detail;
import com.example.u_test1.R;
import com.example.u_test1.productPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.MyHolder> {

    Context context;
    List<productPost> postList;
    private DatabaseReference likeRef;
    private DatabaseReference postsRef;
    boolean mProcessLike=false;
    FirebaseAuth mAuth;
    FirebaseUser user;

    String myUid;
    public AdapterProduct(Context context, List<productPost> postList) {
        this.context = context;
        this.postList = postList;
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        if (user!=null) {
            myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            postsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater= LayoutInflater.from(context);
        view= mInflater.inflate(R.layout.product_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final String uId = postList.get(position).getUid();
        final String uDp = postList.get(position).getuDp();
        final String uName= postList.get(position).getuName();
        final String uEmail = postList.get(position).getuEmail();
        final String uAddress = postList.get(position).getuAddress();
        final String uDivision = postList.get(position).getuDivision();
        final String uPhone = postList.get(position).getuPhone();
        final String pId = postList.get(position).getPid();
        final String pImage1 = postList.get(position).getpImage1();
        final String pImage2 = postList.get(position).getpImage2();
        final String pImage3 = postList.get(position).getpImage3();
        final String pCategory = postList.get(position).getpCategory();
        final String pName = postList.get(position).getpName();
        final String pDescription = postList.get(position).getpDescription();
        final String pPrize = postList.get(position).getpPrize();
        String pTimeStamp = postList.get(position).getpTime();
        final String pLikes = postList.get(position).getpLikes();
        final String pComments=postList.get(position).getpComment();

        Calendar calendar= Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        final String pTime= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        holder.pCategory.setText(pCategory);
        holder.pName.setText(pName);
        holder.pPrize.setText(pPrize);
        holder.pStars.setText(pLikes+"Favourites");
        holder.pComment.setText(pComments+"Comments");
        if (user!=null){
        setLikes(holder, pId);
        }
        if (pImage1.equals("noImage")){
            holder.pImageTv .setVisibility(View.GONE);
        }else {
            try {

                Picasso.get().load(pImage1).fit().into(holder.pImageTv);

            }catch (Exception e){

            }
        }

        holder.btnStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    final int pLikes = Integer.parseInt(postList.get(position).getpLikes());
                    mProcessLike = true;
                    final String productId = postList.get(position).getPid();
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessLike) {
                                if (dataSnapshot.child(productId).hasChild(myUid)) {
                                    postsRef.child(productId).child("pLikes").setValue("" + (pLikes - 1));
                                    likeRef.child(productId).child(myUid).removeValue();
                                    mProcessLike = false;
                                } else {
                                    postsRef.child(productId).child("pLikes").setValue("" + (pLikes + 1));
                                    likeRef.child(productId).child(myUid).setValue("Liked");
                                    mProcessLike = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(context,"Plese Register...",Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Product_Detail.class);
                i.putExtra("pId",pId);
                i.putExtra("pImage1", pImage1);
                i.putExtra("pImage2", pImage2);
                i.putExtra("pImage3", pImage3);
                i.putExtra("pCategory", pCategory);
                i.putExtra("pName",pName);
                i.putExtra("pPrize",pPrize);
                i.putExtra("pDescription",pDescription);
                i.putExtra("pTime",pTime);
                i.putExtra("uId",uId);
                i.putExtra("uName",uName);
                i.putExtra("uEmail",uEmail);
                i.putExtra("uAddress",uAddress);
                i.putExtra("uDivision",uDivision);
                i.putExtra("uPhone",uPhone);
                i.putExtra("uDp",uDp);
                i.putExtra("currentUid",myUid);
                i.putExtra("pLikes",pLikes);
                context.startActivity(i);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Product_Detail.class);
                i.putExtra("pId",pId);
                i.putExtra("pImage1", pImage1);
                i.putExtra("pImage2", pImage2);
                i.putExtra("pImage3", pImage3);
                i.putExtra("pCategory", pCategory);
                i.putExtra("pName",pName);
                i.putExtra("pPrize",pPrize);
                i.putExtra("pDescription",pDescription);
                i.putExtra("pTime",pTime);
                i.putExtra("uId",uId);
                i.putExtra("uName",uName);
                i.putExtra("uEmail",uEmail);
                i.putExtra("uAddress",uAddress);
                i.putExtra("uDivision",uDivision);
                i.putExtra("uPhone",uPhone);
                i.putExtra("uDp",uDp);
                i.putExtra("currentUid",myUid);
                i.putExtra("pLikes",pLikes);
                context.startActivity(i);
            }
        });

    }

    private void setLikes(final MyHolder holder, final String postKey) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(myUid)){
                    holder.btnStars.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_button_like,0,0,0);

                }
                else {
                    holder.btnStars.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_button,0,0,0);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder{
        ImageView pImageTv;
        TextView pName,pCategory,pPrize,pComment,pStars;
        Button btnStars,btnComment;
        CardView cardView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pImageTv=itemView.findViewById(R.id.product_Image);
            pName=itemView.findViewById(R.id.product_Name);
            pCategory=itemView.findViewById(R.id.product_Category);
            pPrize=itemView.findViewById(R.id.product_Prize);
            pStars=itemView.findViewById(R.id.pStarTv);
            pComment=itemView.findViewById(R.id.p_commentTv);
            btnStars=itemView.findViewById(R.id.product_Star);
            btnComment=itemView.findViewById(R.id.product_Comment);
            cardView=itemView.findViewById(R.id.product_CardView);
        }
    }
}
