\
package com.utrapp;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private TextView tokenText;
    private Button copyButton;
    private ProgressBar progressBar;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Whether granted or not, attempt to fetch token
                fetchToken();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenText = findViewById(R.id.tokenText);
        copyButton = findViewById(R.id.copyButton);
        progressBar = findViewById(R.id.progressBar);

        copyButton.setEnabled(false);
        copyButton.setOnClickListener(v -> copyTokenToClipboard());

        // Request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                fetchToken();
            }
        } else {
            fetchToken();
        }
    }

    private void fetchToken() {
        progressBar.setVisibility(View.VISIBLE);
        tokenText.setText("Fetching FCM token...");
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    Log.w("FCM", "Fetching FCM registration token failed", e);
                    tokenText.setText("Failed to get token: " + (e != null ? e.getMessage() : "Unknown error"));
                    Toast.makeText(this, "Failed to get token", Toast.LENGTH_SHORT).show();
                    return;
                }
                String token = task.getResult();
                tokenText.setText(token);
                copyButton.setEnabled(true);
            });
    }

    private void copyTokenToClipboard() {
        CharSequence token = tokenText.getText();
        if (token == null || token.length() == 0 || token.toString().startsWith("Failed") || token.toString().startsWith("Fetching")) {
            Toast.makeText(this, "Token not ready yet", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("FCM Token", token);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Token copied", Toast.LENGTH_SHORT).show();
    }
}
