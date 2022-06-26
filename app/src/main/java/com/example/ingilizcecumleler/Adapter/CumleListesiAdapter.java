package com.example.ingilizcecumleler.Adapter;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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
    private ItemCount count;
    final private Context context;
    Map<String, String> kategoriMap;

    public CumleListesiAdapter(@NonNull FirestoreRecyclerOptions<Cumleler> options, String islem, Context context, Map<String, String> kategoriMap) {
        super(options);
        this.islem = islem;
        this.context = context;
        this.kategoriMap = kategoriMap;
    }

    @Override
    protected void onBindViewHolder(@NonNull ButunCumlelerHolder holder, final int position, @NonNull Cumleler model) {
        binding.englishSentenceTextView.setText(model.en.toUpperCase(Locale.ENGLISH));
        binding.turkceKelimeTextView.setText(model.tr.toUpperCase(new Locale("tr", "TR")));

        binding.textViewKategoriAdi.setText(kategoriMap.get(model.kategoriID));
    }

    @NonNull
    @Override
    public ButunCumlelerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = CustomCumleListeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ButunCumlelerHolder(binding.getRoot());
    }

    class ButunCumlelerHolder extends RecyclerView.ViewHolder {

        public ButunCumlelerHolder(@NonNull View itemView) {
            super(itemView);
            binding.imageView8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(itemView.getContext(), v);
                    //inflating menu from xml resource
                    if (islem.equals(context.getResources().getString(R.string.GeriDonusumKutusu))) {
                        popup.inflate(R.menu.popup_menu_geri_donusum);
                    } else {
                        popup.inflate(R.menu.popup_menu);
                    }
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.popup_geriYukle:
                                    position = getAdapterPosition();
                                    buttonClick(R.id.popup_geriYukle);
                                    return true;
                                case R.id.popup_kaliciSil:
                                    position = getAdapterPosition();
                                    buttonClick(R.id.popup_kaliciSil);
                                    return true;
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
            itemCount(count);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position, int butonID);
    }

    public interface ItemCount {
        void onItemCount(int count);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void buttonClick(int butonID) {
        listener.onItemClick(getSnapshots().getSnapshot(position), position, butonID);
    }

    public void setOnItemCount(ItemCount count) {
        this.count = count;
    }

    public void itemCount(ItemCount count) {
        count.onItemCount(getItemCount());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


}
