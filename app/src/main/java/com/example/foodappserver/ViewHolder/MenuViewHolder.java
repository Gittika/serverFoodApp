package com.example.foodappserver.ViewHolder;



import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.foodappserver.Common;
import com.example.foodappserver.R;
import com.example.foodappserver.ItemClickListener;
import com.example.foodappserver.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

    public TextView textMenuName;
    public ImageView imageView;
private ItemClickListener itemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        textMenuName=(TextView)itemView.findViewById(R.id.Menu_name);
        imageView=(ImageView)itemView.findViewById(R.id.menuImage);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
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
