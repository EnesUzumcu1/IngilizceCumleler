package com.example.ingilizcecumleler.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingilizcecumleler.Object.Kategoriler;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.CustomKategoriListeBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class KategoriListesiAdapter extends FirestoreRecyclerAdapter<Kategoriler, KategoriListesiAdapter.ButunKategorilerHolder> {

    private CustomKategoriListeBinding binding;
    final private Context context;
    private OnItemClickListener listener;
    public int position;
    String islem;
    public KategoriListesiAdapter(@NonNull FirestoreRecyclerOptions<Kategoriler> options, String islem, Context context) {
        super(options);
        this.islem = islem;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ButunKategorilerHolder holder, final int position, @NonNull Kategoriler model) {
        binding.ilanDurumuTextView.setText(position+1+"-) "+model.kategoriAdi);

        if(islem.equals(context.getResources().getString(R.string.ButunKategoriler))){
            binding.popupImageView.setVisibility(View.INVISIBLE);
        }
        else if(islem.equals(context.getResources().getString(R.string.KategoriSil))){
            binding.popupImageView.setImageResource(R.drawable.ic_baseline_delete_24);
        }
        else if(islem.equals(context.getResources().getString(R.string.KategoriDuzenle))){
            binding.popupImageView.setImageResource(R.drawable.ic_baseline_edit_24);
        }
    }

    @NonNull
    @Override
    public ButunKategorilerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = CustomKategoriListeBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ButunKategorilerHolder(binding.getRoot());
    }

    class ButunKategorilerHolder extends RecyclerView.ViewHolder{
        public ButunKategorilerHolder(View itemView){
            super(itemView);
            if(islem.equals(context.getResources().getString(R.string.KategoriSil)) || islem.equals(context.getResources().getString(R.string.KategoriDuzenle))){
                binding.popupImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        position = getAdapterPosition();
                        buttonClick();
                    }
                });
            }
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void buttonClick(){
        listener.onItemClick(getSnapshots().getSnapshot(position),position);
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
