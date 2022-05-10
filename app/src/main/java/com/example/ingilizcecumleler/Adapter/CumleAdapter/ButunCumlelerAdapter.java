package com.example.ingilizcecumleler.Adapter.CumleAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingilizcecumleler.Object.Cumleler;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.CustomCumleListeBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.widget.Toast;

public class ButunCumlelerAdapter extends FirestoreRecyclerAdapter<Cumleler,ButunCumlelerAdapter.ButunCumlelerHolder> {
    private CustomCumleListeBinding binding;
    public String islem;
    public int position;
    private OnItemClickListener listener;
    final private Context context;
    //public String kategoriAdi;
    Map <String,String> kategoriler;
    FirebaseFirestore firebase;
    CollectionReference ref;
    int i=0;
    public ButunCumlelerAdapter(@NonNull FirestoreRecyclerOptions<Cumleler> options,String islem,Context context ) {
        super(options);
        this.islem = islem;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ButunCumlelerHolder holder, final int position, @NonNull Cumleler model) {
        binding.englishSentenceTextView.setText(model.en.toUpperCase(Locale.ENGLISH));
        binding.turkceKelimeTextView.setText(model.tr.toUpperCase(new Locale("tr","TR")));

        /*
        ref.document(model.kategoriID).get().addOnSuccessListener(documentSnapshot -> {
            String kategoriAdi = documentSnapshot.get("kategoriAdi").toString();
            binding.textViewKategoriAdi.setText(kategoriAdi);
        });

         */

        binding.textViewKategoriAdi.setText(kategoriler.get(model.kategoriID));

        if(islem.equals(context.getResources().getString(R.string.CumleSil))){
            binding.imageView8.setImageResource(R.drawable.ic_baseline_delete_24);
        }
        else if(islem.equals(context.getResources().getString(R.string.GeriDonusumKutusu))){
            binding.imageView8.setImageResource(R.drawable.ic_baseline_replay_24);
            binding.imageView9.setImageResource(R.drawable.ic_baseline_delete_24);
        }
        else if(islem.equals(context.getResources().getString(R.string.CumleDuzenle))){
            binding.imageView8.setImageResource(R.drawable.ic_baseline_edit_24);
        }
        //butun cumleler ve cumle eklede simge bulunmuyor.
    }

    @NonNull
    @Override
    public ButunCumlelerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = CustomCumleListeBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        firebase = FirebaseFirestore.getInstance();
        ref = firebase.collection("Kategoriler");

        kategoriler = new HashMap<String, String>();
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        String kategoriID = documentSnapshot.getId();
                        String kategoriAdi= documentSnapshot.getData().get("kategoriAdi").toString();
                        kategoriler.put(kategoriID,kategoriAdi);
                    }
                }
            }
        });

        return new ButunCumlelerHolder(binding.getRoot());
    }

    class ButunCumlelerHolder extends RecyclerView.ViewHolder {

        public ButunCumlelerHolder(@NonNull View itemView) {
            super(itemView);
            binding.imageView8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    buttonClick(context.getResources().getString(R.string.ilkButon));
                }
            });
            if(islem.equals(context.getResources().getString(R.string.GeriDonusumKutusu))){
                binding.imageView9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        position = getAdapterPosition();
                        buttonClick(context.getResources().getString(R.string.ikinciButon));
                    }
                });
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position,String butonAdi);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void buttonClick(String butonAdi){
        listener.onItemClick(getSnapshots().getSnapshot(position),position,butonAdi);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
