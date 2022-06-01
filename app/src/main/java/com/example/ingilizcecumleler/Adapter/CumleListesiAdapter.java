package com.example.ingilizcecumleler.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingilizcecumleler.Object.Cumleler;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.CustomCumleListeBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;
import java.util.Map;

import android.content.Context;

public class CumleListesiAdapter extends FirestoreRecyclerAdapter<Cumleler, CumleListesiAdapter.ButunCumlelerHolder> {
    private CustomCumleListeBinding binding;
    public String islem;
    public int position;
    private OnItemClickListener listener;
    final private Context context;
    Map<String, String> kategoriMap ;

    public CumleListesiAdapter(@NonNull FirestoreRecyclerOptions<Cumleler> options, String islem, Context context, Map<String, String> kategoriMap ) {
        super(options);
        this.islem = islem;
        this.context = context;
        this.kategoriMap = kategoriMap;
    }

    @Override
    protected void onBindViewHolder(@NonNull ButunCumlelerHolder holder, final int position, @NonNull Cumleler model) {
        binding.englishSentenceTextView.setText(model.en.toUpperCase(Locale.ENGLISH));
        binding.turkceKelimeTextView.setText(model.tr.toUpperCase(new Locale("tr","TR")));

        binding.textViewKategoriAdi.setText(kategoriMap.get(model.kategoriID));


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
