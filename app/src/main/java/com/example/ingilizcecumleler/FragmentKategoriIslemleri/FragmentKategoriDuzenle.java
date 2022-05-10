package com.example.ingilizcecumleler.FragmentKategoriIslemleri;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ingilizcecumleler.Adapter.KategoriDuzenleAdapter;
import com.example.ingilizcecumleler.Object.Kategoriler;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.ActivityFragmentKategoriDuzenleBinding;
import com.example.ingilizcecumleler.databinding.CustomDialogBoxKategoriDuzenleBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FragmentKategoriDuzenle  extends Fragment {
    private ActivityFragmentKategoriDuzenleBinding binding;
    private KategoriDuzenleAdapter adapter;
    FirebaseFirestore firestore;
    CollectionReference collectionReferenceKategori;
    Dialog dialog;
    ProgressDialog progressDialog;
    int basariliIslemSayisi=0;
    int  i =0;
    CountDownTimer timer;
    TextView uyari;
    String yeniKategoriString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentKategoriDuzenleBinding.inflate(inflater,container,false);
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
        dialog = new Dialog(getContext());
        uyari = dialog.findViewById(R.id.uyariTextView);
    }

    private void listeyiHazirla(){
        Query query = collectionReferenceKategori;
        query = query.orderBy("kategoriAdi", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Kategoriler> ayarla =new FirestoreRecyclerOptions.Builder<Kategoriler>().setQuery(query,Kategoriler.class).build();
        adapter = new KategoriDuzenleAdapter(ayarla);
        binding.kategoriDuzenleRecyclerView.setHasFixedSize(true);
        binding.kategoriDuzenleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.kategoriDuzenleRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnItemClickListener(new KategoriDuzenleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                showDialogAlert(documentSnapshot);
            }
        });
    }

    public void showDialogAlert(DocumentSnapshot documentSnapshot){
        dialog.setContentView(R.layout.custom_dialog_box_kategori_duzenle);
        Button kaydetButton = dialog.findViewById(R.id.kaydet_button);
        Button iptalButton = dialog.findViewById(R.id.iptal_button);

        kaydetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText yeniKategori = dialog.findViewById(R.id.yeni_kategori_adi_editText);
                TextView uyari = dialog.findViewById(R.id.uyariTextView);
                uyari.setText("");
                yeniKategoriString = yeniKategori.getText().toString().trim().toUpperCase();
                if(!girdiBosMuKontrol()){
                    sonucuBeklet(documentSnapshot);
                }
                else{
                    uyari.setText("Kategori Adı boş bırakılamaz.");
                }
            }
        });
        iptalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    private boolean girdiBosMuKontrol(){
        boolean y = false;
        try {
            if(yeniKategoriString.isEmpty()){
                y = true;
            }
        }catch (Exception e){
            y = false;
        }

        return y;
    }

    private boolean kategoriMevcutMu(String girdi){
        boolean x ;
        ArrayList<String> durum = new ArrayList<>();
        durum.add(girdi);
        Query query = collectionReferenceKategori.whereIn("kategoriAdi", durum);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                basariliIslemSayisi = documentSnapshots.size();
            }
        });
        x = basariliIslemSayisi > 0;
        return x;
    }

    private void veriyiGuncelle(DocumentSnapshot documentSnapshot){
        collectionReferenceKategori.document(documentSnapshot.getId()).update("kategoriAdi",yeniKategoriString);
        TextView t = dialog.findViewById(R.id.uyariTextView);
        t.setText("Güncellendi");
        dialog.dismiss();
    }

    private void sonucuBeklet(DocumentSnapshot documentSnapshot){

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Kontrol Ediliyor...");
        progressDialog.setCancelable(false);
        progressDialog.setProgress(i);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));

        progressDialog.show();


        timer = new CountDownTimer(3000,3000) {
            @Override
            public void onTick(long millisUntilFinished) {
                kategoriMevcutMu(yeniKategoriString);
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                if(girdiKontrol()){
                    veriyiGuncelle(documentSnapshot);
                }
            }
        }.start();
    }

    private boolean girdiKontrol(){
        boolean x;
        try {
            if(kategoriMevcutMu(yeniKategoriString)){
                TextView t = dialog.findViewById(R.id.uyariTextView);
                t.setText("Kategori Zaten Mevcut!.");
                x=  false;
            }
            else{
                x=  true;
            }
        }
        catch (Exception e){
            x = false;
        }
        return x;
    }

}
