package com.example.todoapp;

import android.app.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class UpdateTaskDialog extends AlertDialog {
    Context context;
    String onlineUserID;

    ProgressDialog loader;
    ArrayList<TaskType> taskTypeList;

    View dialogView;
    TaskModel taskToUpdate;

    EditText etTask;
    EditText etDescription;
    TextView mDate;
    Spinner taskTypeDropdown;

    Button clearImage;
    ImageView taskImage;

    protected UpdateTaskDialog(Context context, String onlineUserID, TaskModel taskToUpdate, ArrayList<TaskType> taskTypeList) {
        super(context);
        this.context = context;
        this.taskToUpdate = taskToUpdate;
        this.onlineUserID = onlineUserID;
        this.loader = new ProgressDialog(context);
        this.taskTypeList = taskTypeList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(context);

        dialogView = inflater.inflate(R.layout.update_task, null);
        setView(dialogView);

        etTask = dialogView.findViewById(R.id.mEditTextTask);
        etDescription = dialogView.findViewById(R.id.mEditTextDescription);
        mDate = dialogView.findViewById(R.id.mEditDate);
        Button updateDateBtn = dialogView.findViewById(R.id.pickUpdateDateBtn);

        Button drawImage = dialogView.findViewById(R.id.drawImage);
        clearImage = dialogView.findViewById(R.id.removeImage);
        taskImage = dialogView.findViewById(R.id.taskImage);

        etTask.setText(taskToUpdate.getTask());
        etTask.setSelection(taskToUpdate.getTask().length());

        etDescription.setText(taskToUpdate.getDescription());
        etDescription.setSelection(taskToUpdate.getDescription().length());

        mDate.setText(taskToUpdate.getDate());
        DateFormat fmtDate = DateFormat.getDateInstance();
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mDate.setText(fmtDate.format(myCalendar.getTime()));
        };
        updateDateBtn.setOnClickListener(v ->
                new DatePickerDialog(context, d,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show());


        Button delButton = dialogView.findViewById(R.id.btnDelete);
        Button updateButton = dialogView.findViewById(R.id.btnUpdate);
        taskTypeDropdown = dialogView.findViewById(R.id.taskTypeDropdown);

        // bấm vẽ hình để đi tới activity vẽ hình
        drawImage.setOnClickListener(v -> drawImageOnClick());
        // bỏ hình
        clearImage.setOnClickListener(v -> {
            taskImage.setImageBitmap(null);
            clearImage.setEnabled(false);
        });

        taskImage.setImageBitmap(null);
        // lấy hình từ storage, nếu có thì bỏ vô taskImage
        Utils.downloadImageFromStorage(onlineUserID, taskToUpdate.getId(), bitmap -> {
            if (bitmap != null) {
                taskImage.setImageBitmap(bitmap);
                clearImage.setEnabled(true);
            } else {
                taskImage.setImageBitmap(null);
                clearImage.setEnabled(false);
            }
        });

        //Cập nhật lại các thông tin người dùng nhập
        updateButton.setOnClickListener(view1 -> updateButtonOnClick());

        //xóa task -> xóa data trong firebase
        delButton.setOnClickListener(view12 -> {

            Utils.deleteImageFromStorage(onlineUserID, taskToUpdate.getId());
            Utils.deleteTaskFromDatabase(onlineUserID, taskToUpdate.getId(), (success) -> {
                Toast.makeText(context, success ? "Task has been deleted successfully" : "Delete task failed", Toast.LENGTH_SHORT).show();
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

    private void drawImageOnClick() {

        FragmentManager fm = ((AppCompatActivity) context).getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DrawImageDialog drawImageDialog = new DrawImageDialog();
        drawImageDialog.setOnFinishDrawingListener(bitmap -> {
            taskImage.setImageBitmap(bitmap);
            clearImage.setEnabled(true);
        });
        drawImageDialog.show(ft, "dialog");
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

        final TaskModel model;
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
                Spinner spinner = dialogView.findViewById(R.id.chooseSongSpinner);
                model = new RelaxingTask(newTask, newDescription, taskToUpdate.getId(), newDate, newTaskType,
                        spinner.getSelectedItem().toString());
                break;
            default:
                model = null;
        }

        Bitmap bmp = ((BitmapDrawable) taskImage.getDrawable()).getBitmap();
        if (bmp != null) {
            // phải upload hình lên xong mới cập nhật database
            // vì cập nhật database sẽ khiến recycler view cập nhật ngay, và lúc đó hình chưa upload lên kịp nên sẽ bị lấy lại hình cũ
            Utils.uploadImageToStorage(onlineUserID, taskToUpdate.getId(), bmp, () -> {
                //update lại data của firebase
                Utils.updateTaskToDatabase(onlineUserID, model, (success) -> {
                    Toast.makeText(context, success ? "Task has been updated" : "Update task failed", Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                });
                dismiss();
            });
        } else {
            Utils.deleteImageFromStorage(onlineUserID, taskToUpdate.getId());
            //update lại data của firebase
            Utils.updateTaskToDatabase(onlineUserID, model, (success) -> {
                Toast.makeText(context, success ? "Task has been updated" : "Update task failed", Toast.LENGTH_SHORT).show();
                loader.dismiss();
            });
            dismiss();
        }
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

                if (taskToUpdateName.equals("Relaxing")) {
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
