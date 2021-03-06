package com.krishna.team_olive.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.team_olive.ModelClasses.MyAddModel;
import com.krishna.team_olive.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAddsAdapter extends RecyclerView.Adapter<MyAddsAdapter.ViewHolder> {
    Context context;
    ArrayList<MyAddModel> list;

    public MyAddsAdapter(Context context, ArrayList<MyAddModel> list) {
        this.context = context;
        this.list = list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_product_photo;
        TextView tv_product_name;
        TextView tv_exchanged_cateogary;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_product_photo=itemView.findViewById(R.id.iv_postimg);
            tv_product_name=itemView.findViewById(R.id.tv_postitemname);
            tv_exchanged_cateogary=itemView.findViewById(R.id.tv_exchange02);
        }
    }


    @NonNull
    @Override
    public MyAddsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_add_item,parent,false);
        return new MyAddsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAddsAdapter.ViewHolder holder, int position) {
        holder.tv_product_name.setText(list.get(position).getName());
        holder.tv_exchanged_cateogary.setText(list.get(position).getCateogary());

        holder.iv_product_photo.setImageResource(R.drawable.ic_ads);
        if(list.get(position).getUri()!=null  && !(list.get(position).getUri().equals("")))
            Picasso.get().load(list.get(position).getUri()).placeholder(R.drawable.ic_ads).into(holder.iv_product_photo);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
