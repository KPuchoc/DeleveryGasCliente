package com.example.sportwearstoreolivos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sportwearstoreolivos.Common.Common;
import com.example.sportwearstoreolivos.Database.Database;
import com.example.sportwearstoreolivos.Interface.ItemClickListener;
import com.example.sportwearstoreolivos.Model.Sportswear;
import com.example.sportwearstoreolivos.ViewHolder.SportswearViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class SportswearList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference sportswearlist;
    String categoryId="";
    FirebaseRecyclerAdapter<Sportswear, SportswearViewHolder> adapter;


    //buscador
    FirebaseRecyclerAdapter<Sportswear, SportswearViewHolder> searchAdapter;
    List<String> suggestList  =new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    //Favorite
    Database localDB;


    CallbackManager callbackManager;
    ShareDialog shareDialog;

    Target target=new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto photo=new  SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content=new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);

            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportswear_list);
        shareDialog=new ShareDialog(this);


        callbackManager=CallbackManager.Factory.create();

        //base de datos
        database=FirebaseDatabase.getInstance();
        sportswearlist=database.getReference("Sportswear");

        //LOCAL DB
        localDB=new Database(this);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerSportswear);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if(getIntent()!=null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty()&&categoryId!=null)
        {
            if(Common.isConnectedToInternet(getBaseContext()))
            loadsportswear(categoryId);
            else
            {
                Toast.makeText(SportswearList.this, "Por favor chequea tu Conecxion", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //buscador
       materialSearchBar=(MaterialSearchBar) findViewById(R.id.searchBar);
       materialSearchBar.setHint("Buscar");

       loadSuggest();
       materialSearchBar.setLastSuggestions(suggestList);
       materialSearchBar.setCardViewElevation(10);
       materialSearchBar.addTextChangeListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence s, int i, int i1, int i2) {
               List<String>suggest=new ArrayList<>();
               for(String search:suggestList) //lista de sugerencias
               {
                   if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                       suggest.add(search);
               }

           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });
       materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
           @Override
           public void onSearchStateChanged(boolean enabled) {
               if(!enabled)
                   recyclerView.setAdapter(adapter);
           }

           @Override
           public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
           }

           @Override
           public void onButtonClicked(int buttonCode) {

           }
       });
    }

    private void startSearch(CharSequence text) {
        searchAdapter=new FirebaseRecyclerAdapter<Sportswear, SportswearViewHolder>(
              Sportswear.class,
              R.layout.sportswear_item,
              SportswearViewHolder.class,
              sportswearlist.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(SportswearViewHolder viewHolder, Sportswear model, int position) {
                viewHolder.sportswear_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.sportswear_image);

                final Sportswear local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //  Toast.makeText(SportswearList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent SportwearDetail=new Intent(SportswearList.this,SportswearDetail.class);
                        SportwearDetail.putExtra("id",searchAdapter.getRef(position).getKey());
                        startActivity(SportwearDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    //
    private void loadSuggest() {
        sportswearlist.orderByChild("id").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Sportswear item=postSnapshot.getValue(Sportswear.class);
                            suggestList.add(item.getName());
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void loadsportswear(String categoryId) {
    adapter=new FirebaseRecyclerAdapter<Sportswear, SportswearViewHolder>(Sportswear.class,R.layout.sportswear_item,
            SportswearViewHolder.class,
            sportswearlist.orderByChild("id").equalTo(categoryId))
    {
        @Override
        protected void populateViewHolder(SportswearViewHolder viewHolder, Sportswear model, int position) {
            viewHolder.sportswear_name.setText(model.getName());
            Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.sportswear_image);

            //add favorite
            if(localDB.isFavorite(adapter.getRef(position).getKey()))
                viewHolder.fav_image.setImageResource(R.drawable.ic_bfavorite_on);

            //share Feisbuck :v
            viewHolder.share_imagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Picasso.with(getApplicationContext())
                            .load(model.getImage())
                            .into(target);

                }
            });

            //Click
            viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!localDB.isFavorite(adapter.getRef(position).getKey()))
                    {
                        localDB.addToFavorites(adapter.getRef(position).getKey());
                        viewHolder.fav_image.setImageResource(R.drawable.ic_bfavorite_on);
                        Toast.makeText(SportswearList.this, ""+model.getName()+"Was added to Favorites", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        localDB.removeToFavorites(adapter.getRef(position).getKey());
                        viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_off);
                        Toast.makeText(SportswearList.this, ""+model.getName()+"Was removed from Favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //vid-4 17:05
            final Sportswear local=model;
            viewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                  //  Toast.makeText(SportswearList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                    Intent SportwearDetail=new Intent(SportswearList.this,SportswearDetail.class);
                    SportwearDetail.putExtra("id",adapter.getRef(position).getKey());
                    startActivity(SportwearDetail);
                }
            });
        }
    };



    Log.d("TAG",""+adapter.getItemCount());
    recyclerView.setAdapter(adapter);
    }
}