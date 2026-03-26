package com.appdev.flashycard.main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appdev.flashycard.R;
import com.appdev.flashycard.database.DatabaseHelper;
import com.appdev.flashycard.database.SessionManager;
import com.appdev.flashycard.login.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail;
    private ImageButton btnBack;
    private Button btnLogout;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadUserProfile();
        setupClickListeners();
    }

    private void initViews() {
        tvUsername = findViewById(R.id.tv_profile_username);
        tvEmail = findViewById(R.id.tv_profile_email);
        btnBack = findViewById(R.id.btn_back);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void loadUserProfile() {
        String loggedInEmail = sessionManager.getUserEmail();
        
        if (loggedInEmail != null) {
            Cursor cursor = dbHelper.getUserDetailsByEmail(loggedInEmail);
            
            if (cursor != null && cursor.moveToFirst()) {
                // Indices based on the getUserDetailsByEmail query in DatabaseHelper:
                // 0: user_id, 1: full_name, 2: email, 3: username
                String email = cursor.getString(2);
                String username = cursor.getString(3);
                
                tvUsername.setText(username);
                tvEmail.setText(email);
                cursor.close();
            } else {
                Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            redirectToLogin();
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            redirectToLogin();
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
