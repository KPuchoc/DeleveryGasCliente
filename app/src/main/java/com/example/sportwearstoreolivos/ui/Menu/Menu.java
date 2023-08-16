package com.example.sportwearstoreolivos.ui.Menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sportwearstoreolivos.Home;
import com.example.sportwearstoreolivos.R;


public class Menu extends Fragment {


    public Menu() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent singIn=new Intent(getContext(), Home.class);

        startActivity(singIn);
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }
}