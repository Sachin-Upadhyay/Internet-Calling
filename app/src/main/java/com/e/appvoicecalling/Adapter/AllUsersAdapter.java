package com.e.appvoicecalling.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.appvoicecalling.HomeActivity;
import com.e.appvoicecalling.Model.User;
import com.e.appvoicecalling.R;

import java.util.ArrayList;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.AllUsersViewHolder> {

    Context context;
    ArrayList<User> userArrayList;

    public AllUsersAdapter(Context context,ArrayList<User> userArrayList)
    {
        this.context=context;
        this.userArrayList=userArrayList;
    }
    public AllUsersAdapter()
    {}

    @NonNull
    @Override
    public AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.all_users,parent,false);
        AllUsersViewHolder allUsersAdapter= new AllUsersViewHolder(view);
        return allUsersAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull AllUsersViewHolder holder, int position) {
        User user=userArrayList.get(position);
        holder.textView.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class AllUsersViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        Button button;

        public AllUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.itemName);
            button=itemView.findViewById(R.id.callButton);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user=userArrayList.get(getAdapterPosition());
                    ((HomeActivity)context).callUser(user);
                }
            });
        }
    }
}
