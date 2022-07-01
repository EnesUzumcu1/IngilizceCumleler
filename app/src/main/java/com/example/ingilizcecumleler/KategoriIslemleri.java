package com.example.ingilizcecumleler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.ingilizcecumleler.FragmentKategoriIslemleri.FragmentKategoriListesi;
import com.example.ingilizcecumleler.FragmentKategoriIslemleri.FragmentKategoriEkle;
import com.example.ingilizcecumleler.databinding.ActivityKategoriIslemleriBinding;

public class KategoriIslemleri extends AppCompatActivity {
    private ActivityKategoriIslemleriBinding binding;
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
        binding = ActivityKategoriIslemleriBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void fragmentSec(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            id = bundle.getString("id");
        }
    }
    private void fragmentYukle(){
        if(id.equals(getString(R.string.KategoriEkle))){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentKategoriEkle()).commit();
            binding.toolbar.setTitle(getString(R.string.KategoriEkle));
        }
        else if(id.equals(getString(R.string.Kategoriler))){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentKategoriListesi()).commit();
            binding.toolbar.setTitle(getString(R.string.Kategoriler));
        }
    }

    private void toolBarAyarla(){
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Geri Btonuna basıldığında yapılacaklar ayarlandı.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}