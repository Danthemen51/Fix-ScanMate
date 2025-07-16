package com.justmedandi.myscanmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class activity_register extends AppCompatActivity {

    EditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText,
            nimEditText, alamatEditText, phoneEditText, kelasEditText, jurusanEditText;

    Spinner genderSpinner, roleSpinner;
    Button registerButton;
    TextView goToLoginTextView;
    ImageView eyeToggle;

    FirebaseAuth mAuth;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi view
        emailEditText = findViewById(R.id.registerEmailEditText);
        passwordEditText = findViewById(R.id.registerPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        nameEditText = findViewById(R.id.registerNameEditText);
        nimEditText = findViewById(R.id.registerNimEditText);
        alamatEditText = findViewById(R.id.registerAlamatEditText);
        phoneEditText = findViewById(R.id.registerPhoneEditText);
        kelasEditText = findViewById(R.id.registerKelasEditText);
        jurusanEditText = findViewById(R.id.registerJurusanEditText);
        genderSpinner = findViewById(R.id.spinnerGender);
        roleSpinner = findViewById(R.id.spinnerRole);
        registerButton = findViewById(R.id.registerButton);
        goToLoginTextView = findViewById(R.id.goToLoginTextView);
        eyeToggle = findViewById(R.id.eyeToggle);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.gender_options, R.layout.spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(
                this, R.array.role_options, R.layout.spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);


        // Toggle password visibility
        eyeToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_eye_closed);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_eye_open);
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        // Tombol Daftar
        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String nim = nimEditText.getText().toString().trim();
            String gender = genderSpinner.getSelectedItem().toString();
            String alamat = alamatEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String kelas = kelasEditText.getText().toString().trim();
            String jurusan = jurusanEditText.getText().toString().trim();
            String role = roleSpinner.getSelectedItem().toString();

            // Validasi simple
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Password dan Konfirmasi tidak cocok", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("email", email);
                                userMap.put("name", name);
                                userMap.put("nim", nim);
                                userMap.put("gender", gender);
                                userMap.put("alamat", alamat);
                                userMap.put("phone", phone);
                                userMap.put("kelas", kelas);
                                userMap.put("jurusan", jurusan);
                                userMap.put("role", role);

                                FirebaseFirestore.getInstance().collection("users")
                                        .document(uid)
                                        .set(userMap)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, MainActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Gagal simpan profil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        goToLoginTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, activity_login.class));
        });
    }
}
