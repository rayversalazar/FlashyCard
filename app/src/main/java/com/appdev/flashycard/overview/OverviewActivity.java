package com.appdev.flashycard.overview;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.appdev.flashycard.R;
import com.appdev.flashycard.pressplay.PlayActivity;
import com.google.android.material.button.MaterialButton;

public class OverviewActivity extends AppCompatActivity {

    private long setId;
    private String setTitle;
    private TextView tvHeaderTitle;
    private ImageButton btnBack;
    private Button btnView, btnEdit, btnPerformance;
    private MaterialButton btnAction;

    private enum Mode { VIEW, EDIT, PERFORMANCE }
    private Mode currentMode = Mode.VIEW;
    private EditSetFragment editFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_overview);

        setId = getIntent().getLongExtra("SET_ID", -1);
        setTitle = getIntent().getStringExtra("SET_TITLE");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();

        loadFragment(ViewSetFragment.newInstance(setId));
        updateActionButton(Mode.VIEW);
    }

    private void initViews() {
        tvHeaderTitle = findViewById(R.id.text_set_name);
        btnBack = findViewById(R.id.btn_back);
        btnView = findViewById(R.id.btn_nav_view);
        btnEdit = findViewById(R.id.btn_nav_edit);
        btnPerformance = findViewById(R.id.btn_nav_performance);
        btnAction = findViewById(R.id.btn_play);

        if (setTitle != null) {
            tvHeaderTitle.setText(setTitle);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnView.setOnClickListener(v -> {
            loadFragment(ViewSetFragment.newInstance(setId));
            updateActionButton(Mode.VIEW);
        });

        btnEdit.setOnClickListener(v -> {
            if (editFragment == null) {
                editFragment = EditSetFragment.newInstance(setId);
            }
            loadFragment(editFragment);
            updateActionButton(Mode.EDIT);
        });

        btnPerformance.setOnClickListener(v -> {
            updateActionButton(Mode.PERFORMANCE);
        });

        btnAction.setOnClickListener(v -> {
            if (currentMode == Mode.VIEW) {
                Intent intent = new Intent(OverviewActivity.this, PlayActivity.class);
                intent.putExtra("SET_ID", setId);
                startActivity(intent);
            } else if (currentMode == Mode.EDIT) {
                if (editFragment != null && editFragment.isVisible()) {
                    editFragment.addNewBlankCard();
                } else {
                    // If fragment was swapped out, re-instantiate or show Toast
                    Toast.makeText(this, "Please go to Edit tab first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateActionButton(Mode mode) {
        currentMode = mode;
        highlightButton(mode);

        switch (mode) {
            case VIEW:
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setText("Play");
                btnAction.setIconResource(android.R.drawable.ic_media_play);
                break;
            case EDIT:
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setText("Add Card");
                btnAction.setIconResource(android.R.drawable.ic_input_add);
                break;
            case PERFORMANCE:
                btnAction.setVisibility(View.GONE);
                break;
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void highlightButton(Mode mode) {
        btnView.setTextColor(Color.WHITE);
        btnEdit.setTextColor(Color.WHITE);
        btnPerformance.setTextColor(Color.WHITE);

        switch (mode) {
            case VIEW: btnView.setTextColor(Color.parseColor("#A9EFFF")); break;
            case EDIT: btnEdit.setTextColor(Color.parseColor("#A9EFFF")); break;
            case PERFORMANCE: btnPerformance.setTextColor(Color.parseColor("#A9EFFF")); break;
        }
    }
}
