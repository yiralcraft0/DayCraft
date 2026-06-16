package com.yiralcraft.myapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashSet;
import java.util.Set;

public class MainApp extends AppCompatActivity {

    private LinearLayout verticalTaskList;
    private SharedPreferences prefs;
    private EditText searchBar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.mainapp);

        // Initialize SharedPreferences
        prefs = getSharedPreferences("TodoApp", MODE_PRIVATE);

        // Handle Window Insets for Edge-to-Edge execution
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI Views
        CardView addList = findViewById(R.id.addList);
        ImageButton button = findViewById(R.id.imageButton);
        searchBar = findViewById(R.id.searchBar);
        verticalTaskList = findViewById(R.id.verticalTaskList);

        // Load and populate saved tasks
        Set<String> savedTasks = prefs.getStringSet("tasks", new HashSet<>());
        for (String task : savedTasks) {
            addTask(task);
        }

        // Handle Touch feedback animations for the button container
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    addList.setCardBackgroundColor(Color.WHITE);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    addList.setCardBackgroundColor(Color.parseColor("#DEDEDE"));
                    break;
            }
            return false;
        });

        // Click listener to add a new task
        button.setOnClickListener(v -> {
            String searchText = searchBar.getText().toString().trim();

            if (searchText.trim().isEmpty()) {
                Toast.makeText(this, "Enter a task first!", Toast.LENGTH_SHORT).show();
                return;
            }

            // UI Update & Storage Update
            addTask(searchText);
            saveTask(searchText);

            // Reset input field
            searchBar.setText("");
            Toast.makeText(this, "Task Added 👍", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Dynamically creates a task row item and adds it to the layout view.
     */
    private void addTask(String taskTextValue) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView taskText = new TextView(this);
        taskText.setText(taskTextValue);
        taskText.setTextSize(25);
        taskText.setTextColor(Color.BLACK);
        taskText.setPadding(20, 20, 20, 20);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        taskText.setLayoutParams(textParams);

        CheckBox checkBox = new CheckBox(this);

        // Assemble the row view
        row.addView(taskText);
        row.addView(checkBox);
        verticalTaskList.addView(row);

        // Handle Task Deletion on Long Press
        row.setOnLongClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Do You Want To Delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        verticalTaskList.removeView(row);
                        deleteTaskFromStorage(taskTextValue);
                        Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
    }

    /**
     * Helper to append a single task string into SharedPreferences
     */
    private void saveTask(String task) {
        Set<String> currentTasks = prefs.getStringSet("tasks", new HashSet<>());
        Set<String> updatedTasks = new HashSet<>(currentTasks); // Create copy to safely update
        updatedTasks.add(task);
        prefs.edit().putStringSet("tasks", updatedTasks).apply();
    }

    /**
     * Helper to remove a single task string from SharedPreferences
     */
    private void deleteTaskFromStorage(String task) {
        Set<String> currentTasks = prefs.getStringSet("tasks", new HashSet<>());
        Set<String> updatedTasks = new HashSet<>(currentTasks); // Create copy to safely update
        updatedTasks.remove(task);
        prefs.edit().putStringSet("tasks", updatedTasks).apply();
    }
}