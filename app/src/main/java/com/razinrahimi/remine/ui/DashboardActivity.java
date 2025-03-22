package com.razinrahimi.remine.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
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

public class DashboardActivity extends AppCompatActivity {

    private Button buttonToDashboard, buttonToMaster, buttonToAccount;
    private TextView greetings;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        greetings = findViewById(R.id.username_text);
        buttonToDashboard = findViewById(R.id.button_to_dashboard);
        buttonToAccount = findViewById(R.id.button_to_account);
        buttonToMaster = findViewById(R.id.button_to_master);

        buttonToDashboard.setOnClickListener(view -> startActivity(new Intent(this, DashboardActivity.class)));
        buttonToAccount.setOnClickListener(view -> startActivity(new Intent(this, AccountSetting.class)));
        buttonToMaster.setOnClickListener(view -> startActivity(new Intent(this, MasterTimetable.class)));

        loadUserData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadUserData() {
        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("username");

                // Display user details in the UI
                if (!TextUtils.isEmpty(name)) {
                    greetings.setText(getString(R.string.greeting, name));
                }
            } else {
                Toast.makeText(this, "No profile data found. Please enter your details.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }
}