package com.example.ingilizcecumleler.FragmentCumleIslemleri;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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
    ArrayList<String> cumle1ID,cumle2ID;
    CountDownTimer timer;

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
                    sonucuBeklet();
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
        ArrayList<String> durum = new ArrayList<>();
        ArrayList<String> durum2 = new ArrayList<>();
        String girdi = binding.TextInputLayoutTurkceCumle.getEditText().getText().toString().trim().toLowerCase(Locale.ENGLISH);
        String girdi2 = binding.TextInputLayoutEnglishSentence.getEditText().getText().toString().trim().toLowerCase(new Locale("tr","TR"));
        durum.add(girdi);
        durum2.add(girdi2);
        Query query = collectionReferenceCumleler.whereEqualTo("tr", girdi);
        Query query2 = collectionReferenceCumleler.whereEqualTo("en",girdi2);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if(!documentSnapshots.getDocuments().isEmpty()){
                    for (int i =0;i<documentSnapshots.size();i++){
                        cumle1ID.add(documentSnapshots.getDocuments().get(i).getId());
                        Toast.makeText(getContext(),documentSnapshots.getDocuments().get(i).getId()+"--1",Toast.LENGTH_SHORT).show();
                    }
                    //cumle1ID = documentSnapshots.getDocuments().get(0).getId();

                }
                else{
                    //cumle1ID ="cumle1ID";

                }

            }
        });
        query2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if(!documentSnapshots.getDocuments().isEmpty()){
                    for (int i =0;i<documentSnapshots.size();i++){
                        cumle2ID.add(documentSnapshots.getDocuments().get(i).getId());
                        Toast.makeText(getContext(),documentSnapshots.getDocuments().get(i).getId()+"--2",Toast.LENGTH_SHORT).show();
                    }

                    //cumle2ID = documentSnapshots.getDocuments().get(0).getId();

                }
                else{
                    //cumle2ID = "cumle2ID";

                }


            }
        });

    }


    private void atamalar(){
        firestore = FirebaseFirestore.getInstance();
        collectionReferenceKategoriler = firestore.collection("Kategoriler");
        collectionReferenceCumleler = firestore.collection("Cumleler");
        kategorilerList = new ArrayList<String>() {};
        cumle1ID = new ArrayList<String>(){};
        cumle2ID = new ArrayList<String>(){};
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

    private void sonucuBeklet(){
        cumleMevcutMu();
        binding.uyariTextView.setText("Kontrol ediliyor");
        timer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.uyariTextView.append(".");
            }

            @Override
            public void onFinish() {
                boolean kontrol =false;
                for (String id1 : cumle1ID){
                    for (String id2 : cumle2ID){
                        if(id1.equals(id2)){
                            kontrol = true;
                        }
                    }
                }

                if(!kontrol){
                    cumleEkle();
                }
                else{
                    cumle1ID.clear();
                    cumle2ID.clear();
                    binding.uyariTextView.setText("Cümle zaten mevcut.");
                }
                binding.TextInputLayoutTurkceCumle.getEditText().setText("");
                binding.TextInputLayoutEnglishSentence.getEditText().setText("");
                binding.TextInputLayoutEnglishSentence.clearFocus();
                binding.TextInputLayoutTurkceCumle.clearFocus();
                binding.ekleButton.setEnabled(true);
            }

        }.start();

    }

}
