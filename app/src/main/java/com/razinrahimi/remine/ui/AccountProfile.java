package com.razinrahimi.remine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razinrahimi.remine.R;

//Page for displaying and editing user profile
//Note: Editing feature not implemented YET
public class AccountProfile extends AppCompatActivity {

    //Define UI components
    private ImageButton backButton;
    private TextView textViewUserName;
    private EditText editTextEmail, editTextBirthDate, editTextName;
    private Button btnSaveChanges;

    //Define database variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Bind UI Elements
        backButton = findViewById(R.id.back_button);
        textViewUserName = findViewById(R.id.textViewUsername);
        editTextEmail = findViewById(R.id.editTextEmailAddress);
        editTextBirthDate = findViewById(R.id.editTextNumber);
        editTextName = findViewById(R.id.editTextName);
        btnSaveChanges = findViewById(R.id.button);

        //Event listener
        backButton.setOnClickListener(view -> startActivity(new Intent(this, AccountSetting.class)));

        //Check if user is registered and call method to display user data
        if (currentUser != null) {
            loadUserData();
        }

        //Android UI features
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Load and display user details with exeption handler if user details not found or database failed to be opened
    private void loadUserData() {
        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("username");
                String email = documentSnapshot.getString("email");

                // Display user details in the UI
                if (!TextUtils.isEmpty(name)) {
                    textViewUserName.setText(name);
                    editTextName.setText(name);
                }
                if (!TextUtils.isEmpty(email)) {
                    editTextEmail.setText(email);
                }
            } else {
                Toast.makeText(this, "No profile data found. Please enter your details.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }
}