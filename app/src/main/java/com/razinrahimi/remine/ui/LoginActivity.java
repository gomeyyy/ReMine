package com.razinrahimi.remine.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import com.razinrahimi.remine.R;

public class LoginActivity extends AppCompatActivity {

    //Define UI components
    private EditText emailInput, passwordInput;
    //Define Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //Init firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //Init UI components for user input
        emailInput = findViewById(R.id.user_email_input);
        passwordInput = findViewById(R.id.user_pass_input);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);

        //On Click listener
        loginButton.setOnClickListener(view -> loginUser());
        registerButton.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.title_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Login user method
    private void loginUser() {
        //Retrieve user input
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        //Check if field is empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        //Firebase authentication login function
        mAuth.signInWithEmailAndPassword(email, password)
                //Built in exception for wrong credentials
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        //Go to main page if login successful
                        startActivity(new Intent(this, DashboardActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}