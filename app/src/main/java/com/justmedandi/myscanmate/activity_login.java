package com.justmedandi.myscanmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class activity_login extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView goToRegisterTextView;
    ImageView eyeToggle;
    FirebaseAuth mAuth;
    boolean isPasswordVisible = false;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterTextView = findViewById(R.id.goToRegisterTextView);
        eyeToggle = findViewById(R.id.eyeToggle);

        // Toggle mata password
        eyeToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_eye_closed);
                isPasswordVisible = false;
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_eye_open);
                isPasswordVisible = true;
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Tombol Login
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    checkDelegasiStatusAndContinue();
                } else {
                    Toast.makeText(this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        goToRegisterTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, activity_register.class));
        });
    }

    private void checkDelegasiStatusAndContinue() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    Timestamp until = doc.getTimestamp("delegasi_until");
                    String role = doc.getString("role");

                    if (until != null && until.toDate().before(new Date()) && "delegasi".equals(role)) {
                        FirebaseFirestore.getInstance().collection("users").document(uid)
                                .update("role", "mahasiswa", "delegasi_to", null, "delegasi_until", null)
                                .addOnSuccessListener(v -> {
                                    Toast.makeText(this, "Delegasi kamu telah berakhir", Toast.LENGTH_SHORT).show();
                                    simpanDataUserKeSharedPrefs(doc.getString("nama"), doc.getString("email"), "mahasiswa", doc.getString("gender"));
                                    goToMain();
                                });
                    } else {
                        // Simpan semua data penting ke SharedPreferences
                        simpanDataUserKeSharedPrefs(
                                doc.getString("nama"),
                                doc.getString("email"),
                                doc.getString("role"),
                                doc.getString("gender")
                        );
                        goToMain();
                    }
                })
                .addOnFailureListener(e -> {
                    goToMain(); // lanjut aja walau gagal cek
                });
    }

    private void simpanDataUserKeSharedPrefs(String nama, String email, String role, String gender) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", user.getUid());
        editor.putString("nama", nama);
        editor.putString("email", email);
        editor.putString("role", role);
        editor.putString("gender", gender);
        editor.apply();
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
