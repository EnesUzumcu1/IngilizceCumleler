package com.example.ingilizcecumleler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.ingilizcecumleler.FragmentKategoriIslemleri.FragmentButunKategoriler;
import com.example.ingilizcecumleler.FragmentKategoriIslemleri.FragmentKategoriDuzenle;
import com.example.ingilizcecumleler.FragmentKategoriIslemleri.FragmentKategoriEkle;
import com.example.ingilizcecumleler.FragmentKategoriIslemleri.FragmentKategoriSil;
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
        else if(id.equals(getString(R.string.KategoriDuzenle))){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentKategoriDuzenle()).commit();
            binding.toolbar.setTitle(getString(R.string.KategoriDuzenle));
        }
        else if(id.equals(getString(R.string.KategoriSil))){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentKategoriSil()).commit();
            binding.toolbar.setTitle(getString(R.string.KategoriSil));
        }
        else if(id.equals(getString(R.string.ButunKategoriler))){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentButunKategoriler()).commit();
            binding.toolbar.setTitle(getString(R.string.ButunKategoriler));
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