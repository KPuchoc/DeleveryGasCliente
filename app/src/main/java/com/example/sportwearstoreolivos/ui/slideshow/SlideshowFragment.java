package com.example.sportwearstoreolivos.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sportwearstoreolivos.Home;
import com.example.sportwearstoreolivos.SignIn;
import com.example.sportwearstoreolivos.databinding.FragmentSlideshowBinding;

import io.paperdb.Paper;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Paper.init(getContext());
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);

        Paper.book().destroy();
        Intent singIn=new Intent(getContext(), SignIn.class);
        singIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(singIn);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}