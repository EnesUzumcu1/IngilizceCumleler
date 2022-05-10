package com.example.ingilizcecumleler.FragmentKategoriIslemleri;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ingilizcecumleler.Object.Kategoriler;
import com.example.ingilizcecumleler.databinding.ActivityFragmentKategoriEkleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FragmentKategoriEkle extends Fragment {

    FirebaseFirestore firestore;
    CollectionReference kategoriColReference;
    DocumentReference kategoriDocReference;
    int basariliIslemSayisi = 0;

    private ActivityFragmentKategoriEkleBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentKategoriEkleBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        atamalar();
        binding.buttonEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewDurum.setText("...");
                binding.TextInputLayoutKategori.setError(null);
                if(!girdiBosMuKontrol()){
                    kategoriMevcutMu();
                }
            }
        });
    }

    private void atamalar(){
        firestore = FirebaseFirestore.getInstance();
        kategoriColReference = firestore.collection("Kategoriler");
        kategoriDocReference = kategoriColReference.document();
    }

    private void kategoriEkle(){
        //Girdi büyük harfe dönüştürülüp database ekleniyor.
        String girdi = binding.TextInputLayoutKategori.getEditText().getText().toString().trim().toUpperCase();
        Kategoriler yeniKategori = new Kategoriler(girdi);
        //String documentName = MD5.md5(girdi);
        //kategoriColReference.document(documentName).set(yeniKategori);
        kategoriColReference.document().set(yeniKategori);
        binding.textViewDurum.setText(String.format("' %s ' eklendi.", girdi));
    }

    private boolean girdiBosMuKontrol(){
        boolean y = false;
        try {
            String girdi = binding.TextInputLayoutKategori.getEditText().getText().toString().trim();
            if(girdi.isEmpty()){
                binding.TextInputLayoutKategori.setError("Kategori boş bırakılamaz.");
                binding.textViewDurum.setText("Kategori boş bırakılamaz.");
                y = true;
            }
        }catch (Exception e){
            y = false;
        }

        return y;
    }

    private void kategoriMevcutMu(){
        ArrayList<String> durum = new ArrayList<>();
        String girdi = binding.TextInputLayoutKategori.getEditText().getText().toString().trim().toUpperCase();
        durum.add(girdi);
        Query query = kategoriColReference.whereIn("kategoriAdi", durum);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                basariliIslemSayisi = documentSnapshots.size();
                if(basariliIslemSayisi>0){
                    binding.TextInputLayoutKategori.setError("Kategori Zaten Mevcut!");
                    binding.textViewDurum.setText("Kategori Zaten Mevcut!.");
                }
                else if(basariliIslemSayisi ==0){
                    kategoriEkle();
                }
            }
        });
    }
}
