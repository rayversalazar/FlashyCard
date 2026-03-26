package com.appdev.flashycard.main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appdev.flashycard.R;
import com.appdev.flashycard.database.DatabaseHelper;
import com.appdev.flashycard.database.SessionManager;
import com.appdev.flashycard.main.set.FlashcardSet;
import com.appdev.flashycard.main.set.FlashcardSetAdapter;
import com.appdev.flashycard.overview.OverviewActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FlashcardSetAdapter adapter;
    private List<FlashcardSet> flashcardSets;
    private ImageButton btnProfile;
    private FloatingActionButton fabAddSet;
    private LinearLayout layoutEmpty;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFlashcardSets();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_flashcard_sets);
        btnProfile = findViewById(R.id.btn_profile);
        fabAddSet = findViewById(R.id.fab_add_set);
        layoutEmpty = findViewById(R.id.layout_empty_dashboard);
    }

    private void setupRecyclerView() {
        flashcardSets = new ArrayList<>();
        adapter = new FlashcardSetAdapter(flashcardSets, set -> {
            Intent intent = new Intent(DashboardActivity.this, OverviewActivity.class);
            intent.putExtra("SET_ID", set.getId());
            intent.putExtra("SET_TITLE", set.getTitle());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadFlashcardSets() {
        flashcardSets.clear();
        String email = sessionManager.getUserEmail();
        if (email != null) {
            Cursor userCursor = dbHelper.getUserDetailsByEmail(email);
            if (userCursor != null && userCursor.moveToFirst()) {
                long userId = userCursor.getLong(0);
                userCursor.close();

                Cursor setCursor = dbHelper.getSetsByUser(userId);
                if (setCursor != null && setCursor.moveToFirst()) {
                    do {
                        long id = setCursor.getLong(setCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SET_ID));
                        String title = setCursor.getString(setCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SET_TITLE));
                        String description = setCursor.getString(setCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SET_DESCRIPTION));
                        String color = setCursor.getString(setCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SET_COLOR));
                        flashcardSets.add(new FlashcardSet(id, title, color, description));
                    } while (setCursor.moveToNext());
                    setCursor.close();
                }
            }
        }
        
        if (flashcardSets.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        
        adapter.notifyDataSetChanged();
    }

    private void setupClickListeners() {
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        fabAddSet.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CreateSetActivity.class);
            startActivity(intent);
        });
    }
}
