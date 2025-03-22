package com.razinrahimi.remine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.razinrahimi.remine.R;
import com.razinrahimi.remine.adapters.TaskAdapter;
import com.razinrahimi.remine.data.Task;
import com.razinrahimi.remine.data.UserTask;

import java.util.ArrayList;
import java.util.List;

public class MasterTimetable extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private FirebaseFirestore db;

    private Spinner displayCategory;
    Button goToAddTaskButton, refreshButton;
    Button buttonToDashboard, buttonToMaster, buttonToAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_master_timetable);

        displayCategory = findViewById(R.id.display_category);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this,
                R.array.categories, // Reference to the array
                android.R.layout.simple_spinner_item
        );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        displayCategory.setAdapter(adapter1);

        String defaultCategory = "WorkTask";

        refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(view -> {
            String selectedCategorySpinner = displayCategory.getSelectedItem().toString(); // Get selected category
            String selectedCategory = defaultCategory;
            if (selectedCategorySpinner.equals("Work And Education")) {
                selectedCategory = "WorkTask";
            } else if (selectedCategorySpinner.equals("Personal")) {
                selectedCategory = "PersonalTask";
            } else if (selectedCategorySpinner.equals("Health")) {
                selectedCategory = "HealthTask";
            }
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
            fetchTasksFromFirestore(selectedCategory); // Refresh RecyclerView with selected category
        });


        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore & Task List
        db = FirebaseFirestore.getInstance();
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        // Fetch Tasks from Firestore
        fetchTasksFromFirestore(defaultCategory);

        goToAddTaskButton = findViewById(R.id.goToAddTaskButton);
        // Handle Add Task Button Click
        goToAddTaskButton.setOnClickListener(view ->
                startActivity(new Intent(this, AddTask.class))
        );
      
        goToAddTaskButton = findViewById(R.id.goToAddTaskButton);
        goToAddTaskButton.setOnClickListener(view -> startActivity(new Intent(this, AddTask.class)));

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

    private void fetchTasksFromFirestore(String taskType) {
        db.collection("tasks")
                .whereEqualTo("taskType", taskType) // ✅ Filter by task type
                .orderBy("dueDate", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Firestore Error", e.getMessage());
                            return;
                        }
                        taskList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            UserTask task = doc.toObject(UserTask.class); // ✅ Firestore can now create an instance

                            taskList.add(task);
                        }
                        taskAdapter.setTasks(taskList);
                    }
                });
    }
}
