package com.example.sportwearstoreolivos.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportwearstoreolivos.Interface.ItemClickListener;
import com.example.sportwearstoreolivos.R;

public class SportswearViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView sportswear_name;
    public ImageView sportswear_image,fav_image,share_imagen;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SportswearViewHolder(@NonNull View itemView) {
        super(itemView);
        sportswear_name= (TextView) itemView.findViewById(R.id.sportswear_name);
        sportswear_image= (ImageView) itemView.findViewById(R.id.sportswear_image);
        fav_image=(ImageView)itemView.findViewById(R.id.fav);
        share_imagen=(ImageView)itemView.findViewById(R.id.btnShare);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
