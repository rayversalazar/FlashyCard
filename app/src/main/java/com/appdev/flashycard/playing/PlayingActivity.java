package com.appdev.flashycard.playing;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appdev.flashycard.R;
import com.appdev.flashycard.database.DatabaseHelper;
import com.appdev.flashycard.overview.card.Flashcard;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayingActivity extends AppCompatActivity {

    private long setId;
    private boolean isShuffle;
    private boolean isTermFirst;

    private TextView tvProgress, tvCardContent, tvStateLabel, tvScoreTracker;
    private ImageButton btnClose;
    private MaterialButton btnAction, btnCorrect, btnWrong;
    private LinearLayout layoutFlippedActions;

    private List<Flashcard> flashcards;
    private int currentIndex = 0;
    private boolean isFlipped = false;
    private int correctCount = 0;
    private int wrongCount = 0;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playing);

        dbHelper = new DatabaseHelper(this);

        // Get intent data
        setId = getIntent().getLongExtra("SET_ID", -1);
        isShuffle = getIntent().getBooleanExtra("SHUFFLE", false);
        isTermFirst = getIntent().getBooleanExtra("TERM_FIRST", false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadFlashcards();
        setupListeners();
        updateUI();
    }

    private void initViews() {
        tvProgress = findViewById(R.id.text_progress);
        tvCardContent = findViewById(R.id.tv_card_content);
        tvStateLabel = findViewById(R.id.tv_state_label);
        tvScoreTracker = findViewById(R.id.tv_score_tracker);
        btnClose = findViewById(R.id.btn_close_playing);
        btnAction = findViewById(R.id.btn_action_playing);
        btnCorrect = findViewById(R.id.btn_correct);
        btnWrong = findViewById(R.id.btn_wrong);
        layoutFlippedActions = findViewById(R.id.layout_flipped_actions);
    }

    private void loadFlashcards() {
        flashcards = new ArrayList<>();
        Cursor cursor = dbHelper.getCardsBySet(setId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARD_ID));
                String term = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARD_TERM));
                String definition = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARD_DEFINITION));
                flashcards.add(new Flashcard(id, setId, term, definition));
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (flashcards.isEmpty()) {
            Toast.makeText(this, "No cards in this set!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (isShuffle) {
            Collections.shuffle(flashcards);
        }
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> finish());

        btnAction.setOnClickListener(v -> {
            if (!isFlipped) {
                // Flip logic
                isFlipped = true;
                updateUI();
            }
        });

        btnCorrect.setOnClickListener(v -> {
            correctCount++;
            nextCard();
        });

        btnWrong.setOnClickListener(v -> {
            wrongCount++;
            nextCard();
        });
    }

    private void nextCard() {
        if (currentIndex < flashcards.size() - 1) {
            currentIndex++;
            isFlipped = false;
            updateUI();
        } else {
            // Navigate to PlayResultActivity
            Intent intent = new Intent(PlayingActivity.this, PlayResultActivity.class);
            intent.putExtra("SET_ID", setId);
            intent.putExtra("CORRECT_COUNT", correctCount);
            intent.putExtra("WRONG_COUNT", wrongCount);
            intent.putExtra("TOTAL_COUNT", flashcards.size());
            intent.putExtra("SHUFFLE", isShuffle);
            intent.putExtra("TERM_FIRST", isTermFirst);
            startActivity(intent);
            finish();
        }
    }

    private void updateUI() {
        if (flashcards == null || flashcards.isEmpty()) return;

        Flashcard currentCard = flashcards.get(currentIndex);
        
        // Update Progress & Score
        tvProgress.setText((currentIndex + 1) + " / " + flashcards.size());
        tvScoreTracker.setText("✓ " + correctCount + "  ✗ " + wrongCount);

        if (!isFlipped) {
            // Unflipped state
            tvStateLabel.setText("UNFLIPPED");
            tvCardContent.setText(isTermFirst ? currentCard.getTerm() : currentCard.getDefinition());
            
            btnAction.setVisibility(View.VISIBLE);
            layoutFlippedActions.setVisibility(View.GONE);
        } else {
            // Flipped state
            tvStateLabel.setText("FLIPPED");
            tvCardContent.setText(isTermFirst ? currentCard.getDefinition() : currentCard.getTerm());
            
            btnAction.setVisibility(View.GONE);
            layoutFlippedActions.setVisibility(View.VISIBLE);
        }
    }
}
