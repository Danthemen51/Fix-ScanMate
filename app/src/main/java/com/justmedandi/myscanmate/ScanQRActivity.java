package com.justmedandi.myscanmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        new IntentIntegrator(this).initiateScan(); // langsung buka kamera
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class); // <- agar portrait
        integrator.setOrientationLocked(true); // kunci ke portrait
        integrator.setPrompt("Arahkan kamera ke QR Code ruangan");
        integrator.setBeepEnabled(true); // bunyi beep saat berhasil
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() != null) {
                // QR berhasil dipindai
                String idRuangan = result.getContents();

                // Pindah ke halaman detail
                Intent intent = new Intent(this, DetailRuanganActivity.class);
                intent.putExtra("idRuangan", idRuangan);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}