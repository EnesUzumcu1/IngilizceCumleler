package com.example.ingilizcecumleler.FragmentCumleIslemleri;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ingilizcecumleler.Adapter.ButunCumlelerAdapter;
import com.example.ingilizcecumleler.Object.Cumleler;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.ActivityFragmentCumleDuzenleBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentCumleDuzenle extends Fragment {
    private ActivityFragmentCumleDuzenleBinding binding;
    FirebaseFirestore firestore;
    CollectionReference collectionReferenceCumleler;
    CollectionReference collectionReferenceKategoriler;
    Dialog dialog;
    ButunCumlelerAdapter adapter;
    ArrayAdapter<String> arrayAdapter;
    List<String> kategorilerList;
    String taskID,kategoriAdi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentCumleDuzenleBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        atamalar();
        listeyiHazirla();

    }

    private void atamalar(){
        firestore = FirebaseFirestore.getInstance();
        collectionReferenceCumleler = firestore.collection("Cumleler");
        collectionReferenceKategoriler = firestore.collection("Kategoriler");
        dialog = new Dialog(getContext());
        kategorilerList = new ArrayList<String>() {};
    }

    private void listeyiHazirla(){
        Query query = collectionReferenceCumleler.whereEqualTo("silindiMi",false);
        query = query.orderBy("en", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Cumleler> ayarla =new FirestoreRecyclerOptions.Builder<Cumleler>().setQuery(query,Cumleler.class).build();
        adapter = new ButunCumlelerAdapter(ayarla,getResources().getString(R.string.CumleDuzenle),getContext());
        binding.cumleDuzenleRecyclerView.setHasFixedSize(true);
        binding.cumleDuzenleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.cumleDuzenleRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnItemClickListener(new ButunCumlelerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String butonAdi) {
                if(butonAdi.equals(getResources().getString(R.string.ilkButon))) {
                    showDialogAlert(documentSnapshot,getResources().getString(R.string.ilkButon));
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void showDialogAlert(DocumentSnapshot documentSnapshot,String butonAdi){
        dialog.setContentView(R.layout.custom_dialog_box_cumle_duzenle);
        Button evetButton = dialog.findViewById(R.id.kaydet_button);
        Button hayirButton = dialog.findViewById(R.id.iptal_button);
        EditText trCumle = dialog.findViewById(R.id.yeni_tr_cumle_editText);
        EditText enCumle = dialog.findViewById(R.id.yeni_en_cumle_editText);
        TextView mevcutKategori = dialog.findViewById(R.id.mevcutKategoriTextView);
        Spinner kategoriSpinner = dialog.findViewById(R.id.yeniKategoriSecSpinner);

        //Dialog alert acıldıgında spinner dolduruluyor.

        kategoriAdiGetir(documentSnapshot,mevcutKategori);


        spinnerAdapterAyarla(kategoriSpinner);
        spinnerDoldur(documentSnapshot,kategoriSpinner);
        trCumle.setText(documentSnapshot.getString("tr"));
        enCumle.setText(documentSnapshot.getString("en"));


/*
        int position = arrayAdapter.getPosition(kategoriAdi);
        kategoriSpinner.setSelection(position);


 */



        kategoriSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String kategori =kategoriSpinner.getItemAtPosition(position).toString();

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

        evetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(butonAdi.equals(getResources().getString(R.string.ilkButon))){
                    if (inputIsEmpty(trCumle,enCumle)){
                        String yeniTrKelime = trCumle.getText().toString().trim();
                        String yeniEnKelime = enCumle.getText().toString().trim();
                        trCumleKontrol(documentSnapshot,documentSnapshot.getString("tr"),yeniTrKelime);
                        enCumleKontrol(documentSnapshot,documentSnapshot.getString("en"),yeniEnKelime);
                        kategoriKontrol(documentSnapshot,kategoriSpinner.getSelectedItem().toString(),kategoriAdi);
                    }

                }
                dialog.dismiss();
                listeyiHazirla();
            }
        });
        hayirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void spinnerDoldur(DocumentSnapshot documentSnapshot,Spinner spinner){
        collectionReferenceKategoriler.orderBy("kategoriAdi", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        String kategori = snapshot.getString("kategoriAdi");
                        if(!kategorilerList.contains(kategori)){
                            kategorilerList.add(kategori);
                        }

                    }
                    int position = arrayAdapter.getPosition(kategoriAdi);
                    spinner.setSelection(position);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void spinnerAdapterAyarla(Spinner spinner){
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,kategorilerList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    private void kategoriAdiGetir(DocumentSnapshot documentSnapshot,TextView textView){
        DocumentReference reference= collectionReferenceKategoriler.document(documentSnapshot.get("kategoriID").toString());

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    //secilen kategorinin ID'si aliniyor.
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        kategoriAdi = document.getString("kategoriAdi");
                        textView.setText("Mevcut Kategori: "+kategoriAdi);
                    }
                }
            }
        });


    }


    private void trCumleKontrol(DocumentSnapshot documentSnapshot,String dbVerisi,String input){
        //databaseden gelen veri inputa eşitse veri değişmemiştir ve güncelleme gerektirmez.
        if(!dbVerisi.equals(input)){
            collectionReferenceCumleler.document(documentSnapshot.getId()).update("tr",input);
            Toast.makeText(getContext(),"Türkçe cümle güncellendi.",Toast.LENGTH_SHORT).show();
        }
    }
    private void enCumleKontrol(DocumentSnapshot documentSnapshot,String dbVerisi,String input){
        //databaseden gelen veri inputa eşitse veri değişmemiştir ve güncelleme gerektirmez.
        if(!dbVerisi.equals(input)){
            collectionReferenceCumleler.document(documentSnapshot.getId()).update("en",input);
            Toast.makeText(getContext(),"İngilizce cümle güncellendi.",Toast.LENGTH_SHORT).show();
        }
    }
    private void kategoriKontrol(DocumentSnapshot documentSnapshot,String dbVerisi,String input){
        //databaseden gelen veri inputa eşitse veri değişmemiştir ve güncelleme gerektirmez.
        if(!dbVerisi.equals(input)){
            collectionReferenceCumleler.document(documentSnapshot.getId()).update("kategoriID",taskID);
            Toast.makeText(getContext(),"Kategori güncellendi.",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean inputIsEmpty(EditText en,EditText tr){
        return !(en.getText().toString().trim().isEmpty() || tr.getText().toString().trim().isEmpty());
    }


}
