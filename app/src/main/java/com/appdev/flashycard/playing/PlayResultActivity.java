package com.appdev.flashycard.playing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appdev.flashycard.R;
import com.appdev.flashycard.overview.OverviewActivity;
import com.google.android.material.button.MaterialButton;

public class PlayResultActivity extends AppCompatActivity {

    private long setId;
    private int correctCount, wrongCount, totalCount;
    private boolean isShuffle, isTermFirst;

    private TextView tvAccuracy, tvCorrect, tvWrong;
    private MaterialButton btnTryAgain, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_result);

        // Get intent data
        setId = getIntent().getLongExtra("SET_ID", -1);
        correctCount = getIntent().getIntExtra("CORRECT_COUNT", 0);
        wrongCount = getIntent().getIntExtra("WRONG_COUNT", 0);
        totalCount = getIntent().getIntExtra("TOTAL_COUNT", 0);
        isShuffle = getIntent().getBooleanExtra("SHUFFLE", false);
        isTermFirst = getIntent().getBooleanExtra("TERM_FIRST", false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        displayResults();
        setupListeners();
    }

    private void initViews() {
        tvAccuracy = findViewById(R.id.tv_accuracy_rate);
        tvCorrect = findViewById(R.id.tv_correct_count);
        tvWrong = findViewById(R.id.tv_wrong_count);
        btnTryAgain = findViewById(R.id.btn_try_again);
        btnExit = findViewById(R.id.btn_exit_results);
    }

    private void displayResults() {
        tvCorrect.setText(String.valueOf(correctCount));
        tvWrong.setText(String.valueOf(wrongCount));

        if (totalCount > 0) {
            int accuracy = (int) (((double) correctCount / totalCount) * 100);
            tvAccuracy.setText(accuracy + "%");
        } else {
            tvAccuracy.setText("0%");
        }
    }

    private void setupListeners() {
        btnTryAgain.setOnClickListener(v -> {
            Intent intent = new Intent(PlayResultActivity.this, PlayingActivity.class);
            intent.putExtra("SET_ID", setId);
            intent.putExtra("SHUFFLE", isShuffle);
            intent.putExtra("TERM_FIRST", isTermFirst);
            startActivity(intent);
            finish();
        });

        btnExit.setOnClickListener(v -> {
            // Navigate back to OverviewActivity and clear intermediate activities (like PlayActivity)
            Intent intent = new Intent(PlayResultActivity.this, OverviewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("SET_ID", setId);
            startActivity(intent);
            finish();
        });
    }
}
