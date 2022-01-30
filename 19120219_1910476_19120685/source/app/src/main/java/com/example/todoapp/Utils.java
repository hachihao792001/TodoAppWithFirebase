package com.example.todoapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class Utils {
    private static int sTheme = R.style.AppTheme_Blue;

    public final static int THEME_RED = 0;
    public final static int THEME_ORANGE = 1;
    public final static int THEME_YELLOW = 2;
    public final static int THEME_GREEN = 3;
    public final static int THEME_BLUE = 4;
    public final static int THEME_PURPLE = 5;
    public final static int THEME_PINK = 6;
    public final static int THEME_GRAY = 7;

    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (sTheme) {
            case THEME_RED:
                activity.setTheme(R.style.AppTheme_Red);
                break;
            case THEME_ORANGE:
                activity.setTheme(R.style.AppTheme_Orange);
                break;
            case THEME_YELLOW:
                activity.setTheme(R.style.AppTheme_Yellow);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.AppTheme_Green);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.AppTheme_Blue);
                break;
            case THEME_PINK:
                activity.setTheme(R.style.AppTheme_Pink);
                break;
            case THEME_PURPLE:
                activity.setTheme(R.style.AppTheme_Purple);
                break;
            case THEME_GRAY:
                activity.setTheme(R.style.AppTheme_Gray);
                break;
        }
    }

    public interface UploadImageToStorageListener {
        void onDoneUploading();
    }

    public static void uploadImageToStorage(String userId, String taskId, Bitmap bitmap, UploadImageToStorageListener listener) {
        //https://stackoverflow.com/a/4989543/13440955

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();

        FirebaseStorage.getInstance().getReference().child(userId + "/" + taskId + ".png").putBytes(byteArray).
                addOnCompleteListener(task -> listener.onDoneUploading());
    }

    interface CheckFileExistOnStorageListener {
        void onDoneChecking(boolean exist);
    }

    public static void checkFileExistOnStorage(String path, CheckFileExistOnStorageListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        storageRef.child(path).getDownloadUrl()
                .addOnSuccessListener(uri -> listener.onDoneChecking(true))
                .addOnFailureListener(exception -> listener.onDoneChecking(false));
    }

    public interface DownloadImageFromStorageListener {
        void onDownloadDone(Bitmap bitmap);
    }

    public static void downloadImageFromStorage(String userId, String taskId, DownloadImageFromStorageListener listener) {
        String path = userId + "/" + taskId + ".png";

        checkFileExistOnStorage(path, (exist) -> {
            if (exist) {
                FirebaseStorage.getInstance().getReference().child(path).getBytes(50 * 1024 * 1024).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        byte[] byteArr = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
                        listener.onDownloadDone(bitmap);
                    }
                });
            } else {
                Log.e("firebase", "File " + path + " doesn't exist");
                listener.onDownloadDone(null);
            }
        });
    }

    public static void deleteImageFromStorage(String userId, String taskId) {
        FirebaseStorage.getInstance().getReference().child(userId + "/" + taskId + ".png").delete();
    }

    public interface GetTaskFromDatabaseListener {
        void onGetTaskSuccessfully(TaskModel task);
    }

    public static void getTaskFromDatabase(String onlineUserID, String taskId, GetTaskFromDatabaseListener listener) {
        FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID).child(taskId).get().addOnCompleteListener(tResult -> {
            if (!tResult.isSuccessful()) {
                Log.e("firebase", "Error getting data", tResult.getException());
            } else {
                TaskModel taskModel = tResult.getResult().getValue(TaskModel.class);
                switch (taskModel.getTaskType().name) {
                    case "Meeting":
                        taskModel = tResult.getResult().getValue(MeetingTask.class);
                        break;
                    case "Shopping":
                        taskModel = tResult.getResult().getValue(ShoppingTask.class);
                        break;
                    case "Office":
                        taskModel = tResult.getResult().getValue(OfficeTask.class);
                        break;
                    case "Contact":
                        taskModel = tResult.getResult().getValue(ContactTask.class);
                        break;
                    case "Travelling":
                        taskModel = tResult.getResult().getValue(TravellingTask.class);
                        break;
                    case "Relaxing":
                        taskModel = tResult.getResult().getValue(RelaxingTask.class);
                        break;
                }

                listener.onGetTaskSuccessfully(taskModel);
            }
        });
    }

    public interface UpdateTaskToDatabaseListener {
        void onFinishUpdateTask(boolean success);
    }

    public static void updateTaskToDatabase(String onlineUserID, TaskModel taskModel, UpdateTaskToDatabaseListener listener) {
        FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID).child(taskModel.getId()).setValue(taskModel).addOnCompleteListener(t -> {
            listener.onFinishUpdateTask(t.isSuccessful());
        });
    }

    public interface DeleteTaskFromDatabaseListener {
        void onFinishDeleteTask(boolean success);
    }

    public static void deleteTaskFromDatabase(String onlineUserID, String taskId, DeleteTaskFromDatabaseListener listener) {
        FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID).child(taskId).removeValue().addOnCompleteListener(t -> {
            listener.onFinishDeleteTask(t.isSuccessful());
        });
    }

    public static Class<?> getDetailClassFromTaskType(String taskTypeName) {
        Class<?> detailClass = MeetingTaskDetail.class;
        switch (taskTypeName) {
            case "Meeting":
                detailClass = MeetingTaskDetail.class;
                break;
            case "Shopping":
                detailClass = ShoppingTaskDetail.class;
                break;
            case "Office":
                detailClass = OfficeTaskDetail.class;
                break;
            case "Contact":
                detailClass = ContactTaskDetail.class;
                break;
            case "Travelling":
                detailClass = TravellingTaskDetail.class;
                break;
            case "Relaxing":
                detailClass = RelaxingTaskDetail.class;
                break;
        }

        return detailClass;
    }
}