package com.example.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private TextInputEditText username, password;
    private CheckBox checkboxes;
    private Button btlogin;
    private TextView signUpp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        initUI();

        btlogin.setOnClickListener(view -> loginUser());

        signUpp.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        });
    }

    private void initUI() {
        username = findViewById(R.id.email);
        password = findViewById(R.id.password);
        checkboxes = findViewById(R.id.checkboxes);
        btlogin = findViewById(R.id.btnLogin);
        signUpp = findViewById(R.id.signUp);
    }

    private void loginUser() {
        String email = username.getText() != null ? username.getText().toString().trim() : "";
        String pass = password.getText() != null ? password.getText().toString().trim() : "";

        if (!validateInput(email, pass)) {
            return;
        }

        btlogin.setEnabled(false);
        btlogin.setText("Memproses...");

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {

                    btlogin.setEnabled(true);
                    btlogin.setText("Login");

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Toast.makeText(MainActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Login berhasil: " + user.getEmail());

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Silakan verifikasi email Anda sebelum login!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Login gagal: " + task.getException().getMessage());
                    }
                });
    }

    private boolean validateInput(String email, String pass) {
        if (TextUtils.isEmpty(email)) {
            username.setError("Masukkan email!");
            username.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            username.setError("Format email tidak valid!");
            username.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(pass)) {
            password.setError("Masukkan password!");
            password.requestFocus();
            return false;
        }
        if (pass.length() < 6) {
            password.setError("Password harus minimal 6 karakter!");
            password.requestFocus();
            return false;
        }
        return true;
    }
}
