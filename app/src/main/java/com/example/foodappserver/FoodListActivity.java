package com.example.foodappserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodappserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodListActivity extends AppCompatActivity {
    RecyclerView recview;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    String categoryId="";
    Toolbar toolbar;
    TextView Food_name,Food_price,Food_description;
    FButton btnUpload,btnSelect;
    EditText new_foodTxt,new_priceTxt,new_descrptionTxt;
    ImageView Food_img;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton floatingActionButton;

      Uri savUri;
    Model currentFood;
    String foodId;
   Model newFood;
    FirebaseRecyclerAdapter<Model, FoodViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        getSupportActionBar().hide();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Food");
        storage=FirebaseStorage.getInstance();
        storageReference =storage.getReference();

        Food_name=(TextView)findViewById(R.id.foodTxt);
        Food_img=(ImageView)findViewById(R.id.img1);
        Food_price=(TextView)findViewById(R.id.priceTxt);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.newFoodBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });






        recview = (RecyclerView) findViewById(R.id.recview);
        recview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recview.setLayoutManager(layoutManager);
        //GetiNTENT
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty() && categoryId != null) {
            loadListFood(categoryId);

        }
    }

    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Model, FoodViewHolder>(Model.class, R.layout.list, FoodViewHolder.class, reference.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Model model, int i) { foodViewHolder.food_name.setText(model.getName());
                foodViewHolder.food_price.setText(model.getPrice());
                Glide.with(foodViewHolder.food_image.getContext()).load(model.getImage()).into(foodViewHolder.food_image);
                final Model local = model;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        foodId=adapter.getRef(position).getKey();

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recview.setAdapter(adapter);
    }

    private  void showAddFoodDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodListActivity.this);
        alertDialog.setTitle("Add new item");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);


        new_foodTxt = add_menu_layout.findViewById(R.id.newEditTxt);
        new_priceTxt = add_menu_layout.findViewById(R.id.newEditTxtPrice);
        new_descrptionTxt = add_menu_layout.findViewById(R.id.newEditTxtDescription);
        btnSelect = add_menu_layout.findViewById(R.id.selectFoodBtn);
        btnUpload = add_menu_layout.findViewById(R.id.uploadFoodBtn);
        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();// Let user select image from gallery and save uri of this
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.bag);
        //set button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//here just create new category
                if(newFood!=null){
                    reference.push().setValue(newFood);
                    Toast.makeText(FoodListActivity.this,"New Food "+newFood.getName()+"was added successfully",Toast.LENGTH_SHORT).show();
                }
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

    private void chooseImage(){
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);


    }

    private void uploadImage() {
        if(savUri!=null){
            ProgressDialog mdialog =new ProgressDialog(this);
            mdialog.setMessage("Uploading....");
            mdialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder =storageReference.child("images/*"+imageName);
            imageFolder.putFile(savUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mdialog.dismiss();
                            Toast.makeText(FoodListActivity.this ,"Uploaded!!!!",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newFood =new Model();
                                    //set value for new category image upload and we get download link
                                   newFood.setName(new_foodTxt.getText().toString());
                                   newFood.setPrice(new_priceTxt.getText().toString());
                                   newFood.setDescription(new_descrptionTxt.getText().toString());
                                   newFood.setMenuId(categoryId);
                                   newFood.setImage(uri.toString());

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(FoodListActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress =(100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                            mdialog.setMessage("Uploaded"+progress+"%");
                        }
                    });
        }

    }

    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }else if (item.getTitle().equals(Common.DELETE)) {

            deleteCategory(adapter.getRef(item.getOrder()).getKey());

        }


        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(String key, Model item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodListActivity.this);
        alertDialog.setTitle("Update new Food");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);


        new_foodTxt = add_menu_layout.findViewById(R.id.newEditTxt);
        new_priceTxt = add_menu_layout.findViewById(R.id.newEditTxtPrice);
       new_descrptionTxt = add_menu_layout.findViewById(R.id.newEditTxtDescription);
        btnSelect = add_menu_layout.findViewById(R.id.selectFoodBtn);
        btnUpload = add_menu_layout.findViewById(R.id.uploadFoodBtn);
        //set default name
       new_foodTxt.setText(item.getName());
       new_priceTxt.setText(item.getPrice());
       new_descrptionTxt.setText(item.getDescription());

        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();// Let user select image from gallery and save uri of this
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.bag);
        //set button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//here just create new food

                    item.setName(new_foodTxt.getText().toString());
                    item.setPrice(new_priceTxt.getText().toString());
                    item.setDescription(new_descrptionTxt.getText().toString());

                    reference.child(key).setValue(item);

                    Toast.makeText(FoodListActivity.this,"Dish"+item.getName()+"was added",Toast.LENGTH_SHORT).show();


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

    private void changeImage(final Model item) {
        if(savUri!=null){
            ProgressDialog mdialog =new ProgressDialog(this);
            mdialog.setMessage("Uploading....");
            mdialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder =storageReference.child("images/*"+imageName);
            imageFolder.putFile(savUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mdialog.dismiss();
                            Toast.makeText(FoodListActivity.this ,"Uploaded!!!!",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set value for new category image upload and we get download link
                                    item.setImage(uri.toString());

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(FoodListActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress =(100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                            mdialog.setMessage("Uploaded"+progress+"%");
                        }
                    });
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==RESULT_OK
                && data!=null && data.getData()!=null){
            savUri =data.getData();
            btnSelect.setText("Image Selected");

        }
    }

    private void deleteCategory(String key) {
        reference.child(key).removeValue();

    }
}