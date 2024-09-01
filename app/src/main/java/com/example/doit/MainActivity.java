package com.example.doit;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(view -> showAddTaskDialog());

        // Add ItemTouchHelper for swipe actions
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                showEditTaskDialog(viewHolder.getAdapterPosition());
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Task");

        final EditText input = new EditText(this);
        input.setHint("Add Task");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String taskText = input.getText().toString().trim();
            if (!TextUtils.isEmpty(taskText)) {
                taskList.add(new Task(taskText));
                taskAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showEditTaskDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        final EditText input = new EditText(this);
        input.setText(taskList.get(position).getText());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String taskText = input.getText().toString().trim();
            if (!TextUtils.isEmpty(taskText)) {
                taskList.get(position).setText(taskText);
                taskAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            taskAdapter.notifyItemChanged(position); // Restore the item state if editing is canceled
        });

        builder.show();
    }

    // TaskAdapter class
    public static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

        private List<Task> taskList;

        public TaskAdapter(List<Task> taskList) {
            this.taskList = taskList;
        }

        @Override
        public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemtask, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TaskViewHolder holder, int position) {
            Task task = taskList.get(position);
            holder.taskCheckBox.setText(task.getText());

            holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setMessage("Are you sure you want to delete this task?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                taskList.remove(position);
                                notifyDataSetChanged();
                            })
                            .setNegativeButton("No", (dialog, which) -> holder.taskCheckBox.setChecked(false))
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        public static class TaskViewHolder extends RecyclerView.ViewHolder {
            CheckBox taskCheckBox;

            public TaskViewHolder(View itemView) {
                super(itemView);
                taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            }
        }
    }

    // Task model class
    public static class Task {
        private String text;

        public Task(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
