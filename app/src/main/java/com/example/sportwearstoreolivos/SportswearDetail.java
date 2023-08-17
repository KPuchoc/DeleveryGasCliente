package com.example.sportwearstoreolivos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.sportwearstoreolivos.Common.Common;
import com.example.sportwearstoreolivos.Database.Database;
import com.example.sportwearstoreolivos.Model.Order;
import com.example.sportwearstoreolivos.Model.Rating;
import com.example.sportwearstoreolivos.Model.Sportswear;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class SportswearDetail extends AppCompatActivity implements RatingDialogListener {

    TextView sportswear_name,sportswear_price,sportswear_description;
    ImageView sportswear_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart,btnRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;
    String Id="";

    FirebaseDatabase database;
    DatabaseReference sportswear;
    DatabaseReference ratingTbl;

    Sportswear currentSportswear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportswear_detail);

        database=FirebaseDatabase.getInstance();
        sportswear=database.getReference("Sportswear");
        ratingTbl=database.getReference("Rating");

        //vid-04 19:20
        numberButton=(ElegantNumberButton) findViewById(R.id.number_button);
        btnCart=(FloatingActionButton) findViewById(R.id.btnCart);
        btnRating=(FloatingActionButton)findViewById(R.id.btn_rating);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        Id,
                        currentSportswear.getName(),
                        numberButton.getNumber(),
                        currentSportswear.getPrice(),
                        currentSportswear.getDiscount()


                ));
                Toast.makeText(SportswearDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        sportswear_description=(TextView)findViewById(R.id.sportswear_description);
        sportswear_name=(TextView) findViewById(R.id.sportswear_name);
        sportswear_price=(TextView) findViewById(R.id.sportswear_price);
        sportswear_image=(ImageView) findViewById(R.id.img_Sportswear);

        collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent()!=null)
            Id=getIntent().getStringExtra("id");
        if(!Id.isEmpty())
        {
            if (Common.isConnectedToInternet(getBaseContext()))
            {
                getDetailSportswear(Id);
                getRatingFood(Id);
            }
            else
            {
                Toast.makeText(SportswearDetail.this, "Por favor chequea tu Conecxion", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void getRatingFood(String id) {
        com.google.firebase.database.Query sportswearRating=ratingTbl.orderByChild("id").equalTo(id);
        sportswearRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Rating item=postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;

                }
                if(count!=0)
                {
                    float average= sum/count;
                    ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ShowRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Sportswear")
                .setDescription("Please select some stars and give your feddback")
                .setTitleTextColor(R.color.overlayBackground)
                .setDescriptionTextColor(R.color.overlayBackground)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.overlayBackground)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.overlayBackground)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(SportswearDetail.this)
                .show();



    }

    private void getDetailSportswear(String id) {
        sportswear.child(Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentSportswear=dataSnapshot.getValue(Sportswear.class);
                Picasso.with(getBaseContext()).load(currentSportswear.getImage()).into(sportswear_image);

                collapsingToolbarLayout.setTitle(currentSportswear.getName());

                sportswear_price.setText(currentSportswear.getPrice());

                sportswear_name.setText(currentSportswear.getName());

                sportswear_description.setText(currentSportswear.getDescription());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NonNull String comments) {
        Rating rating=new Rating(Common.currentUser.getPhone(),
                Id,
                String.valueOf(value),
                comments);
        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentUser.getPhone()).exists())
                {
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();

                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                else
                {
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(SportswearDetail.this, "Thank you for submit rating", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}