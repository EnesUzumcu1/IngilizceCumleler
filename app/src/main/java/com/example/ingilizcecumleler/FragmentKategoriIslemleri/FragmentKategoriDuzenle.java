package com.example.ingilizcecumleler.FragmentKategoriIslemleri;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ingilizcecumleler.Adapter.KategoriListesiAdapter;
import com.example.ingilizcecumleler.Object.Kategoriler;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.ActivityFragmentKategoriListesiBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class FragmentKategoriDuzenle  extends Fragment {
    private ActivityFragmentKategoriListesiBinding binding;
    private KategoriListesiAdapter adapter;
    FirebaseFirestore firestore;
    CollectionReference collectionReferenceKategori;
    Dialog dialog;
    int basariliIslemSayisi=0;
    TextView uyari;
    String yeniKategoriString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentKategoriListesiBinding.inflate(inflater,container,false);
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
        adapter = new KategoriListesiAdapter(ayarla,getResources().getString(R.string.KategoriDuzenle),getContext());
        binding.kategoriListesiRecyclerView.setHasFixedSize(true);
        binding.kategoriListesiRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.kategoriListesiRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new KategoriListesiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                showDialogAlert(documentSnapshot);
            }
        });
        adapter.startListening();
    }

    public void showDialogAlert(DocumentSnapshot documentSnapshot){
        dialog.setContentView(R.layout.custom_dialog_box_kategori_duzenle);
        Button kaydetButton = dialog.findViewById(R.id.kaydet_button);
        Button iptalButton = dialog.findViewById(R.id.iptal_button);

        EditText yeniKategori = dialog.findViewById(R.id.yeni_kategori_adi_editText);
        yeniKategori.setText(documentSnapshot.getString("kategoriAdi"));

        kaydetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView uyari = dialog.findViewById(R.id.uyariTextView);
                uyari.setText("");
                yeniKategoriString = yeniKategori.getText().toString().trim().toUpperCase();
                if(!girdiBosMuKontrol()){
                    kategoriMevcutMu(documentSnapshot);
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

    private void kategoriMevcutMu(DocumentSnapshot documentSnapshot){
        Query query = collectionReferenceKategori.whereEqualTo("kategoriAdi", yeniKategoriString);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                basariliIslemSayisi = documentSnapshots.size();
                if(basariliIslemSayisi==0){
                    veriyiGuncelle(documentSnapshot);
                    listeyiHazirla();
                    Toast.makeText(getContext(),"Kategori güncellendi: "+yeniKategoriString,Toast.LENGTH_SHORT).show();
                }
                else{
                    TextView t = dialog.findViewById(R.id.uyariTextView);
                    t.setText("Kategori Zaten Mevcut!.");
                }
            }
        });
    }

    private void veriyiGuncelle(DocumentSnapshot documentSnapshot){
        collectionReferenceKategori.document(documentSnapshot.getId()).update("kategoriAdi",yeniKategoriString);
        TextView t = dialog.findViewById(R.id.uyariTextView);
        t.setText("Güncellendi");
        dialog.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
