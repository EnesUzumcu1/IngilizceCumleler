package com.example.ingilizcecumleler.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingilizcecumleler.Object.Kategoriler;
import com.example.ingilizcecumleler.databinding.CustomKategoriListeBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ButunKategorilerAdapter extends FirestoreRecyclerAdapter<Kategoriler,ButunKategorilerAdapter.ButunKategorilerHolder> {

    private CustomKategoriListeBinding binding;
    public ButunKategorilerAdapter(@NonNull FirestoreRecyclerOptions<Kategoriler> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ButunKategorilerHolder holder, int position, @NonNull Kategoriler model) {
        binding.ilanDurumuTextView.setText(model.kategoriAdi);
        binding.popupImageView.setVisibility(View.INVISIBLE);
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
        }
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
