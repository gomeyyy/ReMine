package com.razinrahimi.remine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.razinrahimi.remine.R;

public class Introduction extends AppCompatActivity {

    Button toReg,toLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_introduction);

        toReg = findViewById(R.id.buttonToReg);
        toLogin = findViewById((R.id.buttonToLogin));

        toLogin.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));
        toReg.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}