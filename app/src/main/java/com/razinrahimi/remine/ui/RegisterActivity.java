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

import com.google.firebase.firestore.FirebaseFirestore;
import com.razinrahimi.remine.R;
import com.razinrahimi.remine.data.Users;

public class RegisterActivity extends AppCompatActivity {

    //Define UI components
    private EditText registerEmailInput, registerPasswordInput,confirmPasswordInput, usernameInput;
    //Define database variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        //Init Firebase authentication and firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Find UI components using findViewById
        registerEmailInput = findViewById(R.id.registerEmailInput);
        registerPasswordInput = findViewById(R.id.registerPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        usernameInput = findViewById(R.id.username_input);

        Button registerUserButton = findViewById(R.id.registerUserButton);
        Button loginPageButton = findViewById(R.id.loginPageButton);

        //call registerUser method on click
        registerUserButton.setOnClickListener(view -> registerUser());
        //go to login page onclick
        loginPageButton.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_page_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void registerUser() {
        //Retrieve user input and change into String
        String email = registerEmailInput.getText().toString().trim();
        String password = registerPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();

        //Check if required fields are empty
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        //Check if confirm password and password the same
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        //Firebase authentication function to create new user using email and password
        //User Id is automatically assigned by firebase
        //Built-in exception handler for firebase and firestore operations
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        String userID = mAuth.getCurrentUser().getUid();
                        Users user = new Users(userID, email, username);

                        //Store User's insensitive details into firestore users collection
                        //Go to login page upon successful registration
                        db.collection("users").document(userID)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, LoginActivity.class));
                                    finish();
                                })
                                //Notify user if user data not stored in firestore
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to store user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        //notify user if registration failed
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
