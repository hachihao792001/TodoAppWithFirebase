package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity {
    TextView dateTv; // Tv = text view


    //tạo DatePicker cho người dùng chọn ngày của công việc
    DateFormat fmtDate = DateFormat.getDateInstance();
    Calendar myCalendar = Calendar.getInstance();

    Bitmap bitmap;
    private static final int REQUEST_CAMERA_CODE = 100;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FloatingActionButton addTaskFromCameraButton;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    SharedPreferences sharedPreferences;
    Constant constant;
    int appTheme;
    int themeColor;
    int appColor;
    //    int idTheme=3;
    //Danh sách loại task
    ArrayList<TaskType> taskTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // lấy theme user đã chọn trong THEME.txt để set up
        SharedPreferences sharedPreferences = getSharedPreferences("THEME.txt", Context.MODE_PRIVATE);
        int idtheme = sharedPreferences.getInt("theme", -1);
        Constant.theme = Constant.convert(idtheme);
        if (idtheme == -1)
            setTheme(Constant.convert(5));
        else
            setTheme(Constant.theme);


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

        //Thực hiện các thao tác với firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);


        //Setup floating Button để người dùng tạo task mới
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(view -> addTask());

        //Nút tạo một task từ ảnh
        addTaskFromCameraButton = findViewById(R.id.btn_add_task_from_camera);
        addTaskFromCameraButton.setOnClickListener(view -> addTaskFromImage());


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

        //gắn drag & drop vào Recycler View
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallBack = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            //Cập nhật array tasks mới lên database
            //...

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //do nothing
        }
    };

    //Hàm tạo thêm 1 task
    private void addTask() {
        AddTaskDialog addTaskDialog = new AddTaskDialog(this, onlineUserID, taskTypeList, "");
        addTaskDialog.show();
        addTaskDialog.setOnDismissListener(dialogInterface -> {
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,
                    null, recyclerView.getLayoutManager().getItemCount());
        });
    }

    //Hàm tạo thêm 1 task từ image
    private void addTaskFromImage() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        } else {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(HomeActivity.this);
        }
    }

    //Hàm cập nhật task
    private void updateTask(TaskModel taskToUpdate) {
        UpdateTaskDialog updateTaskDialog = new UpdateTaskDialog(this, onlineUserID, taskToUpdate, taskTypeList);
        updateTaskDialog.show();
        updateTaskDialog.setOnDismissListener(dialogInterface -> {
            adapter.notifyDataSetChanged();
        });
    }

    FirebaseRecyclerAdapter<TaskModel, FirebaseViewHolder> adapter;

    @Override
    protected void onStart() {
        super.onStart();

        //Setup việc lấy data từ firebase và hiển thị vào recyclerview

        FirebaseRecyclerOptions<TaskModel> options = new FirebaseRecyclerOptions.Builder<TaskModel>().setQuery(reference, TaskModel.class).build();
        adapter = new FirebaseRecyclerAdapter<TaskModel, FirebaseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final TaskModel model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDescription(model.getDescription());
                holder.setTaskType(model.getTaskType());
                holder.setIsDone(model.isDone());

                // lấy hình từ storage, để vô taskImage nếu có
                ImageView taskImage = holder.mView.findViewById(R.id.taskImage);
                Utils.downloadImageFromStorage(onlineUserID, model.getId(), bitmap1 -> taskImage.setImageBitmap(bitmap1));

                //bấm vào background của task thì chuyển tới màn hình detail của task
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
                                    intent.putExtra("userId", onlineUserID);
                                    intent.putExtra("task", meetingTask);
                                    break;
                                case "Shopping":
                                    ShoppingTask shoppingTask = task.getResult().getValue(ShoppingTask.class);
                                    intent = new Intent(HomeActivity.this, ShoppingTaskDetail.class);
                                    intent.putExtra("userId", onlineUserID);
                                    intent.putExtra("task", shoppingTask);
                                    break;
                                case "Office":
                                    OfficeTask officeTask = task.getResult().getValue(OfficeTask.class);
                                    intent = new Intent(HomeActivity.this, OfficeTaskDetail.class);
                                    intent.putExtra("userId", onlineUserID);
                                    intent.putExtra("task", officeTask);
                                    break;
                                case "Contact":
                                    ContactTask contactTask = task.getResult().getValue(ContactTask.class);
                                    intent = new Intent(HomeActivity.this, ContactTaskDetail.class);
                                    intent.putExtra("userId", onlineUserID);
                                    intent.putExtra("task", contactTask);
                                    break;
                                case "Travelling":
                                    TravellingTask travellingTask = task.getResult().getValue(TravellingTask.class);
                                    intent = new Intent(HomeActivity.this, TravellingTaskDetail.class);
                                    intent.putExtra("userId", onlineUserID);
                                    intent.putExtra("task", travellingTask);
                                    break;
                                case "Relaxing":
                                    RelaxingTask relaxingTask = task.getResult().getValue(RelaxingTask.class);
                                    intent = new Intent(HomeActivity.this, RelaxingTaskDetail.class);
                                    intent.putExtra("userId", onlineUserID);
                                    intent.putExtra("task", relaxingTask);
                                    break;
                            }

                            startActivity(intent);
                        }
                    });
                });

                //bấm cây bút thì lấy task từ database về và cho hàm updateTask xử lí (hiện UpdateTaskDialog)
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

                    //lấy task từ database về, cập nhật isDone cho nó, rồi set lên database lại
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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.firebase_recycler_item, parent, false);
                return new FirebaseViewHolder(v);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
                break;
            case R.id.changeTheme:
                Intent themeIntent = new Intent(HomeActivity.this, ChangeTheme.class);
                startActivity(themeIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    String description = getTextFromImage(bitmap);
                    AddTaskDialog addTaskDialog = new AddTaskDialog(this, onlineUserID, taskTypeList, description);
                    addTaskDialog.show();
                    addTaskDialog.setOnDismissListener(dialogInterface -> {
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,
                                null, recyclerView.getLayoutManager().getItemCount());
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getTextFromImage(Bitmap bitmap) {
        String result = "";
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(HomeActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }

            result = stringBuilder.toString();
        }
        return result;
    }
}