package com.razinrahimi.remine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.razinrahimi.remine.R;

public class AccountSetting extends AppCompatActivity {

    Button buttonToDashboard, buttonToMaster, buttonToAccount;
    Button profileEditButton, changePasswordButton, clearDataButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_setting);

        //Button lain
        profileEditButton = findViewById(R.id.profile_edit);
        changePasswordButton = findViewById(R.id.change_password_button);
        clearDataButton = findViewById(R.id.clear_data_button);
        logoutButton = findViewById(R.id.logout_button);

        profileEditButton.setOnClickListener(view -> {
            Toast.makeText(this, "Opening Profile Edit...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AccountProfile.class));
        });
        changePasswordButton.setOnClickListener(view -> {
            Toast.makeText(this, "Opening Change Password...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ChangePassword.class));
        });
        clearDataButton.setOnClickListener(view -> Toast.makeText(this, "Feature not yet available...", Toast.LENGTH_SHORT).show());
        logoutButton.setOnClickListener(view -> Toast.makeText(this, "Feature not yet available...", Toast.LENGTH_SHORT).show());

        //Dashboard Functions
        buttonToDashboard = findViewById(R.id.button_to_dashboard);
        buttonToAccount = findViewById(R.id.button_to_account);
        buttonToMaster = findViewById(R.id.button_to_master);

        buttonToDashboard.setOnClickListener(view -> startActivity(new Intent(this, DashboardActivity.class)));
        buttonToAccount.setOnClickListener(view -> startActivity(new Intent(this, AccountSetting.class)));
        buttonToMaster.setOnClickListener(view -> startActivity(new Intent(this, MasterTimetable.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}