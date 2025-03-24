package com.razinrahimi.remine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razinrahimi.remine.R;
import com.razinrahimi.remine.data.HealthTask;
import com.razinrahimi.remine.data.PersonalTask;
import com.razinrahimi.remine.data.Task;
import com.razinrahimi.remine.data.TaskManager;
import com.razinrahimi.remine.data.TaskPriority;
import com.razinrahimi.remine.data.TaskStatus;
import com.razinrahimi.remine.data.WorkTask;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddTask extends AppCompatActivity {

    //Define UI components
    private EditText titleIn, notesIn, duedateIn, locationIn;
    private Button addTaskBtn;
    private ImageButton backButton;
    private Spinner categorySpinner,prioritySpinner;
    private TaskManager taskManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_task_activity); //Link to XML

        //Find components and assign variable
        titleIn = findViewById(R.id.titleEt);
        notesIn = findViewById(R.id.notesEt);
        duedateIn = findViewById(R.id.dueDateEt);
        locationIn = findViewById(R.id.locationEt);
        addTaskBtn = findViewById(R.id.addTaskBtn);
        backButton = findViewById(R.id.back_button);
        categorySpinner = findViewById(R.id.categorySpinner);
        prioritySpinner = findViewById(R.id.prioritySpinner);

        //Initialise task manager and database
        taskManager = new TaskManager(AddTask.this);
        db = FirebaseFirestore.getInstance();

        //For task editing
        String taskId = getIntent().getStringExtra("taskId");
        if (taskId != null && !taskId.isEmpty()) {
            displayOldTask(taskId); // Call the function to load existing task
        }

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

        //Event listener for back button
        backButton.setOnClickListener(view -> startActivity(new Intent(this, MasterTimetable.class))); //Back To Master Timetable

        //When add task button is clicked, call uploadData and go back to master timetable
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
                startActivity(new Intent(view.getContext(), MasterTimetable.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Retrieve user input, create an object, and pass to task manager for operations
    private void uploadData() {
        String taskId = getIntent().getStringExtra("taskId");

        //Retrieve user input
        String title = titleIn.getText().toString().trim();
        String notes = notesIn.getText().toString().trim();
        String dueDate = duedateIn.getText().toString().trim();
        String location = locationIn.getText().toString().trim();

        //Convert enum to string
        String priorityText = prioritySpinner.getSelectedItem().toString();
        TaskPriority priority = TaskPriority.valueOf(priorityText.toUpperCase());
        String categorySelected = categorySpinner.getSelectedItem().toString();


        if (title.isEmpty() || dueDate.isEmpty()) {
            Toast.makeText(this, "Task title and due date cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate dueDate format
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedDate = LocalDate.parse(dueDate, formatter); // If format is wrong, exception will be thrown
            dueDate = parsedDate.format(formatter); // Ensure it's saved in the correct format
        } catch (DateTimeParseException e) {
            Toast.makeText(this, "Invalid date format! Use dd-MM-yyyy.", Toast.LENGTH_LONG).show();
            return; // Stop execution
        }

        //Define newTask
        Task newTask;

        //Choose which type of task to create (Work, Health, Personal)
        if (categorySelected.equalsIgnoreCase("Work And Education")) {
            newTask = new WorkTask(title, notes, dueDate, priority, location);
        } else if (categorySelected.equalsIgnoreCase("Personal")) {
            newTask = new PersonalTask(title, notes, dueDate, priority);
        } else if (categorySelected.equalsIgnoreCase("Health")) {
            newTask = new HealthTask(title, notes, dueDate, priority, false); //false is a Placeholder for boolean exercise routine, not implemented YET
        } else {
            Toast.makeText(this, "Invalid category selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Call addTask for adding new task and updateTask for updating task
        if (taskId != null && !taskId.isEmpty()) {
            // Edit existing task, by using taskId to find the task
            newTask.setTaskId(taskId);
            taskManager.updateTask(newTask);
        } else {
            // Add new task
            taskManager.addTask(newTask);
        }

    }

    //Display old task details on EditText as a guide for users when editing
    public void displayOldTask(String taskId) {
        DocumentReference taskRef = db.collection("tasks").document(taskId);

        taskRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String title = documentSnapshot.getString("title");
                String desc = documentSnapshot.getString("notes");
                String dueDate = documentSnapshot.getString("dueDate");
                String loc = documentSnapshot.getString("location");

                // Display existing task details in the UI
                if (title != null) {
                    titleIn.setText(title);
                }
                if (desc != null) {
                    notesIn.setText(desc);
                }
                if (dueDate != null) {
                    duedateIn.setText(dueDate);
                }
                if (loc != null) {
                    locationIn.setText(loc);
                }
            } else {
                Toast.makeText(this, "No existing task found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load task data", Toast.LENGTH_SHORT).show());
    }

}

