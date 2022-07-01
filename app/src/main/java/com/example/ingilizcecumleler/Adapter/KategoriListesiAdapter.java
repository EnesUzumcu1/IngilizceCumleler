package com.example.ingilizcecumleler.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingilizcecumleler.Object.Kategoriler;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.CustomKategoriListeBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class KategoriListesiAdapter extends FirestoreRecyclerAdapter<Kategoriler, KategoriListesiAdapter.ButunKategorilerHolder> {

    private CustomKategoriListeBinding binding;
    private OnItemClickListener listener;
    public int position;
    public KategoriListesiAdapter(@NonNull FirestoreRecyclerOptions<Kategoriler> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ButunKategorilerHolder holder, final int position, @NonNull Kategoriler model) {
        binding.ilanDurumuTextView.setText(position+1+"-) "+model.kategoriAdi);
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
            binding.popupImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(itemView.getContext(), v);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.popup_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.popup_duzenle:
                                    position = getAdapterPosition();
                                    buttonClick(R.id.popup_duzenle);
                                    return true;
                                case R.id.popup_sil:
                                    position = getAdapterPosition();
                                    buttonClick(R.id.popup_sil);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position, int butonID);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void buttonClick(int butonID){
        listener.onItemClick(getSnapshots().getSnapshot(position),position,butonID);
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
