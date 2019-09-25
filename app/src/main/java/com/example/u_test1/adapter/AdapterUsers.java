package com.example.u_test1.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.u_test1.R;
import com.example.u_test1.ThereProfile;
import com.example.u_test1.Users;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    Context context;
    List<Users> users;

    public AdapterUsers(Context context, List<Users> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String uId=users.get(position).getUid();
        String userImage=users.get(position).getImage();
        String userName=users.get(position).getName();
        final String userEmail=users.get(position).getEmail();
        String userAddress=users.get(position).getAddress();
        String userDivision=users.get(position).getDivision();
        holder.uName.setText(userName);
        holder.uEmail.setText(userEmail);

        try{

            Picasso.get().load(userImage).fit().into(holder.uImage);

        }catch (Exception e){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ThereProfile.class);
                i.putExtra("uId",uId);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView uImage;
        TextView uName,uEmail;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            uImage = itemView.findViewById(R.id.user_Image);
            uName = itemView.findViewById(R.id.user_Name);
            uEmail= itemView.findViewById(R.id.user_Email);
        }
    }
}
