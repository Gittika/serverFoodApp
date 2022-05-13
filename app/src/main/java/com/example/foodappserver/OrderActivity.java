package com.example.foodappserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.foodappserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderActivity extends AppCompatActivity {
   MaterialSpinner spinner;
     RecyclerView recyclerView;
       RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseRecyclerAdapter<Request , OrderViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().setTitle("Customer Orders");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        database= FirebaseDatabase.getInstance();
        reference =database.getReference("Request");
        recyclerView =(RecyclerView)findViewById(R.id.orderList);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        loadOrders();

    }


    private void loadOrders() {
        adapter =new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                reference
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Request request, int i) {
                orderViewHolder.txtOrderId.setText(adapter.getRef(i).getKey());
                orderViewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(request.getStatus()));
                orderViewHolder.txtOrderPhone.setText(request.getName());
                orderViewHolder.txtOrderAddress.setText(request.getAddress());
                orderViewHolder.txtTotalAmount.setText(request.getTotal());

                orderViewHolder.track.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrderActivity.this, TrackingActivity.class);
                        Common.currentRequest = request;
                        startActivity(intent);

                    }
                });


                orderViewHolder.detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrderActivity.this,OrderDetailActivity.class);
                        startActivity(intent);

                    }
                });


                orderViewHolder.update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(i).getKey(), adapter.getItem(i));

                    }
                });


                orderViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(i).getKey());

                    }
                });





            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


    }


   /* public boolean onContextItemSelected(@NonNull MenuItem item) {
       if(item.getTitle().equals(Common.UPDATE)) {
           showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
           return super.onContextItemSelected(item);
       }
        else if(item.getTitle().equals(Common.DELETE))
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        return super.onContextItemSelected(item);
    }*/

    private void deleteOrder(String key) {

        reference.child(key).removeValue();
    }

    private void showUpdateDialog(String key, Request item) {
        final AlertDialog.Builder alertDialog =new AlertDialog.Builder(OrderActivity.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");
        LayoutInflater inflater =this.getLayoutInflater();
        final  View view =inflater.inflate(R.layout.update_order_layout,null);

        spinner =(MaterialSpinner)view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","On my Way","Shipped");

        alertDialog.setView(view);


        final String localKey =key;
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                reference.child(localKey).setValue(item);

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             dialog.dismiss();

            }
        });
        alertDialog.show();



    }
}