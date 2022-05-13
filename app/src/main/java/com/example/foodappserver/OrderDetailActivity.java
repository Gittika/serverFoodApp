package com.example.foodappserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.foodappserver.ViewHolder.OrderDetailAdapter;

public class OrderDetailActivity extends AppCompatActivity {
TextView order_total;
RecyclerView lstFoods;
RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        order_total=(TextView)findViewById(R.id.totalAmountDetail);
        lstFoods =(RecyclerView)findViewById(R.id.orderDetailRecycle);
        lstFoods.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        lstFoods.setLayoutManager(layoutManager);
        order_total.setText(Common.currentRequest.getTotal());

        OrderDetailAdapter adapter =new OrderDetailAdapter(Common.currentRequest.getFoods());
       adapter.notifyDataSetChanged();
        lstFoods.setAdapter(adapter);
    }
}