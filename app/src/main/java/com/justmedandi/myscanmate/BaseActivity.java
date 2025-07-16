package com.justmedandi.myscanmate;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkDelegasiStatus();
    }

    protected void checkDelegasiStatus() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                                });
                    }
                });
    }
}
