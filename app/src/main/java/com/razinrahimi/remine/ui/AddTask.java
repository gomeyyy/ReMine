package com.razinrahimi.remine.ui;

import android.content.Intent;
import android.os.Bundle;
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

import com.razinrahimi.remine.R;
import com.razinrahimi.remine.data.HealthTask;
import com.razinrahimi.remine.data.PersonalTask;
import com.razinrahimi.remine.data.Task;
import com.razinrahimi.remine.data.TaskManager;
import com.razinrahimi.remine.data.TaskPriority;
import com.razinrahimi.remine.data.WorkTask;

public class AddTask extends AppCompatActivity {

    EditText titleIn, notesIn, duedateIn, locationIn;
    Button addTaskBtn;
    ImageButton backButton;
    Spinner categorySpinner,prioritySpinner;
    TaskManager taskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_task_activity);

        titleIn = findViewById(R.id.titleEt);
        notesIn = findViewById(R.id.notesEt);
        duedateIn = findViewById(R.id.dueDateEt);
        locationIn = findViewById(R.id.locationEt);
        addTaskBtn = findViewById(R.id.addTaskBtn);
        backButton = findViewById(R.id.back_button);
        categorySpinner = findViewById(R.id.categorySpinner);
        prioritySpinner = findViewById(R.id.prioritySpinner);

        taskManager = new TaskManager(AddTask.this);

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

        backButton.setOnClickListener(view -> startActivity(new Intent(this, MasterTimetable.class))); //Back To Master Timetable

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
    private void uploadData() {

        String title = titleIn.getText().toString().trim();
        String notes = notesIn.getText().toString().trim();
        String dueDate = duedateIn.getText().toString().trim();
        String location = locationIn.getText().toString().trim();

        String priorityText = prioritySpinner.getSelectedItem().toString();
        TaskPriority priority = TaskPriority.valueOf(priorityText.toUpperCase());

        String categorySelected = categorySpinner.getSelectedItem().toString();

        Task newTask; //Kena Tambah UID, Status (Completed or no)

        if (categorySelected.equalsIgnoreCase("Work And Education")) {
            newTask = new WorkTask(title, notes, dueDate, priority, location);
        } else if (categorySelected.equalsIgnoreCase("Personal")) {
            newTask = new PersonalTask(title, notes, dueDate, priority);
        } else if (categorySelected.equalsIgnoreCase("Health")) {
            newTask = new HealthTask(title, notes, dueDate, priority, false); //Placeholder, tak siap lagi
        } else {
            Toast.makeText(this, "Invalid category selected!", Toast.LENGTH_SHORT).show();
            return;
        }
        taskManager.addTask(newTask);

    }
}

