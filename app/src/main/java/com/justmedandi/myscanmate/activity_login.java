package com.justmedandi.myscanmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class activity_login extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView goToRegisterTextView;
    ImageView eyeToggle;
    FirebaseAuth mAuth;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi UI
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterTextView = findViewById(R.id.goToRegisterTextView);
        eyeToggle = findViewById(R.id.eyeToggle);

        // Toggle Show/Hide Password
        eyeToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Sembunyikan password
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_eye_closed);
                isPasswordVisible = false;
            } else {
                // Tampilkan password
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_eye_open);
                isPasswordVisible = true;
            }
            // Geser cursor ke akhir
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Login button
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Pergi ke halaman register
        goToRegisterTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, activity_register.class));
        });
    }
}
