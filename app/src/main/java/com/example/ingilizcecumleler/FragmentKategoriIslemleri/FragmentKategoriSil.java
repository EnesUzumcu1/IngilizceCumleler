package com.example.ingilizcecumleler.FragmentKategoriIslemleri;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class FragmentKategoriSil extends Fragment {
    private ActivityFragmentKategoriListesiBinding binding;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReferenceKategoriler;
    private CollectionReference collectionReferenceCumleler;
    private KategoriListesiAdapter adapter;
    Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=ActivityFragmentKategoriListesiBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        atamalar();
        listeyiHazirla();
    }

    private void atamalar(){
        firestore = FirebaseFirestore.getInstance();
        collectionReferenceKategoriler = firestore.collection("Kategoriler");
        dialog = new Dialog(getContext());
    }

    private void listeyiHazirla(){
        Query query = collectionReferenceKategoriler;
        query = query.orderBy("kategoriAdi", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Kategoriler> ayarla =new FirestoreRecyclerOptions.Builder<Kategoriler>().setQuery(query,Kategoriler.class).build();
        adapter = new KategoriListesiAdapter(ayarla,getResources().getString(R.string.KategoriSil),getContext());
        binding.kategoriListesiRecyclerView.setHasFixedSize(true);
        binding.kategoriListesiRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.kategoriListesiRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnItemClickListener(new KategoriListesiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                showDialogAlert(documentSnapshot);
            }
        });
    }

    public void showDialogAlert(DocumentSnapshot documentSnapshot){
        dialog.setContentView(R.layout.custom_dialog_box_kategori_sil);
        Button evetButton = dialog.findViewById(R.id.evet_button);
        Button hayirButton = dialog.findViewById(R.id.hayir_button);
        TextView uyariYazisi = dialog.findViewById(R.id.uyariYazisiTextView);

        uyariYazisi.setText(String.format("\"%s\" kategorisini silmek istediğinizden emin misiniz?", documentSnapshot.get("kategoriAdi").toString()));

        evetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kategoriKullanimdaMi(documentSnapshot);
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

    public void kategoriyiSil(DocumentSnapshot documentSnapshot){
        collectionReferenceKategoriler.document(documentSnapshot.getId()).delete();
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
}
