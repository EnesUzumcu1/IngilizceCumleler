package com.example.ingilizcecumleler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ingilizcecumleler.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    String eMailRegex, eMail, sifre;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        atamalar();
        binding.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                digerSayfayaGit(ForgotPasswordActivity.class);
            }
        });
        binding.girisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oturumAcmaKontrolu();
                beniHatirla();
            }
        });
    }

    public void atamalar(){
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        eMailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        //Beni hatırla kısmı
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            binding.textInputEmail.getEditText().setText(loginPreferences.getString("username", ""));
            binding.textInputPassword.getEditText().setText(loginPreferences.getString("password", ""));
            binding.beniHatirlacheckBox.setChecked(true);
        }
    }

    private boolean eMailKontrol(){
        Pattern pattern = Pattern.compile(eMailRegex);
        eMail = binding.textInputEmail.getEditText().getText().toString().trim();
        boolean check = pattern.matcher(eMail).matches();
        if(check){
            binding.textInputEmail.setError(null);
        }
        else if(eMail.isEmpty()){
            binding.textInputEmail.setError("E-Mail boş bırakılamaz!");
        }
        else{
            binding.textInputEmail.setError("Geçersiz E-Mail!");
        }
        return check;
    }

    private boolean sifreKontrol(){
        sifre = binding.textInputPassword.getEditText().getText().toString().trim();
        if(sifre.isEmpty()){
            binding.textInputPassword.setError("Şifre boş bırakılamaz!");
            return false;
        }
        else{
            binding.textInputPassword.setError(null);
            return true;
        }
    }

    public void oturumAcmaKontrolu(){
        if(eMailKontrol() && sifreKontrol()){
            mAuth.signInWithEmailAndPassword(eMail,sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        sayfayiTemizle();
                        digerSayfayaGit(HomePageActivity.class);
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(), "Bilgiler yanlış veya hatalı deneme", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        }
    }

    public void digerSayfayaGit(Class<?> classAdi){
        Intent intent = new Intent(LoginActivity.this,classAdi);
        startActivity(intent);
    }

    public void sayfayiTemizle(){
        binding.textInputEmail.getEditText().setText("");
        binding.textInputPassword.getEditText().setText("");
    }

    public void beniHatirla(){
        if(binding.beniHatirlacheckBox.isChecked()){
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("username", eMail);
            loginPrefsEditor.putString("password", sifre);
            loginPrefsEditor.commit();
        }
        else{
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
    }
}