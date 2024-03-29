package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

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
    private FloatingActionButton addTaskWithImageButton;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    private Boolean isAddTaskWithImg = false;
    private Boolean isAddTaskWithOCR = false;
    private ProgressDialog loader;

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
        addTaskFromCameraButton.setOnClickListener(view -> addTaskWithOCR());

        //Tạo task với ảnh
        addTaskWithImageButton = findViewById(R.id.btn_add_task_with_image);
        addTaskWithImageButton.setOnClickListener(view -> addTaskWithImage());

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

    ItemTouchHelper.SimpleCallback simpleCallBack = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @RequiresApi(api = Build.VERSION_CODES.N)

        List<Integer> order = new ArrayList<Integer>();

        void swapItemAdapter() {
            if (order.size() > 0) {
                int from = order.get(0);
                int to = order.get(1);
                TaskModel temp = adapter.getItem(from);
                adapter.getRef(from).setValue(adapter.getItem(to)).addOnCompleteListener(yesTo -> {
                    if (yesTo.isSuccessful()) {
                        adapter.getRef(to).setValue(temp).addOnCompleteListener(yesFrom -> {
                            if (yesFrom.isSuccessful()) {
                                if (order.isEmpty()) {
                                    loader.dismiss();
                                    //recyclerView.setAdapter(adapter);
                                    return;
                                }
                                else {
                                    order.remove(0);
                                    order.remove(0);
                                    swapItemAdapter();
                                }
                            } else {
                                Toast.makeText(HomeActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                                return;
                            }
                        });
                    } else {
                        Toast.makeText(HomeActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        loader.dismiss();
                        return;
                    }
                });
            }
            else {
                loader.dismiss();
                return;
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            loader = new ProgressDialog(HomeActivity.this);
            loader.setMessage("Updating...");
            loader.setCanceledOnTouchOutside(false);
            loader.show();
            swapItemAdapter();
        }

        @Override
        public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            order.add(fromPos);
            order.add(toPos);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            adapter.getRef(viewHolder.getAdapterPosition()).removeValue().addOnCompleteListener(task->{
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Task has been deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            //String err = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "Delete task failed", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    };

    //Hàm tạo thêm 1 task
    private void addTask() {
        AddTaskDialog addTaskDialog = new AddTaskDialog(this, onlineUserID, taskTypeList, "");
        addTaskDialog.show();
        addTaskDialog.setOnDismissListener(dialogInterface -> {
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,
                    null, recyclerView.getLayoutManager().getItemCount());

            adapter.notifyDataSetChanged();
        });
    }

    //Hàm tạo thêm 1 task bằng việc scan chữ viết từ 1 image
    private void addTaskWithOCR() {
        isAddTaskWithOCR = true;
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        } else {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(HomeActivity.this);
        }
    }

    // Hàm tạo task với 1 image
    private void addTaskWithImage() {
        isAddTaskWithImg = true;
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
                holder.setImage(onlineUserID, model.getId());

                //bấm vào background của task thì chuyển tới màn hình detail của task
                holder.mView.setOnClickListener(v -> {

                    //lấy task từ database để putExtra vào intent
                    Utils.getTaskFromDatabase(onlineUserID, model.getId(), (taskModel) -> {
                        Intent intent = new Intent(HomeActivity.this, Utils.getDetailClassFromTaskType(
                                model.getTaskType().name));
                        intent.putExtra("userId", onlineUserID);
                        intent.putExtra("task", taskModel);

                        startActivity(intent);
                    });
                });

                //bấm cây bút thì lấy task từ database về và cho hàm updateTask xử lí (hiện UpdateTaskDialog)
                Button editButton = holder.mView.findViewById(R.id.editButton);
                editButton.setOnClickListener(v -> {
                    //lấy data từ database để cho hàm updateTask dùng
                    Utils.getTaskFromDatabase(onlineUserID, model.getId(), (taskModel) -> {
                        updateTask(taskModel);
                    });
                });


                //Checkbox đánh dấu task đã hoàn thành hay chưa
                CheckBox doneCheckBox = holder.mView.findViewById(R.id.doneCheckBox);
                doneCheckBox.setOnClickListener(v -> {
                    boolean newDoneState = doneCheckBox.isChecked();

                    //lấy task từ database về, cập nhật isDone cho nó, rồi set lên database lại
                    Utils.getTaskFromDatabase(onlineUserID, model.getId(), (taskModel) -> {
                        taskModel.setDone(newDoneState);
                        Utils.updateTaskToDatabase(onlineUserID, taskModel, (success) -> {
                            Toast.makeText(HomeActivity.this,
                                    success ?
                                            "Task " + model.getTask() + " " + (newDoneState ? "done" : "not done") :
                                            "Update task failed", Toast.LENGTH_SHORT).show();

                            if (newDoneState) {
                                MediaPlayer mediaPlayer = MediaPlayer.create(HomeActivity.this, R.raw.ding);
                                mediaPlayer.start();
                            }
                        });
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


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && isAddTaskWithOCR) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    isAddTaskWithOCR = false;
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
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE & isAddTaskWithImg) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    isAddTaskWithImg = false;
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    AddTaskDialog addTaskDialog = new AddTaskDialog(this, onlineUserID, taskTypeList, "", bitmap);
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
            Toast.makeText(HomeActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
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