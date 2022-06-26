package com.example.ingilizcecumleler.FragmentCumleIslemleri;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ingilizcecumleler.Object.Cumleler;
import com.example.ingilizcecumleler.databinding.ActivityFragmentCumleEkleBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentCumleEkle extends Fragment {
    private ActivityFragmentCumleEkleBinding binding;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReferenceKategoriler;
    private CollectionReference collectionReferenceCumleler;
    ArrayAdapter<String> arrayAdapter;
    List<String> kategorilerList;
    String taskID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentCumleEkleBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        atamalar();
        spinnerAdapterAyarla();
        spinnerDoldur();
        binding.ekleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!girdiBosMuKontrolEN()||!girdiBosMuKontrolTR()){
                    cumleMevcutMu();
                    binding.scrollView.fullScroll(View.FOCUS_DOWN);
                    binding.ekleButton.setEnabled(false);
                }
            }
        });
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String kategori =binding.spinner.getItemAtPosition(position).toString();

                Query query = collectionReferenceKategoriler.whereEqualTo("kategoriAdi",kategori);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            //secilen kategorinin ID'si aliniyor.
                            taskID = task.getResult().getDocuments().get(0).getId();
                        }
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void cumleEkle(){
        String englishSentence =binding.TextInputLayoutEnglishSentence.getEditText().getText().toString().trim().toLowerCase(Locale.ENGLISH);
        String turkceCumle = binding.TextInputLayoutTurkceCumle.getEditText().getText().toString().trim().toLowerCase(new Locale("tr","TR"));
        Cumleler yeniCumle = new Cumleler(turkceCumle,englishSentence,taskID,false);
        collectionReferenceCumleler.add(yeniCumle);
        binding.uyariTextView.setText("Eklendi.");
    }

    private void cumleMevcutMu(){
        String girdi = binding.TextInputLayoutTurkceCumle.getEditText().getText().toString().trim().toLowerCase(Locale.ENGLISH);
        String girdi2 = binding.TextInputLayoutEnglishSentence.getEditText().getText().toString().trim().toLowerCase(new Locale("tr","TR"));
        Query query = collectionReferenceCumleler.whereEqualTo("tr", girdi);
        query.whereEqualTo("en",girdi2);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                int basariliislem = documentSnapshots.size();
                if(basariliislem==0){
                    cumleEkle();
                }
                else{
                    binding.uyariTextView.setText("Cümle zaten mevcut.");
                }
                binding.TextInputLayoutTurkceCumle.getEditText().setText("");
                binding.TextInputLayoutEnglishSentence.getEditText().setText("");
                binding.TextInputLayoutEnglishSentence.clearFocus();
                binding.TextInputLayoutTurkceCumle.clearFocus();
                binding.ekleButton.setEnabled(true);
            }
        });
    }


    private void atamalar(){
        firestore = FirebaseFirestore.getInstance();
        collectionReferenceKategoriler = firestore.collection("Kategoriler");
        collectionReferenceCumleler = firestore.collection("Cumleler");
        kategorilerList = new ArrayList<String>() {};
    }

    private void spinnerAdapterAyarla(){
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,kategorilerList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(arrayAdapter);
    }

    private void spinnerDoldur(){
        collectionReferenceKategoriler.orderBy("kategoriAdi", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        String kategori = snapshot.getString("kategoriAdi");
                        kategorilerList.add(kategori);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private boolean girdiBosMuKontrolEN(){
        boolean y = false;
        try {
            String girdi = binding.TextInputLayoutEnglishSentence.getEditText().getText().toString().trim();
            if(girdi.isEmpty()){
                binding.TextInputLayoutEnglishSentence.setError("Cümle boş bırakılamaz.");
                y = true;
            }
        }catch (Exception e){
            y = false;
        }
        return y;
    }
    private boolean girdiBosMuKontrolTR(){
        boolean y = false;
        try {
            String girdi = binding.TextInputLayoutTurkceCumle.getEditText().getText().toString().trim();
            if(girdi.isEmpty()){
                binding.TextInputLayoutTurkceCumle.setError("Cümle boş bırakılamaz.");
                y = true;
            }
        }catch (Exception e){
            y = false;
        }
        return y;
    }

}
