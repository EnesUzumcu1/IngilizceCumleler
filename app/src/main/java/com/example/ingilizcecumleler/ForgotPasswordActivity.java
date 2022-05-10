package com.example.ingilizcecumleler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ingilizcecumleler.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth mAuth;
    String eMailRegex, eMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        atamalar();
        binding.sifirlaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eMailKontrol()){
                    String mail = binding.eMailTextInputLayout.getEditText().getText().toString().trim();
                    mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "E-Mailinizi kontrol edin.", Toast.LENGTH_SHORT).show();
                                digerSayfayaGit(LoginActivity.class);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Bir hata oluştu.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void atamalar() {
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        eMailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
    }

    private boolean eMailKontrol(){
        Pattern pattern = Pattern.compile(eMailRegex);
        eMail = binding.eMailTextInputLayout.getEditText().getText().toString().trim();
        boolean check = pattern.matcher(eMail).matches();
        if(check){
            binding.eMailTextInputLayout.setError(null);
        }
        else if(eMail.isEmpty()){
            binding.eMailTextInputLayout.setError("E-Mail boş bırakılamaz!");
        }
        else{
            binding.eMailTextInputLayout.setError("Geçersiz E-Mail!");
        }
        return check;
    }
    public void digerSayfayaGit(Class<?> classAdi){
        Intent intent = new Intent(ForgotPasswordActivity.this,classAdi);
        startActivity(intent);
    }
}