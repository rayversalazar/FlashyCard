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

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUpLink;
    private CheckBox cbShowPassword;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            goToDashboard();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.editTextEmailAddress);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.loginbtn);
        tvSignUpLink = findViewById(R.id.tvNoAccount);
        cbShowPassword = findViewById(R.id.checkbox_show_password);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!isValidGmail(email)) {
                Toast.makeText(this, "Please enter a valid Gmail address (@gmail.com)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.loginWithEmail(email, password)) {
                sessionManager.createLoginSession(email);
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                goToDashboard();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        tvSignUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        cbShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Move cursor to the end of the text
            etPassword.setSelection(etPassword.getText().length());
        });
    }

    private boolean isValidGmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@gmail.com");
    }

    private void goToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
