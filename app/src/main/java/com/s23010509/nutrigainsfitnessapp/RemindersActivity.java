package com.s23010509.nutrigainsfitnessapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RemindersActivity extends AppCompatActivity {

    private List<Reminder> reminders = new ArrayList<>();
    private ReminderAdapter adapter;
    private EditText etReminderTitle;
    private TextView tvSelectedDateTime;
    private Calendar selectedDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);


        NotificationHelper.createNotificationChannel(this);

        // Initialize views
        etReminderTitle = findViewById(R.id.etReminderTitle);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);
        Button btnSetTime = findViewById(R.id.btnSetTime);
        Button btnSetDate = findViewById(R.id.btnSetDate);
        Button btnAddReminder = findViewById(R.id.btnAddReminder);
        RecyclerView rvReminders = findViewById(R.id.rvReminders);

        adapter = new ReminderAdapter(reminders);
        rvReminders.setLayoutManager(new LinearLayoutManager(this));
        rvReminders.setAdapter(adapter);

        // Set time button click
        btnSetTime.setOnClickListener(v -> showTimePicker());

        // Set date button click
        btnSetDate.setOnClickListener(v -> showDatePicker());

        // Add reminder button click
        btnAddReminder.setOnClickListener(v -> addReminder());

        // Back button click (goes to HomeActivity)
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RemindersActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateDateTimeDisplay();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeDisplay();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        String time = timeFormat.format(selectedDateTime.getTime());
        String date = dateFormat.format(selectedDateTime.getTime());
        tvSelectedDateTime.setText(String.format("%s at %s", date, time));
    }

    private void addReminder() {
        String title = etReminderTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a reminder title", Toast.LENGTH_SHORT).show();
            return;
        }

        Reminder newReminder = new Reminder(
                title,
                selectedDateTime.getTimeInMillis(),
                System.currentTimeMillis()
        );

        reminders.add(newReminder);
        adapter.notifyDataSetChanged();
        etReminderTitle.setText("");

        // Schedule notification
        scheduleNotification(newReminder);
    }

    private void scheduleNotification(Reminder reminder) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title", reminder.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) reminder.getCreatedAt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        String timeStr = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                .format(new Date(reminder.getTime()));
        Toast.makeText(this, "Alarm set for " + timeStr, Toast.LENGTH_SHORT).show();
    }

    private void cancelNotification(Reminder reminder) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) reminder.getCreatedAt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

        private List<Reminder> reminders;

        public ReminderAdapter(List<Reminder> reminders) {
            this.reminders = reminders;
        }

        @NonNull
        @Override
        public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reminder, parent, false);
            return new ReminderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
            Reminder reminder = reminders.get(position);
            holder.bind(reminder);
        }

        @Override
        public int getItemCount() {
            return reminders.size();
        }

        class ReminderViewHolder extends RecyclerView.ViewHolder {
            TextView tvReminderTitle, tvReminderTime, tvReminderDate;
            Button btnDelete;

            public ReminderViewHolder(@NonNull View itemView) {
                super(itemView);
                tvReminderTitle = itemView.findViewById(R.id.tvReminderTitle);
                tvReminderTime = itemView.findViewById(R.id.tvReminderTime);
                tvReminderDate = itemView.findViewById(R.id.tvReminderDate);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }

            public void bind(Reminder reminder) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

                tvReminderTitle.setText(reminder.getTitle());
                tvReminderTime.setText(timeFormat.format(reminder.getTime()));
                tvReminderDate.setText(dateFormat.format(reminder.getTime()));

                btnDelete.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Reminder toRemove = reminders.get(position);
                        reminders.remove(position);
                        notifyItemRemoved(position);
                        cancelNotification(toRemove);
                    }
                });
            }
        }
    }
}