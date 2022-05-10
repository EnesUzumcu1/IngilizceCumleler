package com.example.ingilizcecumleler.FragmentHomePage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ingilizcecumleler.CumleIslemleri;
import com.example.ingilizcecumleler.KategoriIslemleri;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.ActivityFragmentCumleIslemleriBinding;

public class FragmentCumleIslemleri extends Fragment {
    private ActivityFragmentCumleIslemleriBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentCumleIslemleriBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.CardViewCumleEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CumleIslemleri.class);
                intent.putExtra("id",getString(R.string.CumleEkle));
                startActivity(intent);
            }
        });
        binding.CardViewCumleDuzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CumleIslemleri.class);
                intent.putExtra("id",getString(R.string.CumleDuzenle));
                startActivity(intent);
            }
        });
        binding.CardViewCumleSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CumleIslemleri.class);
                intent.putExtra("id",getString(R.string.CumleSil));
                startActivity(intent);
            }
        });
        binding.CardViewButunCumleler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CumleIslemleri.class);
                intent.putExtra("id",getString(R.string.ButunCumleler));
                startActivity(intent);
            }
        });
        binding.CardViewGeriDonusumKutusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CumleIslemleri.class);
                intent.putExtra("id",getString(R.string.GeriDonusumKutusu));
                startActivity(intent);
            }
        });
    }
}
