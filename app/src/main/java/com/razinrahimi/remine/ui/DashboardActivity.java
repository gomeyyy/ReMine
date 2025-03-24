package com.razinrahimi.remine.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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

public class DashboardActivity extends AppCompatActivity {

    //Define UI components
    private Button buttonToDashboard, buttonToMaster, buttonToAccount, buttonToNotes;
    private RecyclerView allTask;
    private List<Task> taskList;
    private TaskAdapter taskAdapter;
    private TextView greetings;
    //Database
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

        //Display all task at dashboard recycler view
        allTask = findViewById(R.id.task_view_dashboard);
        allTask.setLayoutManager(new LinearLayoutManager(this));

        //Initialise task list and adapter
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this);
        allTask.setAdapter(taskAdapter);

        //Initialise UI components
        greetings = findViewById(R.id.username_text);
        buttonToDashboard = findViewById(R.id.button_to_dashboard);
        buttonToAccount = findViewById(R.id.button_to_account);
        buttonToMaster = findViewById(R.id.button_to_master);
        buttonToNotes = findViewById(R.id.button_to_notes);

        buttonToNotes.setOnClickListener(view -> startActivity(new Intent(this, NotesAndTodos.class)));
        buttonToDashboard.setOnClickListener(view -> startActivity(new Intent(this, DashboardActivity.class)));
        buttonToAccount.setOnClickListener(view -> startActivity(new Intent(this, AccountSetting.class)));
        buttonToMaster.setOnClickListener(view -> startActivity(new Intent(this, MasterTimetable.class)));

        //Load user data and tasks
        if (currentUser != null) {
            loadUserData(); // Call loadUserData() only if user is logged in
            fetchTasksFromFirestore();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Retrieve user data from users collection in firestore
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

    //Retrieve user's tasks from tasks collection in firestore
    private void fetchTasksFromFirestore() {
        db.collection("tasks")
                .orderBy("dueDate", Query.Direction.ASCENDING)
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Firestore Error", e.getMessage());
                            return;
                        }
                        taskList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            UserTask task = doc.toObject(UserTask.class); //Create UserTask object from retrieved data

                            taskList.add(task); //add object in tasklist
                        }
                        taskAdapter.setTasks(taskList); //put tasklist in adapter to display
                    }
                });
    }

}