package com.example.foodappserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodappserver.Common;
import com.example.foodappserver.ItemClickListener;
import com.example.foodappserver.R;


public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {
public TextView food_name;
public ImageView food_image;
public TextView food_price;

private ItemClickListener itemClickListener;


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        food_name =(TextView)itemView.findViewById(R.id.foodTxt);
        food_price =(TextView)itemView.findViewById(R.id.priceTxt);
        food_image =(ImageView)itemView.findViewById(R.id.img1);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAbsoluteAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");
        menu.add(0,0,getAbsoluteAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAbsoluteAdapterPosition(), Common.DELETE);
    }
}
