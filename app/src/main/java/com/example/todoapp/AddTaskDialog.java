package com.example.todoapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddTaskDialog extends AlertDialog {

    Context context;
    DatabaseReference reference;
    ProgressDialog loader;
    ArrayList<TaskType> taskTypeList;
    String description;

    View dialogView;
    Spinner taskTypeDropdown;

    //tạo DatePicker cho người dùng chọn ngày của công việc
    DateFormat fmtDate = DateFormat.getDateInstance();
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view,
                              int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateTv.setText(fmtDate.format(myCalendar.getTime()));
        }
    };

    EditText taskEt;
    EditText descriptionEt;
    TextView dateTv;

    protected AddTaskDialog(Context context, String onlineUserID, ArrayList<TaskType> taskTypes, String description) {
        super(context);
        this.context = context;
        this.reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);
        this.loader = new ProgressDialog(context);
        this.taskTypeList = taskTypes;
        this.description = description;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.add_task, null);
        setView(dialogView);
        setCancelable(false);

        taskEt = dialogView.findViewById(R.id.task);
        descriptionEt = dialogView.findViewById(R.id.description);
        dateTv = dialogView.findViewById(R.id.date);
        Button pickDate = dialogView.findViewById(R.id.pickDateBtn);
        Button drawImage = dialogView.findViewById(R.id.drawImage);
        Button saveButton = dialogView.findViewById(R.id.saveBtn);
        Button cancel = dialogView.findViewById(R.id.cancelBtn);
        taskTypeDropdown = dialogView.findViewById(R.id.taskTypeDropdown);

        if (description.length() > 0) {
            descriptionEt.setText(description);
            taskEt.setText("My task");
        }

        // Cho phép người dùng chọn ngày
        pickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DatePickerDialog(context, d,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // kết quả chọn ngày được cập nhật ở dateTextview
        dateTv.setText(fmtDate.format(myCalendar.getTime()));

        // bấm vẽ hình để đi tới activity vẽ hình
        drawImage.setOnClickListener(v -> drawImageOnClick());

        //Nhấn nút Cancel để tắt dialog tạo task
        cancel.setOnClickListener(v -> dismiss());

        //Nhấn nút Save, kiểm tra và lưu vào database
        saveButton.setOnClickListener(v -> saveButtonOnClick());

        //Set up Dropdown cho phần chọn loại task
        taskTypeDropdown.setAdapter(new TaskTypeAdapter(context, taskTypeList));
        taskTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateTaskDetailFragment(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        super.onCreate(savedInstanceState);
    }

    void drawImageOnClick() {
        DrawImageDialog drawImageDialog = new DrawImageDialog(context);
        drawImageDialog.show();
    }

    void saveButtonOnClick() {
        String newTaskName = taskEt.getText().toString().trim();
        String newTaskDescription = descriptionEt.getText().toString().trim();
        String newTaskID = reference.push().getKey();
        String newTaskDate = dateTv.getText().toString().trim();
        TaskType newTaskTaskType = (TaskType) taskTypeDropdown.getSelectedItem();

        //Kiểm tra các thông tin task người dùng nhập hợp lệ khôgn
        if (TextUtils.isEmpty(newTaskName)) {
            taskEt.setError("Task is required!");
            return;
        } else if (TextUtils.isEmpty(newTaskDescription)) {
            descriptionEt.setError("Description is required!");
            return;
        }

        loader.setMessage("Adding your task...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        //Tùy vào từng loại task người dùng chọn tạo ra các model tương ứng
        TaskModel model = null;
        switch (newTaskTaskType.name) {
            case "Meeting":
                EditText etMeetingUrl = dialogView.findViewById(R.id.et_meetingUrl);
                EditText etMeetingLocation = dialogView.findViewById(R.id.et_location);
                String meetingUrl = etMeetingUrl.getText().toString().trim();
                String meetingLocation = etMeetingLocation.getText().toString().trim();
                model = new MeetingTask(newTaskName, newTaskDescription, newTaskID, newTaskDate, newTaskTaskType,
                        meetingUrl, meetingLocation);
                break;
            case "Shopping":
                EditText etProductUrl = dialogView.findViewById(R.id.et_productUrl);
                EditText etShoppingLocation = dialogView.findViewById(R.id.et_shoppingLocation);
                String productUrl = etProductUrl.getText().toString().trim();
                String shoppingLocation = etShoppingLocation.getText().toString().trim();
                model = new ShoppingTask(newTaskName, newTaskDescription, newTaskID, newTaskDate, newTaskTaskType,
                        productUrl, shoppingLocation);
                break;
            case "Office":
                model = new OfficeTask(newTaskName, newTaskDescription, newTaskID, newTaskDate, newTaskTaskType);
                break;
            case "Contact":
                EditText phoneET = dialogView.findViewById(R.id.et_phoneNumber);
                EditText emailET = dialogView.findViewById(R.id.et_email);
                model = new ContactTask(newTaskName, newTaskDescription, newTaskID, newTaskDate, newTaskTaskType,
                        phoneET.getText().toString(), emailET.getText().toString());
                break;
            case "Travelling":
                EditText etPlace = dialogView.findViewById(R.id.et_place);
                model = new TravellingTask(newTaskName, newTaskDescription, newTaskID, newTaskDate, newTaskTaskType,
                        etPlace.getText().toString().trim());
                break;
            case "Relaxing":
                Spinner chooseSongSpinner = dialogView.findViewById(R.id.chooseSongSpinner);
                model = new RelaxingTask(newTaskName, newTaskDescription, newTaskID, newTaskDate, newTaskTaskType,
                        chooseSongSpinner.getSelectedItem().toString().trim());
                break;
        }

        //Thông báo kết quả tạo task thành công/ thất bại
        reference.child(newTaskID).setValue(model).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(context, "Task has been added successfully!", Toast.LENGTH_SHORT).show();
                loader.dismiss();
            } else {
                //String error = task.getException().toString();
                Toast.makeText(context, "Add task failed! Please try again!", Toast.LENGTH_SHORT).show();
                loader.dismiss();
            }
        });

        dismiss();
    }

    void updateTaskDetailFragment(int chosenTask) {
        //do stuff
        RelativeLayout taskDetailFragment = dialogView.findViewById(R.id.taskDetail);
        LayoutInflater inflater = LayoutInflater.from(dialogView.getContext());

        //Task detail sẽ thay đổi tương ứng với mỗi loại task người dùng chọn
        taskDetailFragment.removeAllViews();

        switch (chosenTask) {
            case 0: { //Meeting
                View meetingInputDetail = inflater.inflate(R.layout.meeting_input_detail, null);
                taskDetailFragment.addView(meetingInputDetail);
                break;
            }
            case 1: { //Shopping
                View shoppingInputDetail = inflater.inflate(R.layout.shopping_input_detail, null);
                taskDetailFragment.addView(shoppingInputDetail);
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
                break;

            }
            case 4: { //travel
                View travellingInputDetail = inflater.inflate(R.layout.travelling_input_detail, null);
                taskDetailFragment.addView(travellingInputDetail);
                break;

            }
            case 5: {  //relax
                View relaxingInputDetail = inflater.inflate(R.layout.relaxing_input_detail, null);
                Spinner spinner = (Spinner) relaxingInputDetail.findViewById(R.id.chooseSongSpinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                        R.array.list_song, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String playlistName = adapterView.getItemAtPosition(i).toString();
                        Toast.makeText(adapterView.getContext(), playlistName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                taskDetailFragment.addView(relaxingInputDetail);
                break;
            }
        }
    }
}
