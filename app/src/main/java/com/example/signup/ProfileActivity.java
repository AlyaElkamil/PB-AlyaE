package com.example.signup;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private Button btnBack;
    private TextView txtUsername, txtEmail, txtNIM;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        txtUsername = findViewById(R.id.user_name);
        txtEmail = findViewById(R.id.user_email);
        txtNIM = findViewById(R.id.user_nim);
        btnBack = findViewById(R.id.btn_logout);

        loadUserData();

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserDetails userDetails = snapshot.getValue(UserDetails.class);
                        if (userDetails != null) {
                            txtUsername.setText("Username: " + userDetails.username);
                            txtEmail.setText("Email: " + userDetails.email);
                            txtNIM.setText("NIM: " + userDetails.NIM);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Gagal mengambil data!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
