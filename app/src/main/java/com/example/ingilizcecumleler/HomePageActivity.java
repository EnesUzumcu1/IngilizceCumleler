package com.example.ingilizcecumleler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ingilizcecumleler.Adapter.ViewPagerAdapter;
import com.example.ingilizcecumleler.databinding.ActivityHomePageBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;


public class HomePageActivity extends AppCompatActivity {
    private ActivityHomePageBinding binding;
    private ViewPagerAdapter viewPagerAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        atamalar();
        viewPagerTabLayout();
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //ekran yan kaydırılarak değiştirildiğinde tabLayout itemleride kendini günceller.
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        binding.imageViewLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                digerSayfayaGit(LoginActivity.class);
            }
        });
    }


    private void atamalar() {
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
    }

    private void viewPagerTabLayout(){
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(viewPagerAdapter);
    }

    public void digerSayfayaGit(Class<?> classAdi){
        Intent intent = new Intent(HomePageActivity.this,classAdi);
        startActivity(intent);
    }


}