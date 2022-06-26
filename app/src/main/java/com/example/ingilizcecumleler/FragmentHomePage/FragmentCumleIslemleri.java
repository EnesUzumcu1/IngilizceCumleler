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
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.ActivityFragmentCumleIslemleriBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FragmentCumleIslemleri extends Fragment {
    private ActivityFragmentCumleIslemleriBinding binding;
    Map<String, String> kategoriIDveAdMap;
    FirebaseFirestore firestore;
    CollectionReference collectionReferenceKategoriler;

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
        binding.CardViewButunCumleler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CumleIslemleri.class);
                intent.putExtra("id",getString(R.string.Cumleler));
                intent.putExtra("map",(Serializable) kategoriIDveAdMap);
                startActivity(intent);
            }
        });
        binding.CardViewGeriDonusumKutusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CumleIslemleri.class);
                intent.putExtra("id",getString(R.string.GeriDonusumKutusu));
                intent.putExtra("map",(Serializable) kategoriIDveAdMap);
                startActivity(intent);
            }
        });
        //Anasayfadayken kategoriler önden yüklenip adaptere verilirken beklenmemesi için burada alınıyor.
        firestore = FirebaseFirestore.getInstance();
        collectionReferenceKategoriler = firestore.collection("Kategoriler");
        kategoriIDveAdMap =new HashMap<>();
        spinnerDoldur();
    }
    private void spinnerDoldur(){
        collectionReferenceKategoriler.orderBy("kategoriAdi", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        String kategori = snapshot.getString("kategoriAdi");
                        kategoriIDveAdMap.put(snapshot.getId(),kategori);
                    }
                }
            }
        });
    }
}
