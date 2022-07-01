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

public class FragmentKategoriListesi extends Fragment {
    private ActivityFragmentKategoriListesiBinding binding;
    private KategoriListesiAdapter adapter;
    FirebaseFirestore firestore;
    CollectionReference collectionReferenceKategori;
    CollectionReference collectionReferenceCumleler;
    Dialog dialogKategoriSil;
    Dialog dialogKategoriDuzenle;
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
        dialogKategoriSil = new Dialog(getContext());
        dialogKategoriDuzenle = new Dialog(getContext());
        uyari = dialogKategoriDuzenle.findViewById(R.id.uyariTextView);
    }

    private void listeyiHazirla(){
        Query query = collectionReferenceKategori.orderBy("kategoriAdi", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Kategoriler> ayarla =new FirestoreRecyclerOptions.Builder<Kategoriler>().setQuery(query,Kategoriler.class).build();
        adapter = new KategoriListesiAdapter(ayarla);
        binding.kategoriListesiRecyclerView.setHasFixedSize(true);
        binding.kategoriListesiRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.kategoriListesiRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new KategoriListesiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, int butonID) {
                if(butonID == R.id.popup_sil){
                    showDialogAlertKategoriSil(documentSnapshot);
                }
                else if(butonID == R.id.popup_duzenle){
                    showDialogAlertKategoriDuzenle(documentSnapshot);
                }
            }
        });
        adapter.startListening();
    }

    //Kategori Silmek için işlemler
    public void showDialogAlertKategoriSil(DocumentSnapshot documentSnapshot){
        dialogKategoriSil.setContentView(R.layout.custom_dialog_box_kategori_sil);
        Button evetButton = dialogKategoriSil.findViewById(R.id.evet_button);
        Button hayirButton = dialogKategoriSil.findViewById(R.id.hayir_button);
        TextView uyariYazisi = dialogKategoriSil.findViewById(R.id.uyariYazisiTextView);

        uyariYazisi.setText(String.format("\"%s\" kategorisini silmek istediğinizden emin misiniz?", documentSnapshot.get("kategoriAdi").toString()));

        evetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kategoriKullanimdaMi(documentSnapshot);
                dialogKategoriSil.dismiss();
                listeyiHazirla();
            }
        });
        hayirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogKategoriSil.dismiss();
            }
        });
        dialogKategoriSil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogKategoriSil.show();
    }
    public void kategoriyiSil(DocumentSnapshot documentSnapshot){
        collectionReferenceKategori.document(documentSnapshot.getId()).delete();
    }
    private void kategoriKullanimdaMi(DocumentSnapshot documentSnapshot){
        collectionReferenceCumleler =firestore.collection("Cumleler");
        Query query = collectionReferenceCumleler.whereEqualTo("kategoriID",documentSnapshot.getId());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if(documentSnapshots.size()==0){
                    kategoriyiSil(documentSnapshot);
                    Toast.makeText(getContext(),"Kategori Silindi.",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),String.format("Bu kategoriyi %s cümle kullanılıyor. İlk önce o cümleyi silin veya kategorisini değiştirin.",documentSnapshots.size()),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //Kategori Silmek için işlemler sonu
    //Kategori Düzenlemek için işlemler
    public void showDialogAlertKategoriDuzenle(DocumentSnapshot documentSnapshot){
        dialogKategoriDuzenle.setContentView(R.layout.custom_dialog_box_kategori_duzenle);
        Button kaydetButton = dialogKategoriDuzenle.findViewById(R.id.kaydet_button);
        Button iptalButton = dialogKategoriDuzenle.findViewById(R.id.iptal_button);

        EditText yeniKategori = dialogKategoriDuzenle.findViewById(R.id.yeni_kategori_adi_editText);
        yeniKategori.setText(documentSnapshot.getString("kategoriAdi"));

        kaydetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView uyari = dialogKategoriDuzenle.findViewById(R.id.uyariTextView);
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
                dialogKategoriDuzenle.dismiss();
            }
        });
        dialogKategoriDuzenle.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogKategoriDuzenle.show();
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
                    TextView t = dialogKategoriDuzenle.findViewById(R.id.uyariTextView);
                    t.setText("Kategori Zaten Mevcut!.");
                }
            }
        });
    }
    private void veriyiGuncelle(DocumentSnapshot documentSnapshot){
        collectionReferenceKategori.document(documentSnapshot.getId()).update("kategoriAdi",yeniKategoriString);
        TextView t = dialogKategoriDuzenle.findViewById(R.id.uyariTextView);
        t.setText("Güncellendi");
        dialogKategoriDuzenle.dismiss();
    }
    //Kategori Düzenlemek için işlemler sonu
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
