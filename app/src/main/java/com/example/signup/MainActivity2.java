package com.example.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "MainActivity2";

    private Button signUpBtn;
    private TextInputEditText usernameSignUp, emailUser, passwordSignUp, nimUser;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Inisialisasi Firebase Authentication & Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Inisialisasi UI
        initUI();

        // Set listener untuk tombol daftar
        signUpBtn.setOnClickListener(view -> {
            String username = usernameSignUp.getText().toString().trim();
            String email = emailUser.getText().toString().trim();
            String password = passwordSignUp.getText().toString().trim();
            String NIM = nimUser.getText().toString().trim();

            if (validateInput(username, email, password, NIM)) {
                registerUser(username, email, password, NIM);
            }
        });
    }

    private void initUI() {
        usernameSignUp = findViewById(R.id.usernames);
        emailUser = findViewById(R.id.emails);
        passwordSignUp = findViewById(R.id.passwords);
        nimUser = findViewById(R.id.nims);
        signUpBtn = findViewById(R.id.btnLogin);
    }

    private boolean validateInput(String username, String email, String password, String NIM) {
        if (TextUtils.isEmpty(username)) {
            usernameSignUp.setError("Masukkan Username!");
            usernameSignUp.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailUser.setError("Masukkan Email yang valid!");
            emailUser.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordSignUp.setError("Password minimal 6 karakter!");
            passwordSignUp.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(NIM)) {
            nimUser.setError("Masukkan NIM!");
            nimUser.requestFocus();
            return false;
        }
        return true;
    }

    private void registerUser(String username, String email, String password, String NIM) {
        signUpBtn.setEnabled(false);
        signUpBtn.setText("Mendaftarkan...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    signUpBtn.setEnabled(true);
                    signUpBtn.setText("Daftar");

                    if (task.isSuccessful()) {
                        FirebaseUser fUser = mAuth.getCurrentUser();
                        if (fUser != null) {
                            String uid = fUser.getUid();
                            UserDetails userDetails = new UserDetails(uid, username, email, NIM);
                            databaseReference.child(uid).setValue(userDetails)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            fUser.sendEmailVerification()
                                                    .addOnCompleteListener(emailTask -> {
                                                        if (emailTask.isSuccessful()) {
                                                            Toast.makeText(MainActivity2.this, "Registrasi berhasil! Periksa email Anda untuk verifikasi.", Toast.LENGTH_LONG).show();
                                                            Log.d(TAG, "Registrasi berhasil untuk: " + username);
                                                            startActivity(new Intent(MainActivity2.this, MainActivity.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(MainActivity2.this, "Gagal mengirim email verifikasi.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(MainActivity2.this, "Gagal menyimpan data, coba lagi.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(MainActivity2.this, "Email sudah digunakan!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity2.this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
