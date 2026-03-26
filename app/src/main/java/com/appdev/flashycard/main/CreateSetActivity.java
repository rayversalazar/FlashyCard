package com.appdev.flashycard.main;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appdev.flashycard.R;
import com.appdev.flashycard.database.DatabaseHelper;
import com.appdev.flashycard.database.SessionManager;

import java.util.Random;

public class CreateSetActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button btnCreate;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_set);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_set_title);
        etDescription = findViewById(R.id.et_set_description);
        btnCreate = findViewById(R.id.btn_create_set);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            long userId = getUserId();
            if (userId != -1) {
                // Generate a random color for the set
                String randomColor = getRandomColor();
                
                long setId = dbHelper.addFlashcardSet(userId, title, description, randomColor);
                if (setId != -1) {
                    Toast.makeText(this, "Set created successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to Dashboard
                } else {
                    Toast.makeText(this, "Failed to create set", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User error. Please login again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long getUserId() {
        String email = sessionManager.getUserEmail();
        if (email != null) {
            Cursor cursor = dbHelper.getUserDetailsByEmail(email);
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(0); // user_id is the first column
                cursor.close();
                return id;
            }
        }
        return -1;
    }

    private String getRandomColor() {
        String[] colors = {"#26C6DA", "#42A5F5", "#66BB6A", "#AB47BC", "#FF8A65", "#CE93D8", "#AED581", "#4DD0E1"};
        return colors[new Random().nextInt(colors.length)];
    }
}
