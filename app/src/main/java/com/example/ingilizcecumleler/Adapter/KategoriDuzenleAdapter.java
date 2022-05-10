package com.example.ingilizcecumleler.Adapter;

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

public class KategoriDuzenleAdapter extends FirestoreRecyclerAdapter<Kategoriler,KategoriDuzenleAdapter.KategoriDuzenleHolder> {

    private CustomKategoriListeBinding binding;
    private OnItemClickListener listener;
    public int position;
    public KategoriDuzenleAdapter(@NonNull FirestoreRecyclerOptions<Kategoriler> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull KategoriDuzenleHolder holder, int position, @NonNull Kategoriler model) {
        binding.ilanDurumuTextView.setText(model.kategoriAdi);
        binding.popupImageView.setImageResource(R.drawable.ic_baseline_edit_24);
    }

    @NonNull
    @Override
    public KategoriDuzenleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = CustomKategoriListeBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new KategoriDuzenleHolder(binding.getRoot());
    }

    class KategoriDuzenleHolder extends RecyclerView.ViewHolder {

        public KategoriDuzenleHolder(@NonNull View itemView) {
            super(itemView);
            binding.popupImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    duzenle();
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot,int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void duzenle(){
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
