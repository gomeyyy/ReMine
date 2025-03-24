package com.razinrahimi.remine.ui;

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

//Page to navigate to user setting pages
public class AccountSetting extends AppCompatActivity {

    //Initialise UI components
    private Button buttonToDashboard, buttonToMaster, buttonToAccount;
    private Button profileEditButton, changePasswordButton, clearDataButton, logoutButton;
    private TextView username;

    //Define database
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_setting);

        //Button to pages
        profileEditButton = findViewById(R.id.profile_edit);
        changePasswordButton = findViewById(R.id.change_password_button);
        clearDataButton = findViewById(R.id.clear_data_button);
        logoutButton = findViewById(R.id.logout_button);

        //Init Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        username = findViewById(R.id.username_display);

        //Event listeners to start new intent
        profileEditButton.setOnClickListener(view -> {
            Toast.makeText(this, "Opening Profile Edit...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AccountProfile.class));
        });
        changePasswordButton.setOnClickListener(view -> {
            Toast.makeText(this, "Opening Change Password...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ChangePassword.class));
        });

        //Display message using Toast
        clearDataButton.setOnClickListener(view -> Toast.makeText(this, "Feature not yet available...", Toast.LENGTH_SHORT).show());
        logoutButton.setOnClickListener(view -> Toast.makeText(this, "Feature not yet available...", Toast.LENGTH_SHORT).show());

        //Dashboard Functions
        buttonToDashboard = findViewById(R.id.button_to_dashboard);
        buttonToAccount = findViewById(R.id.button_to_account);
        buttonToMaster = findViewById(R.id.button_to_master);

        //Event listeners for navigating
        buttonToDashboard.setOnClickListener(view -> startActivity(new Intent(this, DashboardActivity.class)));
        buttonToAccount.setOnClickListener(view -> startActivity(new Intent(this, AccountSetting.class)));
        buttonToMaster.setOnClickListener(view -> startActivity(new Intent(this, MasterTimetable.class)));

        if (currentUser != null) {
            loadUserData(); // Call loadUserData() only if user is logged in
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Display username on top of page
    private void loadUserData() {
        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("username");

                // Display user name in the UI
                if (!TextUtils.isEmpty(name)) {
                    username.setText(name);
                }
            } else {
                Toast.makeText(this, "No profile data found. Please enter your details.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }
}