package com.appdev.flashycard.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.appdev.flashycard.main.DashboardActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword;
    private Button btnSignUp;
    private TextView tvLoginLink;
    private CheckBox cbShowPassword;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        
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
        etUsername = findViewById(R.id.editTextUsername);
        etEmail = findViewById(R.id.editTextEmailAddress);
        etPassword = findViewById(R.id.editTextPassword);
        btnSignUp = findViewById(R.id.signupbtn);
        tvLoginLink = findViewById(R.id.tvAlreadyAccount);
        cbShowPassword = findViewById(R.id.checkbox_show_password);
    }

    private void setupListeners() {
        btnSignUp.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidGmail(email)) {
                Toast.makeText(this, "Please enter a valid Gmail address (@gmail.com)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            long result = dbHelper.signUp(username, email, username, password);

            if (result > 0) {
                sessionManager.createLoginSession(email);
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            } else if (result == -1) {
                Toast.makeText(this, "Username or Email already exists", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        cbShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            etPassword.setSelection(etPassword.getText().length());
        });
    }

    private boolean isValidGmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@gmail.com");
    }
}
