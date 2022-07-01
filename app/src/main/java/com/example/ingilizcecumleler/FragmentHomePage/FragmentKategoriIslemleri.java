package com.example.ingilizcecumleler.FragmentHomePage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ingilizcecumleler.KategoriIslemleri;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.ActivityFragmentKategoriIslemleriBinding;

public class FragmentKategoriIslemleri extends Fragment {
    private ActivityFragmentKategoriIslemleriBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentKategoriIslemleriBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.CardViewKategoriEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), KategoriIslemleri.class);
                intent.putExtra("id",getString(R.string.KategoriEkle));
                startActivity(intent);
            }
        });
        binding.CardViewKategoriler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), KategoriIslemleri.class);
                intent.putExtra("id",getString(R.string.Kategoriler));
                startActivity(intent);
            }
        });
    }
}
