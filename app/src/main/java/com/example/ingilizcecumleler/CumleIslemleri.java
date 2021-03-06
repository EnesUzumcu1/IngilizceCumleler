package com.example.ingilizcecumleler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.ingilizcecumleler.FragmentCumleIslemleri.FragmentCumleListesi;
import com.example.ingilizcecumleler.FragmentCumleIslemleri.FragmentCumleEkle;
import com.example.ingilizcecumleler.databinding.ActivityCumleIslemleriBinding;

public class CumleIslemleri extends AppCompatActivity {
    private ActivityCumleIslemleriBinding binding;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        atamalar();
        fragmentSec();
        fragmentYukle();
        toolBarAyarla();
    }

    private void atamalar(){
        binding = ActivityCumleIslemleriBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    private void fragmentSec(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            id = bundle.getString("id");
        }
    }
    private void fragmentYukle(){
        if(id.equals(getString(R.string.CumleEkle))){
            getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(),new FragmentCumleEkle()).commit();
            binding.toolbar.setTitle(getString(R.string.CumleEkle));
        }
        else if(id.equals(getString(R.string.Cumleler))){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentCumleListesi()).commit();
            binding.toolbar.setTitle(getString(R.string.Cumleler));
        }
        else if(id.equals(getString(R.string.GeriDonusumKutusu))){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentCumleListesi()).commit();
            binding.toolbar.setTitle(getString(R.string.GeriDonusumKutusu));
        }
    }

    private void toolBarAyarla(){
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Geri Btonuna bas??ld??????nda yap??lacaklar ayarland??.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}