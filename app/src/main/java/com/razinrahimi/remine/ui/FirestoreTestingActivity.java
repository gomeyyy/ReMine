package com.razinrahimi.remine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.UUID;

public class FirestoreTestingActivity extends AppCompatActivity {

    EditText titleIn, notesIn, duedateIn, locationIn;
    Button addTaskBtn;
    Spinner categorySpinner,prioritySpinner;
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
        categorySpinner = findViewById(R.id.categorySpinner);
        prioritySpinner = findViewById(R.id.prioritySpinner);

        db = FirebaseFirestore.getInstance();

        //Select Category
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this,
                R.array.categories, // Reference to the array
                android.R.layout.simple_spinner_item
        );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter1);

        //Select Priority
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this,
                R.array.priorities, // Reference to the array
                android.R.layout.simple_spinner_item
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter2);

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = titleIn.getText().toString().trim();
                String notes = notesIn.getText().toString().trim();
                String dueDate = duedateIn.getText().toString().trim();
                String location = locationIn.getText().toString().trim();
                uploadData(title,notes,dueDate,location);
                startActivity(new Intent(view.getContext(), MasterTimetable.class));
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
                        Toast.makeText(FirestoreTestingActivity.this, "Uploaded...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FirestoreTestingActivity.this, "Failed to Upload Data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

