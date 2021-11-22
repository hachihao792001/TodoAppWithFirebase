package com.example.todoapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class UpdateTaskDialog extends AlertDialog {
    Context context;
    DatabaseReference reference;
    ProgressDialog loader;
    ArrayList<TaskType> taskTypeList;

    View dialogView;
    TaskModel taskToUpdate;

    EditText etTask;
    EditText etDescription;
    TextView mDate;
    Spinner taskTypeDropdown;

    protected UpdateTaskDialog(Context context, String onlineUserID, TaskModel taskToUpdate, ArrayList<TaskType> taskTypeList) {
        super(context);
        this.context = context;
        this.taskToUpdate = taskToUpdate;
        this.reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);
        this.loader = new ProgressDialog(context);
        this.taskTypeList = taskTypeList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.update_data, null);
        setView(dialogView);

        etTask = dialogView.findViewById(R.id.mEditTextTask);
        etDescription = dialogView.findViewById(R.id.mEditTextDescription);
        mDate = dialogView.findViewById(R.id.mEditDate);
        Button updateDateBtn = dialogView.findViewById(R.id.pickUpdateDateBtn);

        etTask.setText(taskToUpdate.getTask());
        etTask.setSelection(taskToUpdate.getTask().length());

        etDescription.setText(taskToUpdate.getDescription());
        etDescription.setSelection(taskToUpdate.getDescription().length());

        mDate.setText(taskToUpdate.getDate());
        DateFormat fmtDate = DateFormat.getDateInstance();
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDate.setText(fmtDate.format(myCalendar.getTime()));
            }
        };
        updateDateBtn.setOnClickListener(v ->
                new DatePickerDialog(context, d,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show());


        Button delButton = dialogView.findViewById(R.id.btnDelete);
        Button updateButton = dialogView.findViewById(R.id.btnUpdate);
        taskTypeDropdown = dialogView.findViewById(R.id.taskTypeDropdown);

        //Cập nhật lại các thông tin người dùng nhập
        updateButton.setOnClickListener(view1 -> {
            updateButtonOnClick();
        });

        //xóa task -> xóa data trong firebase
        delButton.setOnClickListener(view12 -> {
            reference.child(taskToUpdate.getId()).removeValue().addOnCompleteListener(removeTask -> {
                if (removeTask.isSuccessful()) {
                    Toast.makeText(context, "Task has been deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    String err = removeTask.getException().toString();
                    Toast.makeText(context, "Delete task failed!", Toast.LENGTH_SHORT).show();
                }
            });

            dismiss();
        });


        taskTypeDropdown.setAdapter(new TaskTypeAdapter(context, taskTypeList));
        for (int i = 0; i < taskTypeList.size(); i++) {
            if (taskTypeList.get(i).name.equals(taskToUpdate.getTaskType().name)) {
                taskTypeDropdown.setSelection(i);
                break;
            }
        }
        taskTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                updateTaskDetailFragment(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        super.onCreate(savedInstanceState);
    }

    void updateButtonOnClick() {
        String newTask = etTask.getText().toString().trim();
        String newDescription = etDescription.getText().toString().trim();
        TaskType newTaskType = (TaskType) taskTypeDropdown.getSelectedItem();
        String newDate = mDate.getText().toString().trim();

        if (TextUtils.isEmpty(newTask)) {
            etTask.setError("Task is required!");
            return;
        } else if (TextUtils.isEmpty(newDescription)) {
            etDescription.setError("Description is required!");
            return;
        }

        loader.setMessage("Updating your task...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        TaskModel model = null;

        switch (newTaskType.name) {
            case "Meeting":
                EditText etMeetingUrl = dialogView.findViewById(R.id.et_meetingUrl);
                EditText etMeetingLocation = dialogView.findViewById(R.id.et_location);
                model = new MeetingTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType,
                        etMeetingUrl.getText().toString().trim(),
                        etMeetingLocation.getText().toString().trim());
                break;
            case "Shopping":
                EditText etProductUrl = dialogView.findViewById(R.id.et_productUrl);
                EditText etShoppingLocation = dialogView.findViewById(R.id.et_shoppingLocation);
                model = new ShoppingTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType,
                        etProductUrl.getText().toString().trim(),
                        etShoppingLocation.getText().toString().trim());
                break;
            case "Office":
                model = new OfficeTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType);
                break;
            case "Contact":
                EditText etPhoneNumber = dialogView.findViewById(R.id.et_phoneNumber);
                EditText etEmail = dialogView.findViewById(R.id.et_email);
                model = new ContactTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType,
                        etPhoneNumber.getText().toString(), etEmail.getText().toString());
                break;
            case "Travelling":
                EditText etPlace = dialogView.findViewById(R.id.et_place);
                model = new TravellingTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType,
                        etPlace.getText().toString().trim());
                break;
            case "Relaxing":
                Spinner spinner = (Spinner) dialogView.findViewById(R.id.chooseSongSpinner);
                model = new RelaxingTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType,
                        spinner.getSelectedItem().toString());
                break;
        }
        //update lại data của firebase
        reference.child(taskToUpdate.getId()).setValue(model).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Task has been updated", Toast.LENGTH_SHORT).show();
                loader.dismiss();
            } else {
                //String err = task.getException().toString();
                Toast.makeText(context, "Update task failed!", Toast.LENGTH_SHORT).show();
                loader.dismiss();
            }
        });

        dismiss();
    }

    void updateTaskDetailFragment(int chosenTask) {
        RelativeLayout taskDetailFragment = dialogView.findViewById(R.id.taskDetail);
        LayoutInflater inflater = LayoutInflater.from(dialogView.getContext());

        //thay doi taskDetail tuong ung voi moi type
        taskDetailFragment.removeAllViews();
        String taskToUpdateName = taskToUpdate.getTaskType().name;
        switch (chosenTask) {
            case 0: { //Meeting
                View meetingInputDetail = inflater.inflate(R.layout.meeting_input_detail, null);
                taskDetailFragment.addView(meetingInputDetail);
                if (taskToUpdateName.equals("Meeting")) {
                    EditText etMeetingUrl = dialogView.findViewById(R.id.et_meetingUrl);
                    EditText etMeetingLocation = dialogView.findViewById(R.id.et_location);

                    MeetingTask meetingTaskToUpdate = (MeetingTask) taskToUpdate;
                    etMeetingUrl.setText(meetingTaskToUpdate.getMeetingUrl());
                    etMeetingUrl.setSelection(meetingTaskToUpdate.getMeetingUrl().length());
                    etMeetingLocation.setText(meetingTaskToUpdate.getMeetingLocation());
                    etMeetingLocation.setSelection(meetingTaskToUpdate.getMeetingLocation().length());
                }
                break;
            }
            case 1: { //Shopping
                View shoppingInputDetail = inflater.inflate(R.layout.shopping_input_detail, null);
                taskDetailFragment.addView(shoppingInputDetail);
                if (taskToUpdateName.equals("Shopping")) {
                    EditText etProductUrl = dialogView.findViewById(R.id.et_productUrl);
                    EditText etShoppingLocation = dialogView.findViewById(R.id.et_shoppingLocation);

                    ShoppingTask shoppingTaskToUpdate = (ShoppingTask) taskToUpdate;
                    etProductUrl.setText(shoppingTaskToUpdate.getProductUrl());
                    etProductUrl.setSelection(shoppingTaskToUpdate.getProductUrl().length());
                    etShoppingLocation.setText(shoppingTaskToUpdate.getShoppingLocation());
                    etShoppingLocation.setSelection(shoppingTaskToUpdate.getShoppingLocation().length());
                }
                break;
            }
            case 2: { //office
                View officeInputDetail = inflater.inflate(R.layout.office_input_detail, null);
                taskDetailFragment.addView(officeInputDetail);
                break;
            }
            case 3: { //contact
                View contactInputDetail = inflater.inflate(R.layout.contact_input_detail, null);
                taskDetailFragment.addView(contactInputDetail);

                if (taskToUpdateName.equals("Contact")) {
                    EditText etPhoneNumber = dialogView.findViewById(R.id.et_phoneNumber);
                    EditText etEmail = dialogView.findViewById(R.id.et_email);

                    ContactTask contactTaskToUpdate = (ContactTask) taskToUpdate;
                    etPhoneNumber.setText(contactTaskToUpdate.getPhoneNumber());
                    etPhoneNumber.setSelection(contactTaskToUpdate.getPhoneNumber().length());
                    etEmail.setText(contactTaskToUpdate.getEmail());
                    etEmail.setSelection(contactTaskToUpdate.getEmail().length());
                }
                break;

            }
            case 4: { //travel
                View travellingInputDetail = inflater.inflate(R.layout.travelling_input_detail, null);
                taskDetailFragment.addView(travellingInputDetail);

                if (taskToUpdateName.equals("Travelling")) {
                    EditText etPlace = dialogView.findViewById(R.id.et_place);

                    TravellingTask travellingTaskToUpdate = (TravellingTask) taskToUpdate;
                    etPlace.setText(travellingTaskToUpdate.getPlace());
                    etPlace.setSelection(travellingTaskToUpdate.getPlace().length());
                }
                break;

            }
            case 5: {  //relax
                View relaxingInputDetail = inflater.inflate(R.layout.relaxing_input_detail, null);
                taskDetailFragment.addView(relaxingInputDetail);

                if (taskToUpdateName.equals("Relaxing")) {
                    Spinner chooseSongSpinner = relaxingInputDetail.findViewById(R.id.chooseSongSpinner);
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                            R.array.list_song, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chooseSongSpinner.setAdapter(adapter);

                    chooseSongSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            String playlistName = adapterView.getItemAtPosition(i).toString();
                            Toast.makeText(adapterView.getContext(), playlistName, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                    RelaxingTask relaxingTaskToUpdate = (RelaxingTask) taskToUpdate;
                    String[] songNames = context.getResources().getStringArray(R.array.list_song);
                    for (int s = 0; s < songNames.length; s++) {
                        if (relaxingTaskToUpdate.getPlaylistName().equals(songNames[s])) {
                            chooseSongSpinner.setSelection(s);
                            break;
                        }
                    }
                }
                break;

            }
        }
    }
}
