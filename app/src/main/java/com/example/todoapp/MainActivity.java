package com.example.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTasks;
    private TextView tvEmptyState;
    private ExtendedFloatingActionButton fabAddTask;

    private TaskAdapter taskAdapter;
    private DatabaseHelper dbHelper;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);
        
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAddTask = findViewById(R.id.fabAddTask);

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        
        loadTasks();

        fabAddTask.setOnClickListener(v -> showAddTaskDialog());
        
        // Hide FAB text on scroll down, expand on scroll up
        recyclerViewTasks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAddTask.shrink();
                } else if (dy < 0) {
                    fabAddTask.extend();
                }
            }
        });
    }

    private void loadTasks() {
        taskList = dbHelper.getAllTasks();
        if (taskList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerViewTasks.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerViewTasks.setVisibility(View.VISIBLE);
        }

        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(taskList, dbHelper);
            recyclerViewTasks.setAdapter(taskAdapter);
        } else {
            taskAdapter.setTasks(taskList);
        }
    }

    private void showAddTaskDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.RoundedBottomSheetDialog);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_add_task, null);
        dialog.setContentView(view);

        TextInputEditText etTaskTitle = view.findViewById(R.id.etTaskTitle);
        MaterialButton btnSelectDate = view.findViewById(R.id.btnSelectDate);
        MaterialButton btnSelectTime = view.findViewById(R.id.btnSelectTime);
        MaterialButton btnSaveTask = view.findViewById(R.id.btnSaveTask);

        final String[] selectedDate = {""};
        final String[] selectedTime = {""};

        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(MainActivity.this, (view1, year, month, dayOfMonth) -> {
                selectedDate[0] = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                btnSelectDate.setText(selectedDate[0]);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        btnSelectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePicker = new TimePickerDialog(MainActivity.this, (view12, hourOfDay, minute) -> {
                selectedTime[0] = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                btnSelectTime.setText(selectedTime[0]);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePicker.show();
        });

        btnSaveTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText() != null ? etTaskTitle.getText().toString().trim() : "";
            if (TextUtils.isEmpty(title)) {
                etTaskTitle.setError("Vui lòng nhập tên công việc");
                return;
            }

            String timeString = "";
            if (!selectedDate[0].isEmpty() && !selectedTime[0].isEmpty()) {
                timeString = selectedTime[0] + " - " + selectedDate[0];
            } else if (!selectedDate[0].isEmpty()) {
                timeString = selectedDate[0];
            } else if (!selectedTime[0].isEmpty()) {
                timeString = selectedTime[0];
            }

            Task task = new Task(0, title, timeString, false);
            dbHelper.addTask(task);
            loadTasks();
            dialog.dismiss();
            Toast.makeText(MainActivity.this, "Đã thêm công việc", Toast.showLength_SHORT).show();
        });

        dialog.show();
    }
}
