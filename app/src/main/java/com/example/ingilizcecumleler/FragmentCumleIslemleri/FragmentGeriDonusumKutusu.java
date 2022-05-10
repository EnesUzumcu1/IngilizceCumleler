package com.example.ingilizcecumleler.FragmentCumleIslemleri;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ingilizcecumleler.Adapter.ButunCumlelerAdapter;
import com.example.ingilizcecumleler.Object.Cumleler;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.ActivityFragmentGeriDonusumKutusuBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FragmentGeriDonusumKutusu extends Fragment {
    private ActivityFragmentGeriDonusumKutusuBinding binding;
    FirebaseFirestore firestore;
    CollectionReference collectionReferenceCumleler;
    ButunCumlelerAdapter adapter;
    Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentGeriDonusumKutusuBinding.inflate(inflater,container,false);
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
        dialog = new Dialog(getContext());
    }

    private void listeyiHazirla(){
        Query query = collectionReferenceCumleler.whereEqualTo("silindiMi",true);
        query = query.orderBy("en", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Cumleler> ayarla =new FirestoreRecyclerOptions.Builder<Cumleler>().setQuery(query,Cumleler.class).build();
        adapter = new ButunCumlelerAdapter(ayarla,getResources().getString(R.string.GeriDonusumKutusu),getContext());
        binding.geriDonusumRecyclerView.setHasFixedSize(true);
        binding.geriDonusumRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.geriDonusumRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnItemClickListener(new ButunCumlelerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position,String butonAdi) {
                if(butonAdi.equals(getResources().getString(R.string.ilkButon))) {
                    showDialogAlert(documentSnapshot,getResources().getString(R.string.ilkButon));
                }
                else if(butonAdi.equals(getResources().getString(R.string.ikinciButon))){
                    showDialogAlert(documentSnapshot,getResources().getString(R.string.ikinciButon));
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void showDialogAlert(DocumentSnapshot documentSnapshot,String butonAdi){
        dialog.setContentView(R.layout.custom_dialog_box_kategori_sil);
        Button evetButton = dialog.findViewById(R.id.evet_button);
        Button hayirButton = dialog.findViewById(R.id.hayir_button);
        TextView uyariYazisi = dialog.findViewById(R.id.uyariYazisiTextView);

        if(butonAdi.equals(getResources().getString(R.string.ilkButon))){
            uyariYazisi.setText(String.format("\"%s\" cümlesini geri getirmek istediğinizden emin misiniz?", documentSnapshot.get("en").toString()));
        }
        else if(butonAdi.equals(getResources().getString(R.string.ikinciButon))){
            uyariYazisi.setText(String.format("\"%s\" cümlesini kalıcı olarak silmek istediğinizden emin misiniz?", documentSnapshot.get("en").toString()));
        }

        evetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(butonAdi.equals(getResources().getString(R.string.ilkButon))){
                    cumleyiGeriGetir(documentSnapshot);
                }
                else if(butonAdi.equals(getResources().getString(R.string.ikinciButon))){
                    cumleyiSil(documentSnapshot);
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

    private void cumleyiGeriGetir(DocumentSnapshot documentSnapshot){
        collectionReferenceCumleler.document(documentSnapshot.getId()).update("silindiMi",false);
    }
    private void cumleyiSil(DocumentSnapshot documentSnapshot){
        collectionReferenceCumleler.document(documentSnapshot.getId()).delete();
    }


}
