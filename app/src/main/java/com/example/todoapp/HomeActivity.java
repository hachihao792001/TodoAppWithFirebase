package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {
    EditText taskEt;  //Et = edit text
    EditText descriptionEt;
    TextView dateTv; // Tv = text view

    //tạo DatePicker cho người dùng chọn ngày của công việc
    DateFormat fmtDate = DateFormat.getDateInstance();
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view,
                              int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    private ProgressDialog loader;

    //Danh sách loại task
    ArrayList<TaskType> taskTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Setup toolbar
        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Todo List");


        //Setup recyclerview lưu các task mà người dùng tạo
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);

        //Thực hiện các thao tác với firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);

        //Setup floating Button để người dùng tạo task mới
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(view -> addTask());

        //Danh sách các loại task mà pm hỗ trợ, mỗi loại kèm icon riêng
        String[] taskTypeNames = getResources().getStringArray(R.array.task_types);
        taskTypeList = new ArrayList<TaskType>(Arrays.asList(
                new TaskType(taskTypeNames[0], R.drawable.black_meeting_icon),
                new TaskType(taskTypeNames[1], R.drawable.black_shopping_icon),
                new TaskType(taskTypeNames[2], R.drawable.black_office_icon),
                new TaskType(taskTypeNames[3], R.drawable.black_contact_icon),
                new TaskType(taskTypeNames[4], R.drawable.black_travelling_icon),
                new TaskType(taskTypeNames[5], R.drawable.black_relaxing_icon)
        ));
    }

    //Hàm tạo thêm 1 task
    private void addTask() {
        AddTaskDialog addTaskDialog = new AddTaskDialog(this, onlineUserID, loader, recyclerView, taskTypeList);
        addTaskDialog.show();
    }


    private void updateLabel() {
        dateTv.setText(fmtDate.format(myCalendar.getTime()));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Setup việc lấy data từ firebase và hiển thị vào recyclerview

        FirebaseRecyclerOptions<TaskModel> options = new FirebaseRecyclerOptions.Builder<TaskModel>().setQuery(reference, TaskModel.class).build();
        FirebaseRecyclerAdapter<TaskModel, FirebaseViewHolder> adapter = new FirebaseRecyclerAdapter<TaskModel, FirebaseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final TaskModel model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDescription(model.getDescription());
                holder.setTaskType(model.getTaskType());
                holder.setIsDone(model.isDone());

                holder.mView.setOnClickListener(v -> {
                    //lấy task từ database để putExtra vào intent
                    reference.child(getRef(position).getKey()).get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Intent intent = null;
                            switch (model.getTaskType().name) {
                                case "Meeting":
                                    MeetingTask meetingTask = task.getResult().getValue(MeetingTask.class);
                                    intent = new Intent(HomeActivity.this, MeetingTaskDetail.class);
                                    intent.putExtra("task", meetingTask);
                                    break;
                                case "Shopping":
                                    ShoppingTask shoppingTask = task.getResult().getValue(ShoppingTask.class);
                                    intent = new Intent(HomeActivity.this, ShoppingTaskDetail.class);
                                    intent.putExtra("task", shoppingTask);
                                    break;
                                case "Office":
                                    OfficeTask officeTask = task.getResult().getValue(OfficeTask.class);
                                    intent = new Intent(HomeActivity.this, OfficeTaskDetail.class);
                                    intent.putExtra("task", officeTask);
                                    break;
                                case "Contact":
                                    ContactTask contactTask = task.getResult().getValue(ContactTask.class);
                                    intent = new Intent(HomeActivity.this, ContactTaskDetail.class);
                                    intent.putExtra("task", contactTask);
                                    break;
                                case "Travelling":
                                    TravellingTask travellingTask = task.getResult().getValue(TravellingTask.class);
                                    intent = new Intent(HomeActivity.this, TravellingTaskDetail.class);
                                    intent.putExtra("task", travellingTask);
                                    break;
                                case "Relaxing":
                                    RelaxingTask relaxingTask = task.getResult().getValue(RelaxingTask.class);
                                    intent = new Intent(HomeActivity.this, RelaxingTaskDetail.class);
                                    intent.putExtra("task", relaxingTask);
                                    break;
                            }

                            startActivity(intent);
                        }
                    });
                });


                Button editButton = holder.mView.findViewById(R.id.editButton);
                editButton.setOnClickListener(v -> {

                    //lấy data từ database để cho hàm updateTask dùng
                    reference.child(getRef(position).getKey()).get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            TaskModel taskToEdit = null;
                            switch (model.getTaskType().name) {
                                case "Meeting":
                                    taskToEdit = task.getResult().getValue(MeetingTask.class);
                                    break;
                                case "Shopping":
                                    taskToEdit = task.getResult().getValue(ShoppingTask.class);
                                    break;
                                case "Office":
                                    taskToEdit = task.getResult().getValue(OfficeTask.class);
                                    break;
                                case "Contact":
                                    taskToEdit = task.getResult().getValue(ContactTask.class);
                                    break;
                                case "Travelling":
                                    taskToEdit = task.getResult().getValue(TravellingTask.class);
                                    break;
                                case "Relaxing":
                                    taskToEdit = task.getResult().getValue(RelaxingTask.class);
                                    break;
                            }

                            updateTask(taskToEdit);
                        }
                    });
                });


                //Checkbox đánh dấu task đã hoàn thành hay chưa
                CheckBox doneCheckBox = holder.mView.findViewById(R.id.doneCheckBox);
                doneCheckBox.setOnClickListener(v -> {
                    boolean newDoneState = doneCheckBox.isChecked();

                    reference.child(getRef(position).getKey()).get().addOnCompleteListener(getDataTask -> {
                        if (!getDataTask.isSuccessful()) {
                            Log.e("firebase", "Error getting data", getDataTask.getException());
                        } else {
                            TaskModel receivedTask = null;

                            switch (model.getTaskType().name) {
                                case "Meeting":
                                    receivedTask = getDataTask.getResult().getValue(MeetingTask.class);
                                    break;
                                case "Shopping":
                                    receivedTask = getDataTask.getResult().getValue(ShoppingTask.class);
                                    break;
                                case "Office":
                                    receivedTask = getDataTask.getResult().getValue(OfficeTask.class);
                                    break;
                                case "Contact":
                                    receivedTask = getDataTask.getResult().getValue(ContactTask.class);
                                    break;
                                case "Travelling":
                                    receivedTask = getDataTask.getResult().getValue(TravellingTask.class);
                                    break;
                                case "Relaxing":
                                    receivedTask = getDataTask.getResult().getValue(RelaxingTask.class);
                                    break;
                            }

                            receivedTask.setDone(newDoneState);
                            reference.child(getRef(position).getKey()).setValue(receivedTask).addOnCompleteListener(t -> {
                                if (t.isSuccessful()) {
                                    Toast.makeText(HomeActivity.this,
                                            "Task " + model.getTask() + " " + (newDoneState ? "done" : "not done"),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, "Update task failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                });
            }


            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
                return new FirebaseViewHolder(v);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    //Hàm cập nhật task
    private void updateTask(TaskModel taskToUpdate) {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_data, null);
        myDialog.setView(view);
        AlertDialog dialog = myDialog.create();

        EditText mTask = view.findViewById(R.id.mEditTextTask);
        EditText mDescription = view.findViewById(R.id.mEditTextDescription);
        TextView mDate = view.findViewById(R.id.mEditDate);
        Button updateDateBtn = view.findViewById(R.id.pickUpdateDateBtn);
        mTask.setText(taskToUpdate.getTask());
        mTask.setSelection(taskToUpdate.getTask().length());
        mDescription.setText(taskToUpdate.getDescription());
        mDescription.setSelection(taskToUpdate.getDescription().length());

        mDate.setText(taskToUpdate.getDate());
        DateFormat fmtDate = DateFormat.getDateInstance();
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view,
                                  int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDate.setText(fmtDate.format(myCalendar.getTime()));
            }
        };

        updateDateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DatePickerDialog(HomeActivity.this, d,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button delButton = view.findViewById(R.id.btnDelete);
        Button updateButton = view.findViewById(R.id.btnUpdate);
        Spinner taskTypeDropdown = view.findViewById(R.id.taskTypeDropdown);
        //Cập nhật lại các thông tin người dùng nhập
        updateButton.setOnClickListener(view1 -> {
            String newTask = mTask.getText().toString().trim();
            String newDescription = mDescription.getText().toString().trim();
            TaskType newTaskType = (TaskType) taskTypeDropdown.getSelectedItem();
            String newDate = mDate.getText().toString().trim();

            TaskModel model = null;

            switch (newTaskType.name) {
                case "Meeting":
                    EditText etMeetingUrl = view.findViewById(R.id.et_meetingUrl);
                    EditText etMeetingLocation = view.findViewById(R.id.et_location);
                    model = new MeetingTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType,
                            etMeetingUrl.getText().toString().trim(),
                            etMeetingLocation.getText().toString().trim());
                    break;
                case "Shopping":
                    EditText etProductUrl = view.findViewById(R.id.et_productUrl);
                    EditText etShoppingLocation = view.findViewById(R.id.et_shoppingLocation);
                    model = new ShoppingTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType,
                            etProductUrl.getText().toString().trim(),
                            etShoppingLocation.getText().toString().trim());
                    break;
                case "Office":
                    model = new OfficeTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType);
                    break;
                case "Contact":
                    EditText etPhoneNumber = view.findViewById(R.id.et_phoneNumber);
                    EditText etEmail = view.findViewById(R.id.et_email);
                    model = new ContactTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType,
                            etPhoneNumber.getText().toString(), etEmail.getText().toString());
                    break;
                case "Travelling":
                    EditText etPlace = view.findViewById(R.id.et_place);
                    model = new TravellingTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType,
                            etPlace.getText().toString().trim());
                    break;
                case "Relaxing":
                    Spinner spinner = (Spinner) view.findViewById(R.id.chooseSongSpinner);
                    model = new RelaxingTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType,
                            spinner.getSelectedItem().toString());
                    break;
            }
            //update lại data của firebase
            reference.child(taskToUpdate.getId()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(HomeActivity.this, "Task has been updated", Toast.LENGTH_SHORT).show();
                    } else {
                        //String err = task.getException().toString();
                        Toast.makeText(HomeActivity.this, "Update task failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.dismiss();
        });

        //xóa task -> xóa data trong firebase
        delButton.setOnClickListener(view12 -> {
            reference.child(taskToUpdate.getId()).removeValue().addOnCompleteListener(removeTask -> {
                if (removeTask.isSuccessful()) {
                    Toast.makeText(HomeActivity.this, "Task has been deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    String err = removeTask.getException().toString();
                    Toast.makeText(HomeActivity.this, "Delete task failed!", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        dialog.show();

        taskTypeDropdown.setAdapter(new TaskTypeAdapter(this, taskTypeList));
        int updatingTaskIndex = 0;
        for (int i = 0; i < taskTypeList.size(); i++) {
            if (taskTypeList.get(i).name.equals(taskToUpdate.getTaskType().name)) {
                updatingTaskIndex = i;
                break;
            }
        }
        taskTypeDropdown.setSelection(updatingTaskIndex);
        taskTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                //do stuff
                RelativeLayout taskDetail = view.findViewById(R.id.taskDetail);
                LayoutInflater inflater = LayoutInflater.from(view.getContext());

                //thay doi taskDetail tuong ung voi moi type
                taskDetail.removeAllViews();
                String taskToUpdateName = taskToUpdate.getTaskType().name;
                switch (i) {
                    case 0: { //Meeting
                        View meetingInputDetail = inflater.inflate(R.layout.meeting_input_detail, null);
                        taskDetail.addView(meetingInputDetail);
                        if (taskToUpdateName.equals("Meeting")) {
                            EditText etMeetingUrl = view.findViewById(R.id.et_meetingUrl);
                            EditText etMeetingLocation = view.findViewById(R.id.et_location);

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
                        taskDetail.addView(shoppingInputDetail);
                        if (taskToUpdateName.equals("Shopping")) {
                            EditText etProductUrl = view.findViewById(R.id.et_productUrl);
                            EditText etShoppingLocation = view.findViewById(R.id.et_shoppingLocation);

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
                        taskDetail.addView(officeInputDetail);
                        break;
                    }
                    case 3: { //contact
                        View contactInputDetail = inflater.inflate(R.layout.contact_input_detail, null);
                        taskDetail.addView(contactInputDetail);

                        if (taskToUpdateName.equals("Contact")) {
                            EditText etPhoneNumber = view.findViewById(R.id.et_phoneNumber);
                            EditText etEmail = view.findViewById(R.id.et_email);

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
                        taskDetail.addView(travellingInputDetail);

                        if (taskToUpdateName.equals("Travelling")) {
                            EditText etPlace = view.findViewById(R.id.et_place);

                            TravellingTask travellingTaskToUpdate = (TravellingTask) taskToUpdate;
                            etPlace.setText(travellingTaskToUpdate.getPlace());
                            etPlace.setSelection(travellingTaskToUpdate.getPlace().length());
                        }
                        break;

                    }
                    case 5: {  //relax
                        View relaxingInputDetail = inflater.inflate(R.layout.relaxing_input_detail, null);
                        taskDetail.addView(relaxingInputDetail);

                        if (taskToUpdateName.equals("Relaxing")) {
                            Spinner chooseSongSpinner = relaxingInputDetail.findViewById(R.id.chooseSongSpinner);
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(HomeActivity.this,
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
                            String[] songNames = getResources().getStringArray(R.array.list_song);
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

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void chooseFile(View view) {
        String sPath = Environment.getExternalStorageDirectory() + "/";
        Uri uri = Uri.parse(sPath);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(uri, "*/*");
        startActivity(intent);
    }


}