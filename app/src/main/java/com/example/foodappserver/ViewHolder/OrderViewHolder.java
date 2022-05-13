package com.example.foodappserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.foodappserver.Common;
import com.example.foodappserver.ItemClickListener;
import com.example.foodappserver.R;

import info.hoang8f.widget.FButton;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtTotalAmount;
    public FButton delete,track,update,detail;



    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtOrderId =(TextView)itemView.findViewById(R.id.OrderId);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.OrderStatus);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.OrderPhone);
        txtOrderAddress=(TextView)itemView.findViewById(R.id.OrderAddress);
        txtTotalAmount=(TextView)itemView.findViewById(R.id.totalAmount);
        delete=(FButton)itemView.findViewById(R.id.deleteBtn);
       update=(FButton)itemView.findViewById(R.id.updateBtn);
        track=(FButton)itemView.findViewById(R.id.trackBtn);
        detail=(FButton)itemView.findViewById(R.id.detailbtn);




    }


}
