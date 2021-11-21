package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    FragmentManager fragmentManager = this.getSupportFragmentManager();


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

    private String key = "";
    private String task;
    private String description;
    private String date;
    private TaskType taskType;

    //các biến cụ thể để lưu cho từng task detail :(
    //meeting
    private String gMeetingUrl = "";
    private String gMeetingLocation = "";
    //shopping
    private String gProductUrl = "";
    private String gShoppingLocation = "";
    //office
    //contact
    private String gPhoneNumber = "";
    private String gEmail = "";
    //Travelling
    private String gTravellingPlace = "";
    //relaxing

    ArrayList<TaskType> taskTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Todo List");

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

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

    private void addTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.input_file, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);
        dialog.show();

        taskEt = myView.findViewById(R.id.task);
        descriptionEt = myView.findViewById(R.id.description);
        dateTv = myView.findViewById(R.id.date);
        Button pickDate = myView.findViewById(R.id.pickDateBtn);
        Button save = myView.findViewById(R.id.saveBtn);
        Button cancel = myView.findViewById(R.id.cancelBtn);
        Spinner taskTypeDropdown = myView.findViewById(R.id.taskTypeDropdown);


        pickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DatePickerDialog(HomeActivity.this, d,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // cập nhật giá trị tại textview day
        updateLabel();

        cancel.setOnClickListener(v -> dialog.dismiss());

        save.setOnClickListener(v -> {
            String mTask = taskEt.getText().toString().trim();
            String mDescription = descriptionEt.getText().toString().trim();
            String id = reference.push().getKey();
            String mDate = dateTv.getText().toString().trim();
            TaskType taskType = (TaskType) taskTypeDropdown.getSelectedItem();

            if (TextUtils.isEmpty(mTask)) {
                taskEt.setError("Task is required!");
                return;
            } else if (TextUtils.isEmpty(mDescription)) {
                taskEt.setError("Description is required!");
                return;
            } else {
                loader.setMessage("Adding your task...");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                TaskModel model = null;
                switch (taskType.name) {
                    case "Meeting":
                        EditText etMeetingUrl = myView.findViewById(R.id.et_meetingUrl);
                        EditText etMeetingLocation = myView.findViewById(R.id.et_location);
                        String meetingUrl = etMeetingUrl.getText().toString().trim();
                        String meetingLocation = etMeetingLocation.getText().toString().trim();
                        model = new MeetingTask(mTask, mDescription, id, mDate, taskType,
                                meetingUrl, meetingLocation);
                        break;
                    case "Shopping":
                        EditText etProductUrl = myView.findViewById(R.id.et_productUrl);
                        EditText etShoppingLocation = myView.findViewById(R.id.et_shoppingLocation);
                        String productUrl = etProductUrl.getText().toString().trim();
                        String shoppingLocation = etShoppingLocation.getText().toString().trim();
                        model = new ShoppingTask(mTask, mDescription, id, mDate, taskType,
                                productUrl, shoppingLocation);
                        break;
                    case "Office":
                        model = new OfficeTask(mTask, mDescription, id, mDate, taskType,
                                "lấy filename từ edit text");
                        break;
                    case "Contact":
                        EditText phoneET = myView.findViewById(R.id.et_phoneNumber);
                        EditText emailET = myView.findViewById(R.id.et_email);
                        model = new ContactTask(mTask, mDescription, id, mDate, taskType,
                                phoneET.getText().toString(), emailET.getText().toString());
                        break;
                    case "Travelling":
                        EditText etPlace = myView.findViewById(R.id.et_place);
                        String place = etPlace.getText().toString().trim();
                        model = new TravellingTask(mTask, mDescription, id, mDate, taskType, place);
                        break;
                    case "Relaxing":
                        EditText etPlaylistName = myView.findViewById(R.id.playlistName);
                        String playlistName = etPlaylistName.getText().toString().trim();
                        model = new RelaxingTask(mTask, mDescription, id, mDate, taskType,
                                playlistName);
                        break;
                }
                reference.child(id).setValue(model).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(HomeActivity.this, "Task has been added successfully!", Toast.LENGTH_SHORT).show();
                        loader.dismiss();
                    } else {
                        //String error = task.getException().toString();
                        Toast.makeText(HomeActivity.this, "Add task failed! Please try again!", Toast.LENGTH_SHORT).show();
                        loader.dismiss();
                    }

                });
            }

            dialog.dismiss();
            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
        });

        taskTypeDropdown.setAdapter(new TaskTypeAdapter(this, taskTypeList));
        taskTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //do stuff
                RelativeLayout taskDetail = myView.findViewById(R.id.taskDetail);
                LayoutInflater inflater = LayoutInflater.from(myView.getContext());

                //thay doi taskDetail tuong ung voi moi type
                taskDetail.removeAllViews();
                switch (i) {
                    case 0: { //Meeting
                        View meetingInputDetail = inflater.inflate(R.layout.meeting_input_detail, null);
                        taskDetail.addView(meetingInputDetail);
                        break;
                    }
                    case 1: { //Shopping
                        View shoppingInputDetail = inflater.inflate(R.layout.shopping_input_detail, null);
                        taskDetail.addView(shoppingInputDetail);
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
                        break;

                    }
                    case 4: { //travel
                        View travellingInputDetail = inflater.inflate(R.layout.travelling_input_detail, null);
                        taskDetail.addView(travellingInputDetail);
                        break;

                    }
                    case 5: {  //relax
                        View relaxingInputDetail = inflater.inflate(R.layout.relaxing_input_detail, null);
                        taskDetail.addView(relaxingInputDetail);
                        break;

                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialog.show();
    }


    private void updateLabel() {
        dateTv.setText(fmtDate.format(myCalendar.getTime()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<TaskModel> options = new FirebaseRecyclerOptions.Builder<TaskModel>().setQuery(reference, TaskModel.class).build();

        FirebaseRecyclerAdapter<TaskModel, MyViewHolder> adapter = new FirebaseRecyclerAdapter<TaskModel, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final TaskModel model) {
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
                    key = getRef(position).getKey();
                    task = model.getTask();
                    description = model.getDescription();
                    taskType = model.getTaskType();
                    date = model.getDate();

                    //lấy data từ database để để vào những "global variable" tuỳ theo từng kiểu taskType cho hàm updateTask xài
                    reference.child(getRef(position).getKey()).get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            switch (model.getTaskType().name) {
                                case "Meeting":
                                    MeetingTask meetingTask = task.getResult().getValue(MeetingTask.class);
                                    gMeetingLocation = meetingTask.getMeetingLocation();
                                    gMeetingUrl = meetingTask.getMeetingUrl();
                                    break;
                                case "Shopping":
                                    ShoppingTask shoppingTask = task.getResult().getValue(ShoppingTask.class);
                                    gProductUrl = shoppingTask.getProductUrl();
                                    gShoppingLocation = shoppingTask.getShoppingLocation();
                                    break;
                                case "Office":
                                    OfficeTask officeTask = task.getResult().getValue(OfficeTask.class);
                                    break;
                                case "Contact":
                                    ContactTask contactTask = task.getResult().getValue(ContactTask.class);
                                    gPhoneNumber = contactTask.getPhoneNumber();
                                    gEmail = contactTask.getEmail();
                                    break;
                                case "Travelling":
                                    TravellingTask travellingTask = task.getResult().getValue(TravellingTask.class);
                                    gTravellingPlace = travellingTask.getPlace();
                                    break;
                                case "Relaxing":
                                    RelaxingTask relaxingTask = task.getResult().getValue(RelaxingTask.class);
                                    break;
                            }
                        }
                    });

                    updateTask();
                });

                CheckBox doneCheckBox = holder.mView.findViewById(R.id.doneCheckBox);
                doneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        reference.child(getRef(position).getKey()).get().addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                TaskModel receivedTask = null;

                                switch (model.getTaskType().name) {
                                    case "Meeting":
                                        receivedTask = task.getResult().getValue(MeetingTask.class);
                                        break;
                                    case "Shopping":
                                        receivedTask = task.getResult().getValue(ShoppingTask.class);
                                        break;
                                    case "Office":
                                        receivedTask = task.getResult().getValue(OfficeTask.class);
                                        break;
                                    case "Contact":
                                        receivedTask = task.getResult().getValue(ContactTask.class);
                                        break;
                                    case "Travelling":
                                        receivedTask = task.getResult().getValue(TravellingTask.class);
                                        break;
                                    case "Relaxing":
                                        receivedTask = task.getResult().getValue(RelaxingTask.class);
                                        break;
                                }

                                receivedTask.setDone(b);
                                reference.child(getRef(position).getKey()).setValue(receivedTask).addOnCompleteListener(t -> {
                                    if (t.isSuccessful()) {
                                        Toast.makeText(HomeActivity.this,
                                                "Task " + (b ? "done" : "not done"),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HomeActivity.this, "Update task failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
                return new MyViewHolder(v);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTask(String task) {
            TextView taskTextView = mView.findViewById(R.id.taskTv);
            taskTextView.setText(task);
        }

        public void setDescription(String des) {
            TextView desTextView = mView.findViewById(R.id.descriptionTv);
            desTextView.setText(des);
        }

        public void setDate(String date) {
            TextView dateTextView = mView.findViewById(R.id.dateTv);
            dateTextView.setText(date);
        }

        public void setTaskType(TaskType taskType) {
            ImageView icon = mView.findViewById(R.id.taskTypeIcon);
            icon.setImageResource(taskType.iconResource);
        }

        public void setIsDone(boolean done) {
            CheckBox doneCheckBox = mView.findViewById(R.id.doneCheckBox);
            if (doneCheckBox.isChecked() != done)
                doneCheckBox.setChecked(done);
        }
    }

    private void updateTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_data, null);
        myDialog.setView(view);

        AlertDialog dialog = myDialog.create();

        EditText mTask = view.findViewById(R.id.mEditTextTask);
        EditText mDescription = view.findViewById(R.id.mEditTextDescription);
        TextView mDate = view.findViewById(R.id.mEditDate);
        Button updateDateBtn = view.findViewById(R.id.pickUpdateDateBtn);

        mTask.setText(task);
        mTask.setSelection(task.length());

        mDescription.setText(description);
        mDescription.setSelection(description.length());

        mDate.setText(date);

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

        updateButton.setOnClickListener(view1 -> {
            task = mTask.getText().toString().trim();
            description = mDescription.getText().toString().trim();
            taskType = (TaskType) taskTypeDropdown.getSelectedItem();
            String date = mDate.getText().toString().trim();

            TaskModel model = new TaskModel(task, description, key, date, taskType);

            switch (taskType.name) {
                case "Meeting":
                    EditText etMeetingUrl = view.findViewById(R.id.et_meetingUrl);
                    EditText etMeetingLocation = view.findViewById(R.id.et_location);
                    String meetingUrl = etMeetingUrl.getText().toString().trim();
                    String meetingLocation = etMeetingLocation.getText().toString().trim();
                    model = new MeetingTask(task, description, key, date, taskType, meetingUrl, meetingLocation);
                    break;
                case "Shopping":
                    EditText etProductUrl = view.findViewById(R.id.et_productUrl);
                    EditText etShoppingLocation = view.findViewById(R.id.et_shoppingLocation);
                    String productUrl = etProductUrl.getText().toString().trim();
                    String shoppingLocation = etShoppingLocation.getText().toString().trim();
                    model = new ShoppingTask(task, description, key, date, taskType, productUrl, shoppingLocation);
                    break;
                case "Office":

                    break;
                case "Contact":
                    EditText etPhoneNumber = view.findViewById(R.id.et_phoneNumber);
                    EditText etEmail = view.findViewById(R.id.et_email);
                    model = new ContactTask(task, description, key, date, taskType,
                            etPhoneNumber.getText().toString(), etEmail.getText().toString());
                    break;
                case "Travelling":
                    EditText etPlace = view.findViewById(R.id.et_place);
                    String place = etPlace.getText().toString().trim();
                    model = new TravellingTask(task, description, key, date, taskType, place);
                    break;
                case "Relaxing":
                    break;
            }

            reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Task has been deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String err = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "Delete task failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();

        taskTypeDropdown.setAdapter(new TaskTypeAdapter(this, taskTypeList));
        int updatingTaskIndex = 0;
        for (int i = 0; i < taskTypeList.size(); i++) {
            if (taskTypeList.get(i).name.equals(taskType.name)) {
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
                switch (i) {
                    case 0: { //Meeting
                        View meetingInputDetail = inflater.inflate(R.layout.meeting_input_detail, null);
                        taskDetail.addView(meetingInputDetail);

                        if (taskType.name.equals("Meeting")) {
                            EditText etMeetingUrl = view.findViewById(R.id.et_meetingUrl);
                            EditText etMeetingLocation = view.findViewById(R.id.et_location);

                            etMeetingUrl.setText(gMeetingUrl);
                            etMeetingUrl.setSelection(gMeetingUrl.length());
                            etMeetingLocation.setText(gMeetingLocation);
                            etMeetingLocation.setSelection(gMeetingLocation.length());
                        }
                        break;
                    }
                    case 1: { //Shopping
                        View shoppingInputDetail = inflater.inflate(R.layout.shopping_input_detail, null);
                        taskDetail.addView(shoppingInputDetail);

                        if (taskType.name.equals("Shopping")) {
                            EditText etProductUrl = view.findViewById(R.id.et_productUrl);
                            EditText etShoppingLocation = view.findViewById(R.id.et_shoppingLocation);

                            etProductUrl.setText(gProductUrl);
                            etProductUrl.setSelection(gProductUrl.length());
                            etShoppingLocation.setText(gShoppingLocation);
                            etShoppingLocation.setSelection(gShoppingLocation.length());
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

                        if (taskType.name.equals("Contact")) {
                            EditText etPhoneNumber = view.findViewById(R.id.et_phoneNumber);
                            EditText etEmail = view.findViewById(R.id.et_email);

                            etPhoneNumber.setText(gPhoneNumber);
                            etPhoneNumber.setSelection(gPhoneNumber.length());
                            etEmail.setText(gEmail);
                            etEmail.setSelection(gEmail.length());
                        }
                        break;

                    }
                    case 4: { //travel
                        View travellingInputDetail = inflater.inflate(R.layout.travelling_input_detail, null);
                        taskDetail.addView(travellingInputDetail);

                        if (taskType.name.equals("Travelling")) {
                            EditText etPlace = view.findViewById(R.id.et_place);

                            etPlace.setText(gTravellingPlace);
                            etPlace.setSelection(gTravellingPlace.length());
                        }
                        break;

                    }
                    case 5: {  //relax
                        View relaxingInputDetail = inflater.inflate(R.layout.relaxing_input_detail, null);
                        taskDetail.addView(relaxingInputDetail);
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