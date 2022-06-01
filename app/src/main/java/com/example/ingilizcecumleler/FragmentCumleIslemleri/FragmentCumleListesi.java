package com.example.ingilizcecumleler.FragmentCumleIslemleri;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ingilizcecumleler.Adapter.CumleListesiAdapter;
import com.example.ingilizcecumleler.Object.Cumleler;
import com.example.ingilizcecumleler.R;
import com.example.ingilizcecumleler.databinding.ActivityFragmentButunCumlelerBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentCumleListesi extends Fragment {
    private ActivityFragmentButunCumlelerBinding binding;
    private CumleListesiAdapter adapter;
    FirebaseFirestore firestore;
    CollectionReference collectionReferenceCumleler,collectionReferenceKategoriler;
    ArrayAdapter<String> arrayAdapter;
    List<String> kategorilerList;
    List<String> kategorilerListCumleDuzenleSpinner;
    String taskID, butunKategoriler="HEPSİ", kategoriAdi, taskIDSpinner;
    String id ="";
    Dialog dialogGeriDonusumKutusu, dialogCumleSil, dialogCumleDuzenle;
    Map kategoriIDveAdMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityFragmentButunCumlelerBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        atamalarOrtak();
        gelenIDBul();

        idyeGoreClassCagirma();

        spinnerAdapterAyarla();
        spinnerDoldur();

        binding.kategoriSecSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String kategori =binding.kategoriSecSpinner.getItemAtPosition(position).toString();

                if(!kategori.equals(butunKategoriler)){
                    Query query = collectionReferenceKategoriler.whereEqualTo("kategoriAdi",kategori);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                //secilen kategorinin ID'si aliniyor.
                                try {
                                    taskID = task.getResult().getDocuments().get(0).getId();
                                }
                                catch (Exception e){

                                }
                            }
                        }
                    });
                }
                else{
                    //diger kategorileri seçtikten sonra tekrardan bütün kategoriler görüntülemek istediğimizde taskId deger içerdiği için null atamak gerekti.
                    taskID=null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.kategoriSecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.equals(getString(R.string.ButunCumleler))) {
                    listeyiHazirlaButunCumleler(taskID);
                } else if (id.equals(getString(R.string.CumleSil))) {
                    listeyiHazirlaCumleSil(taskID);
                } else if (id.equals(getString(R.string.CumleDuzenle))) {
                    listeyiHazirlaCumleDuzenle(taskID);
                } else if (id.equals(getString(R.string.GeriDonusumKutusu))) {
                    listeyiHazirlaGeriDonusumKutusu(taskID);
                }
            }
        });
    }

    public void butunCumleler(){
        atamalarButunCumleler();
        listeyiHazirlaButunCumleler(taskID);
    }

    public void geriDonusumKutusu(){
        atamalarGeriDonusumKutusu();
        listeyiHazirlaGeriDonusumKutusu(taskID);
    }

    public void cumleDuzenle(){
        atamalarCumleDuzenle();
        listeyiHazirlaCumleDuzenle(taskID);
    }

    public void cumleSil(){
        atamalarCumleSil();
        listeyiHazirlaCumleSil(taskID);
    }

    private void gelenIDBul(){
        //Önceki sayfanın hangi sayfadan geldigini anlamak icin id'deki veriyi alıyoruz.
        Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle != null){
            id = bundle.getString("id");
            kategoriIDveAdMap = (Map) bundle.getSerializable("map");
        }
    }

    private void idyeGoreClassCagirma(){
        if (id.equals(getString(R.string.ButunCumleler))) {
            butunCumleler();
        } else if (id.equals(getString(R.string.CumleSil))) {
            cumleSil();
        } else if (id.equals(getString(R.string.CumleDuzenle))) {
            cumleDuzenle();
        } else if (id.equals(getString(R.string.GeriDonusumKutusu))) {
            geriDonusumKutusu();
        } else {
            butunCumleler();
        }
    }

    private void atamalarOrtak(){
        firestore = FirebaseFirestore.getInstance();
        collectionReferenceCumleler = firestore.collection("Cumleler");
        collectionReferenceKategoriler = firestore.collection("Kategoriler");
        kategorilerList = new ArrayList<String>() {};
        kategorilerList.add(butunKategoriler);
        kategoriIDveAdMap =new HashMap<>();
    }

    private void atamalarButunCumleler(){
        //gerekli bir atama yok.
    }

    private void atamalarGeriDonusumKutusu(){
        dialogGeriDonusumKutusu = new Dialog(getContext());
    }

    private void atamalarCumleSil(){
        dialogCumleSil = new Dialog(getContext());
    }

    private void atamalarCumleDuzenle(){
        dialogCumleDuzenle = new Dialog(getContext());
        kategorilerListCumleDuzenleSpinner = new ArrayList<String>() {};
    }

    private void listeyiHazirlaButunCumleler(String taskID){
        Query query = collectionReferenceCumleler;
        query = query.whereEqualTo("silindiMi",false);
        //eger task id yoksa butun cumleleri getirecek. Eger task id varsa secilen kategorideki cumleler gelecek.
        if(taskID !=null){
            query = query.whereEqualTo("kategoriID",taskID);
        }
        query = query.orderBy("en", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Cumleler> ayarla =new FirestoreRecyclerOptions.Builder<Cumleler>().setQuery(query,Cumleler.class).build();
        adapter = new CumleListesiAdapter(ayarla, getResources().getString(R.string.ButunCumleler),getContext(),kategoriIDveAdMap);
        binding.butunCumlelerRecyclerView.setHasFixedSize(true);
        binding.butunCumlelerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.butunCumlelerRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void listeyiHazirlaGeriDonusumKutusu(String taskID){
        Query query = collectionReferenceCumleler.whereEqualTo("silindiMi",true);
        //eger task id yoksa butun cumleleri getirecek. Eger task id varsa secilen kategorideki cumleler gelecek.
        if(taskID !=null){
            query = query.whereEqualTo("kategoriID",taskID);
        }
        query = query.orderBy("en", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Cumleler> ayarla =new FirestoreRecyclerOptions.Builder<Cumleler>().setQuery(query,Cumleler.class).build();
        adapter = new CumleListesiAdapter(ayarla,getResources().getString(R.string.GeriDonusumKutusu),getContext(),kategoriIDveAdMap);
        binding.butunCumlelerRecyclerView.setHasFixedSize(true);
        binding.butunCumlelerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.butunCumlelerRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnItemClickListener(new CumleListesiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String butonAdi) {
                if(butonAdi.equals(getResources().getString(R.string.ilkButon))) {
                    showDialogAlertGeriDonusumKutusu(documentSnapshot,getResources().getString(R.string.ilkButon));
                }
                else if(butonAdi.equals(getResources().getString(R.string.ikinciButon))){
                    showDialogAlertGeriDonusumKutusu(documentSnapshot,getResources().getString(R.string.ikinciButon));
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void listeyiHazirlaCumleSil(String taskID){
        Query query = collectionReferenceCumleler.whereEqualTo("silindiMi",false);
        query = query.orderBy("en", Query.Direction.ASCENDING);
        //eger task id yoksa butun cumleleri getirecek. Eger task id varsa secilen kategorideki cumleler gelecek.
        if(taskID !=null){
            query = query.whereEqualTo("kategoriID",taskID);
        }
        FirestoreRecyclerOptions<Cumleler> ayarla =new FirestoreRecyclerOptions.Builder<Cumleler>().setQuery(query,Cumleler.class).build();
        adapter = new CumleListesiAdapter(ayarla,getResources().getString(R.string.CumleSil),getContext(),kategoriIDveAdMap);
        binding.butunCumlelerRecyclerView.setHasFixedSize(true);
        binding.butunCumlelerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.butunCumlelerRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnItemClickListener(new CumleListesiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position,String butonAdi) {
                if(butonAdi.equals(getResources().getString(R.string.ilkButon))){
                    showDialogAlertCumleSil(documentSnapshot);
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void listeyiHazirlaCumleDuzenle(String taskID){
        Query query = collectionReferenceCumleler.whereEqualTo("silindiMi",false);
        query = query.orderBy("en", Query.Direction.ASCENDING);
        //eger task id yoksa butun cumleleri getirecek. Eger task id varsa secilen kategorideki cumleler gelecek.
        if(taskID !=null){
            query = query.whereEqualTo("kategoriID",taskID);
        }
        FirestoreRecyclerOptions<Cumleler> ayarla =new FirestoreRecyclerOptions.Builder<Cumleler>().setQuery(query,Cumleler.class).build();
        adapter = new CumleListesiAdapter(ayarla,getResources().getString(R.string.CumleDuzenle),getContext(),kategoriIDveAdMap);
        binding.butunCumlelerRecyclerView.setHasFixedSize(true);
        binding.butunCumlelerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.butunCumlelerRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnItemClickListener(new CumleListesiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String butonAdi) {
                if(butonAdi.equals(getResources().getString(R.string.ilkButon))) {
                    showDialogAlertCumleDuzenle(documentSnapshot,getResources().getString(R.string.ilkButon));
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void showDialogAlertCumleSil(DocumentSnapshot documentSnapshot){
        dialogCumleSil.setContentView(R.layout.custom_dialog_box_kategori_sil);
        Button evetButton = dialogCumleSil.findViewById(R.id.evet_button);
        Button hayirButton = dialogCumleSil.findViewById(R.id.hayir_button);
        TextView uyariYazisi = dialogCumleSil.findViewById(R.id.uyariYazisiTextView);

        uyariYazisi.setText(String.format("\"%s\" cümlesini silmek istediğinizden emin misiniz?", documentSnapshot.get("en").toString()));

        evetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cumleyiSil(documentSnapshot);
                dialogCumleSil.dismiss();
                listeyiHazirlaCumleSil(taskID);
            }
        });
        hayirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCumleSil.dismiss();
            }
        });
        dialogCumleSil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCumleSil.show();
    }

    public void showDialogAlertGeriDonusumKutusu(DocumentSnapshot documentSnapshot,String butonAdi){
        dialogGeriDonusumKutusu.setContentView(R.layout.custom_dialog_box_kategori_sil);
        Button evetButton = dialogGeriDonusumKutusu.findViewById(R.id.evet_button);
        Button hayirButton = dialogGeriDonusumKutusu.findViewById(R.id.hayir_button);
        TextView uyariYazisi = dialogGeriDonusumKutusu.findViewById(R.id.uyariYazisiTextView);

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
                    cumleyiKaliciSil(documentSnapshot);
                }
                dialogGeriDonusumKutusu.dismiss();
                listeyiHazirlaGeriDonusumKutusu(taskID);
            }
        });
        hayirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGeriDonusumKutusu.dismiss();
            }
        });
        dialogGeriDonusumKutusu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogGeriDonusumKutusu.show();
    }

    public void showDialogAlertCumleDuzenle(DocumentSnapshot documentSnapshot,String butonAdi){
        dialogCumleDuzenle.setContentView(R.layout.custom_dialog_box_cumle_duzenle);
        Button evetButton = dialogCumleDuzenle.findViewById(R.id.kaydet_button);
        Button hayirButton = dialogCumleDuzenle.findViewById(R.id.iptal_button);
        EditText trCumle = dialogCumleDuzenle.findViewById(R.id.yeni_tr_cumle_editText);
        EditText enCumle = dialogCumleDuzenle.findViewById(R.id.yeni_en_cumle_editText);
        TextView mevcutKategori = dialogCumleDuzenle.findViewById(R.id.mevcutKategoriTextView);
        Spinner kategoriSpinner = dialogCumleDuzenle.findViewById(R.id.yeniKategoriSecSpinner);

        //Dialog alert acıldıgında spinner dolduruluyor.

        kategoriAdiGetir(documentSnapshot,mevcutKategori);

        spinnerAdapterAyarlaCumleDuzenleDialog(kategoriSpinner);
        spinnerDoldurCumleDuzenleDialog(documentSnapshot,kategoriSpinner);
        trCumle.setText(documentSnapshot.getString("tr"));
        enCumle.setText(documentSnapshot.getString("en"));
/*
        int position = arrayAdapter.getPosition(kategoriAdi);
        kategoriSpinner.setSelection(position);
 */
        kategoriSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String kategori =kategoriSpinner.getItemAtPosition(position).toString();

                Query query = collectionReferenceKategoriler.whereEqualTo("kategoriAdi",kategori);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            //secilen kategorinin ID'si aliniyor.
                            taskIDSpinner = task.getResult().getDocuments().get(0).getId();
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        evetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(butonAdi.equals(getResources().getString(R.string.ilkButon))){
                    if (inputIsEmpty(trCumle,enCumle)){
                        String yeniTrKelime = trCumle.getText().toString().trim();
                        String yeniEnKelime = enCumle.getText().toString().trim();
                        trCumleKontrol(documentSnapshot,documentSnapshot.getString("tr"),yeniTrKelime);
                        enCumleKontrol(documentSnapshot,documentSnapshot.getString("en"),yeniEnKelime);
                        kategoriKontrol(documentSnapshot,kategoriSpinner.getSelectedItem().toString(),kategoriAdi);
                    }

                }
                dialogCumleDuzenle.dismiss();
                listeyiHazirlaCumleDuzenle(taskID);
            }
        });
        hayirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCumleDuzenle.dismiss();
            }
        });
        dialogCumleDuzenle.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCumleDuzenle.show();
    }

    //Cümleyi geri dönüşüm kutusundan geri getirir.
    private void cumleyiGeriGetir(DocumentSnapshot documentSnapshot){
        collectionReferenceCumleler.document(documentSnapshot.getId()).update("silindiMi",false);
    }

    //Cümle kalıcı olarak silinir.
    private void cumleyiKaliciSil(DocumentSnapshot documentSnapshot){
        collectionReferenceCumleler.document(documentSnapshot.getId()).delete();
    }

    //Cümleyi geri dönüşüm kutusuna taşır.
    private void cumleyiSil(DocumentSnapshot documentSnapshot){
        collectionReferenceCumleler.document(documentSnapshot.getId()).update("silindiMi",true);
    }

    //sayfanın üstünde kategori seçmeye yarayan spinner ayarı
    private void spinnerAdapterAyarla(){
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,kategorilerList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.kategoriSecSpinner.setAdapter(arrayAdapter);
    }

    //sayfanın üstünde kategori seçmeye yarayan spinner ayarı
    private void spinnerDoldur(){
        collectionReferenceKategoriler.orderBy("kategoriAdi", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        String kategori = snapshot.getString("kategoriAdi");
                        kategorilerList.add(kategori);
                        //kategoriIDveAdMap.put(snapshot.getId(),kategori);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    //Cümle düzenleme sayfasında düzenlenen cümlenin kategorisini gösteren spinneri ayarı
    private void spinnerDoldurCumleDuzenleDialog(DocumentSnapshot documentSnapshot,Spinner spinner){
        collectionReferenceKategoriler.orderBy("kategoriAdi", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        String kategori = snapshot.getString("kategoriAdi");
                        if(!kategorilerListCumleDuzenleSpinner.contains(kategori)){
                            kategorilerListCumleDuzenleSpinner.add(kategori);
                        }

                    }
                    int position = arrayAdapter.getPosition(kategoriAdi);
                    spinner.setSelection(position);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    //Cümle düzenleme sayfasında düzenlenen cümlenin kategorisini gösteren spinneri ayarı
    private void spinnerAdapterAyarlaCumleDuzenleDialog(Spinner spinner){
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,kategorilerListCumleDuzenleSpinner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    //Düzenlenen cümlenin kategori adını getirir
    private void kategoriAdiGetir(DocumentSnapshot documentSnapshot,TextView textView){
        DocumentReference reference= collectionReferenceKategoriler.document(documentSnapshot.get("kategoriID").toString());

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    //secilen kategorinin ID'si aliniyor.
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        kategoriAdi = document.getString("kategoriAdi");
                        textView.setText("Mevcut Kategori: "+kategoriAdi);
                    }
                }
            }
        });


    }

    private void trCumleKontrol(DocumentSnapshot documentSnapshot,String dbVerisi,String input){
        //databaseden gelen veri inputa eşitse veri değişmemiştir ve güncelleme gerektirmez.
        if(!dbVerisi.equals(input)){
            collectionReferenceCumleler.document(documentSnapshot.getId()).update("tr",input);
            Toast.makeText(getContext(),"Türkçe cümle güncellendi.",Toast.LENGTH_SHORT).show();
        }
    }

    private void enCumleKontrol(DocumentSnapshot documentSnapshot,String dbVerisi,String input){
        //databaseden gelen veri inputa eşitse veri değişmemiştir ve güncelleme gerektirmez.
        if(!dbVerisi.equals(input)){
            collectionReferenceCumleler.document(documentSnapshot.getId()).update("en",input);
            Toast.makeText(getContext(),"İngilizce cümle güncellendi.",Toast.LENGTH_SHORT).show();
        }
    }

    private void kategoriKontrol(DocumentSnapshot documentSnapshot,String dbVerisi,String input){
        //databaseden gelen veri inputa eşitse veri değişmemiştir ve güncelleme gerektirmez.
        if(!dbVerisi.equals(input)){
            collectionReferenceCumleler.document(documentSnapshot.getId()).update("kategoriID",taskIDSpinner);
            Toast.makeText(getContext(),"Kategori güncellendi.",Toast.LENGTH_SHORT).show();
        }
    }

    //Düzenlenen cümlenin dialogundaki edittextlerin boş olup olmadığı kontrol edilir.
    private boolean inputIsEmpty(EditText en,EditText tr){
        return !(en.getText().toString().trim().isEmpty() || tr.getText().toString().trim().isEmpty());
    }

    @Override
    public void onStart() {
        super.onStart();
        //listeyiHazirla(taskID);
    }
}
