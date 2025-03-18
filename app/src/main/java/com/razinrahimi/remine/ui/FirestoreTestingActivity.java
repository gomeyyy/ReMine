package com.razinrahimi.remine.ui;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import com.razinrahimi.remine.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FirestoreTestingActivity extends AppCompatActivity {

    EditText titleIn, notesIn, duedateIn, locationIn;
    Button addTaskBtn;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_firestore_testing);

        titleIn = findViewById(R.id.titleEt);
        notesIn = findViewById(R.id.notesEt);
        duedateIn = findViewById(R.id.dueDateEt);
        locationIn = findViewById(R.id.locationEt);
        addTaskBtn = findViewById(R.id.addTaskBtn);

        db = FirebaseFirestore.getInstance();

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = titleIn.getText().toString().trim();
                String notes = notesIn.getText().toString().trim();
                String dueDate = duedateIn.getText().toString().trim();
                String location = locationIn.getText().toString().trim();
                uploadData(title,notes,dueDate,location);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void uploadData(String title, String notes, String dueDate,String location) {

        String id = UUID.randomUUID().toString();

        Map<String , Object> tasks = new HashMap<>();

        tasks.put("id" , id);
        tasks.put("title", title);
        tasks.put("notes", notes);
        tasks.put("dueDate", dueDate);
        tasks.put("location", location);

        db.collection("TestDocuments").document(id).set(tasks)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FirestoreTestingActivity.this, "Uploaded...", Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FirestoreTestingActivity.this, "Failed to Upload Data", Toast.LENGTH_SHORT);
                    }
                });


    }

}

