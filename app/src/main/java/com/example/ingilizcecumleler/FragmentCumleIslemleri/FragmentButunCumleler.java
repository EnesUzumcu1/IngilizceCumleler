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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ingilizcecumleler.Adapter.CumleAdapter.ButunCumlelerAdapter;
import com.example.ingilizcecumleler.Object.Cumleler;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.ActivityFragmentButunCumlelerBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentButunCumleler extends Fragment {
    private ActivityFragmentButunCumlelerBinding binding;
    private ButunCumlelerAdapter adapter;
    FirebaseFirestore firestore;
    CollectionReference collectionReferenceCumleler,collectionReferenceKategoriler;
    ArrayAdapter<String> arrayAdapter;
    List<String> kategorilerList;
    String taskID,butunKategoriler="HEPSİ";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentButunCumlelerBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        atamalar();
        listeyiHazirla(taskID);
        spinnerAdapterAyarla();
        spinnerDoldur();

        binding.kategoriSecSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String kategori =binding.kategoriSecSpinner.getItemAtPosition(position).toString();

                if(!kategori.equals(butunKategoriler)){
                    Query query = collectionReferenceKategoriler.whereEqualTo("kategoriAdi",kategori);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                //secilen kategorinin ID'si aliniyor.
                                try {
                                    taskID = task.getResult().getDocuments().get(0).getId();
                                }
                                catch (Exception e){

                                }

                            }
                        }
                    });
                }
                else{
                    //diger kategorileri seçtikten sonra tekrardan bütün kategoriler görüntülemek istediğimizde taskId deger içerdiği için null atamak gerekti.
                    taskID=null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.kategoriSecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listeyiHazirla(taskID);
            }
        });
    }

    private void atamalar(){
        firestore = FirebaseFirestore.getInstance();
        collectionReferenceCumleler = firestore.collection("Cumleler");
        collectionReferenceKategoriler = firestore.collection("Kategoriler");
        kategorilerList = new ArrayList<String>() {};
        kategorilerList.add(butunKategoriler);
    }

    private void listeyiHazirla(String taskID){
        Query query = collectionReferenceCumleler;
        query = query.whereEqualTo("silindiMi",false);
        //eger task id yoksa butun cumleleri getirecek. Eger task id varsa secilen kategorideki cumleler gelecek.
        if(taskID !=null){
            query = query.whereEqualTo("kategoriID",taskID);
        }
        //query = query.orderBy("en", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Cumleler> ayarla =new FirestoreRecyclerOptions.Builder<Cumleler>().setQuery(query,Cumleler.class).build();
        adapter = new ButunCumlelerAdapter(ayarla, getResources().getString(R.string.ButunCumleler),getContext());
        binding.butunCumlelerRecyclerView.setHasFixedSize(true);
        binding.butunCumlelerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.butunCumlelerRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void spinnerAdapterAyarla(){
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,kategorilerList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.kategoriSecSpinner.setAdapter(arrayAdapter);
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

    @Override
    public void onStart() {
        super.onStart();
        //listeyiHazirla(taskID);
    }
}
