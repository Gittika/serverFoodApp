package com.example.foodappserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodappserver.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView nav;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    LinearLayout container;
EditText new_categoryTxt;
    TextView currentUserName;
    TextView currentUserEmail;
    FButton btnUpload,btnSelect;
    Toolbar toolbar;
    RecyclerView recycler_menu;
    FloatingActionButton floatingActionButton;
    RecyclerView.LayoutManager layoutManager;
    Category newCategory;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    Uri savUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        nav=(NavigationView)findViewById(R.id.navmenu);
        nav.setNavigationItemSelectedListener( this);
        View headerView=nav.getHeaderView(0);
        currentUserName =(TextView)headerView.findViewById(R.id.UserFullName);
        currentUserEmail=(TextView)headerView.findViewById(R.id.UserEmailId);






        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);


        toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Mangement");







        floatingActionButton=(FloatingActionButton)findViewById(R.id.cartFbBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showDialog();
            }
        });

        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Category");
        storage =FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        toggle =new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        //Load menu
        recycler_menu =(RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        loadMenu();

    }
          private  void showDialog() {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
    alertDialog.setTitle("Add new Category");
    alertDialog.setMessage("Please fill full information");

    LayoutInflater inflater = this.getLayoutInflater();
    View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout, null);


    new_categoryTxt = add_menu_layout.findViewById(R.id.newEditTxt);
    btnSelect = add_menu_layout.findViewById(R.id.selectBtn);
    btnUpload = add_menu_layout.findViewById(R.id.uploadBtn);
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
            if(newCategory!=null){
                reference.push().setValue(newCategory);
                Toast.makeText(HomeActivity.this,"New Category "+newCategory.getName()+"was added successfully",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(HomeActivity.this ,"Uploaded!!!!",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set value for new category image upload and we get download link
                                    newCategory =new Category(new_categoryTxt.getText().toString(),uri.toString());

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(HomeActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
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

    private void changeImage(Category item) {
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
                            Toast.makeText(HomeActivity.this ,"Uploaded!!!!",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(HomeActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
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

    private void chooseImage(){
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);


    }

    private void deleteCategory(String key) {
        reference.child(key).removeValue();

    }

    private void showUpdateDialog(String key, Category item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout, null);


        new_categoryTxt = add_menu_layout.findViewById(R.id.newEditTxt);
        btnSelect = add_menu_layout.findViewById(R.id.selectBtn);
        btnUpload = add_menu_layout.findViewById(R.id.uploadBtn);
        //set default name
        new_categoryTxt.setText(item.getName());
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
//here just create new category
                item.setName(new_categoryTxt.getText().toString());
                reference.child(key).setValue(item);

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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==RESULT_OK
        && data!=null && data.getData()!=null){
            savUri =data.getData();
            btnSelect.setText("Image Selected");

        }
    }




    private  void loadMenu() {

        adapter =new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menulist,MenuViewHolder.class,reference) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int i) {
                viewHolder.textMenuName.setText(model.getName());
                Glide.with(viewHolder.imageView.getContext()).load(model.getImage()).into(viewHolder.imageView);
                final Category clickItem=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                       Intent intent =new Intent(HomeActivity.this,FoodListActivity.class);
                       intent.putExtra("CategoryId",adapter.getRef(position).getKey());
                       startActivity(intent);



                    }
                });
            }





        };
        adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(adapter);
    }

//Update and delete //press Ctrl +0


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }else if (item.getTitle().equals(Common.DELETE)) {

            deleteCategory(adapter.getRef(item.getOrder()).getKey());

        }


        return super.onContextItemSelected(item);
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if(id==R.id.menu_home){
            Intent intent =new Intent(HomeActivity.this,HomeActivity.class);
            startActivity(intent);


        }
        if(id==R.id.menu_cart){



        }
        if(id==R.id.menu_order){
            Intent intent=new Intent(HomeActivity.this,OrderActivity.class);
            startActivity(intent);


        }
        if(id==R.id.menu_logout){
            Intent intent =new Intent(HomeActivity.this,SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
    }
