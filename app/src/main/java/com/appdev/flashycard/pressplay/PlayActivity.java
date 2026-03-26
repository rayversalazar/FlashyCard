package com.appdev.flashycard.pressplay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appdev.flashycard.R;
import com.appdev.flashycard.playing.PlayingActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class PlayActivity extends AppCompatActivity {

    private long setId;
    private ImageButton btnBack;
    private SwitchMaterial switchShuffle, switchTermFirst;
    private MaterialButton btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play);

        setId = getIntent().getLongExtra("SET_ID", -1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        switchShuffle = findViewById(R.id.switch_shuffle);
        switchTermFirst = findViewById(R.id.switch_term_first);
        btnPlay = findViewById(R.id.btn_play_session);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlay.setOnClickListener(v -> {
            boolean shuffle = switchShuffle.isChecked();
            boolean termFirst = switchTermFirst.isChecked();
            
            Intent intent = new Intent(PlayActivity.this, PlayingActivity.class);
            intent.putExtra("SET_ID", setId);
            intent.putExtra("SHUFFLE", shuffle);
            intent.putExtra("TERM_FIRST", termFirst);
            startActivity(intent);
        });
    }
}
