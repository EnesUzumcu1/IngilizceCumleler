package com.example.ingilizcecumleler.FragmentKategoriIslemleri;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ingilizcecumleler.Adapter.ButunKategorilerAdapter;
import com.example.ingilizcecumleler.Object.Kategoriler;
import com.example.ingilizcecumleler.databinding.ActivityFragmentButunKategorilerBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FragmentButunKategoriler extends Fragment {
    private ActivityFragmentButunKategorilerBinding binding;
    private ButunKategorilerAdapter adapter;
    FirebaseFirestore firestore;
    CollectionReference collectionReferenceKategori;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentButunKategorilerBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        atamalar();
        listeyiHazirla();
    }

    private void atamalar(){
        firestore = FirebaseFirestore.getInstance();
        collectionReferenceKategori = firestore.collection("Kategoriler");
    }

    private void listeyiHazirla(){
        Query query = collectionReferenceKategori;
        query = query.orderBy("kategoriAdi", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Kategoriler> ayarla =new FirestoreRecyclerOptions.Builder<Kategoriler>().setQuery(query,Kategoriler.class).build();
        adapter = new ButunKategorilerAdapter(ayarla);
        binding.butunKategorilerRecyclerView.setHasFixedSize(true);
        binding.butunKategorilerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.butunKategorilerRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
