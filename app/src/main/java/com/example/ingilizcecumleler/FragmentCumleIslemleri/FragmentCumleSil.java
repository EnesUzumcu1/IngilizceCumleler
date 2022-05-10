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

import com.example.ingilizcecumleler.Adapter.CumleAdapter.ButunCumlelerAdapter;
import com.example.ingilizcecumleler.Adapter.KategoriSilAdapter;
import com.example.ingilizcecumleler.Object.Cumleler;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.ActivityFragmentCumleSilBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FragmentCumleSil extends Fragment {
    private ActivityFragmentCumleSilBinding binding;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReferenceCumleler;
    private ButunCumlelerAdapter adapter;
    Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentCumleSilBinding.inflate(inflater,container,false);
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

    public void showDialogAlert(DocumentSnapshot documentSnapshot){
        dialog.setContentView(R.layout.custom_dialog_box_kategori_sil);
        Button evetButton = dialog.findViewById(R.id.evet_button);
        Button hayirButton = dialog.findViewById(R.id.hayir_button);
        TextView uyariYazisi = dialog.findViewById(R.id.uyariYazisiTextView);

        uyariYazisi.setText(String.format("\"%s\" cümlesini silmek istediğinizden emin misiniz?", documentSnapshot.get("en").toString()));

        evetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cumleyiSil(documentSnapshot);
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

    private void cumleyiSil(DocumentSnapshot documentSnapshot){
        collectionReferenceCumleler.document(documentSnapshot.getId()).update("silindiMi",true);
    }

    private void listeyiHazirla(){
        Query query = collectionReferenceCumleler.whereEqualTo("silindiMi",false);
        query = query.orderBy("en", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Cumleler> ayarla =new FirestoreRecyclerOptions.Builder<Cumleler>().setQuery(query,Cumleler.class).build();
        adapter = new ButunCumlelerAdapter(ayarla,getResources().getString(R.string.CumleSil),getContext());
        binding.cumleSilRecyclerView.setHasFixedSize(true);
        binding.cumleSilRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.cumleSilRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnItemClickListener(new ButunCumlelerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position,String butonAdi) {
                if(butonAdi.equals(getResources().getString(R.string.ilkButon))){
                    showDialogAlert(documentSnapshot);
                }
            }
        });
        adapter.notifyDataSetChanged();
    }
}
