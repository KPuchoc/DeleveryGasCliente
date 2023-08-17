package com.example.sportwearstoreolivos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sportwearstoreolivos.Common.Common;
import com.example.sportwearstoreolivos.Interface.ItemClickListener;
import com.example.sportwearstoreolivos.Model.Category;
import com.example.sportwearstoreolivos.Model.Token;
import com.example.sportwearstoreolivos.Service.ListenOrder;
import com.example.sportwearstoreolivos.ViewHolder.MenuViewHolder;
import com.example.sportwearstoreolivos.databinding.ActivityHomeBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    FirebaseDatabase database;
    DatabaseReference category;
    TextView txtFullName;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager   layoutManager;

    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
 //       Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
//       toolbar.setTitle("Menu");


     //   swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout);

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if(Common.isConnectedToInternet(getBaseContext()))
//        loadMenu();
//        else
//        {
//            Toast.makeText(getBaseContext(), "Por favor chequea tu Conecxion", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//            }
//        });
//
//        swipeRefreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                if(Common.isConnectedToInternet(getBaseContext()))
//        loadMenu();
//        else
//        {
//            Toast.makeText(getBaseContext(), "Por favor chequea tu Conecxion", Toast.LENGTH_SHORT).show();
//            return;
//        }
//            }
//        });




        setSupportActionBar(binding.appBarHome.toolbar);

        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category");

        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
             //           .setAction("Action", null).show();
                Intent cartIntent=new Intent(Home.this,Cart.class);
                startActivity(cartIntent);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_cart, R.id.nav_orders,R.id.nav_ChangePassword,R.id.nav_log_out)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView=navigationView.getHeaderView(0);
        txtFullName=(TextView) headerView.findViewById(R.id.ttxtFullName);
        txtFullName.setText(Common.currentUser.getName());

        recycler_menu=(RecyclerView)findViewById(R.id.reciclerMenu);
        recycler_menu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        if(Common.isConnectedToInternet(this))
        loadMenu();
        else
        {
            Toast.makeText(this, "Por favor chequea tu Conecxion", Toast.LENGTH_SHORT).show();
            return;
        }

      Intent service= new Intent(Home.this, ListenOrder.class);
      startService(service);

//        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(token,false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu() {

        //adaptador menu-.item
        adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>( Category.class,R.layout.menu_item,MenuViewHolder.class,category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                final Category clickItem =model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                      //  Toast.makeText(Home.this, ""+clickItem.getImage(), Toast.LENGTH_SHORT).show();
                        //lista del menu
                        Intent sportswearList=new Intent(Home.this,SportswearList.class);
                        sportswearList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(sportswearList);
                    }
                });

            }
        };
        recycler_menu.setAdapter(adapter);
   //     swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.refresh)
            loadMenu();
       else if(item.getItemId()==R.id.changepass)
          showChangePasswordDialog();
        else if(item.getItemId()==R.id.action_settings)
            showSettings();
        return super.onOptionsItemSelected(item);
    }

    private void showSettings() {
        Intent cartIntent=new Intent(Home.this,settingsac.class);
        startActivity(cartIntent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();

        if(id==R.id.nav_home){

        }else if(id==R.id.nav_cart){
            Intent cartIntent=new Intent(Home.this,Cart.class);
            startActivity(cartIntent);

        } else if(id==R.id.nav_orders){
            Intent orderIntent=new Intent(Home.this,OrderStatus.class);
            startActivity(orderIntent);

        }else if(id==R.id.nav_log_out){
            Paper.book().destroy();
            Intent singIn=new Intent(Home.this, SignIn.class);
            singIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(singIn);
        }
        else if(id==R.id.nav_ChangePassword)
        {
          showChangePasswordDialog();
        }
        DrawerLayout drawer=(DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


       return true;
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Please fill all information");
        LayoutInflater inflater=LayoutInflater.from(this);

        View layout_pwd=inflater.inflate(R.layout.change_password_layout,null);
        final MaterialEditText edtPassword=(MaterialEditText) layout_pwd.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword=(MaterialEditText) layout_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword=(MaterialEditText) layout_pwd.findViewById(R.id.edtRepeatPassword);

        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                android.app.AlertDialog waitingDialog=new SpotsDialog(Home.this);
                waitingDialog.show();

                if(edtPassword.getText().toString().equals(Common.currentUser.getPassword()))
                {
                    if(edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString()))
                    {
                        Map<String,Object>passwordUpdate =new HashMap<>();
                        passwordUpdate.put("password",edtPassword.getText().toString());


                        DatabaseReference user=FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this, "Password was update", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                else
                {   waitingDialog.dismiss();
                    Toast.makeText(Home.this, "New Password doesn´t match", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

            }
        });
        alertDialog.show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}