package com.droidhubworld.androidfilepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.droidhubworld.picker.FolderPicker;
import com.droidhubworld.picker.FolderPickerDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FolderPickerDialog.setOnFileOrFolderSelectListener {
    private static final int SDCARD_PERMISSION = 1,
            FOLDER_PICKER_CODE = 2,
            FILE_PICKER_CODE = 3;

    TextView tvFolder, tvFile,tvFileFromDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkStoragePermission();
        initUI();
    }


    void checkStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //Write permission is required so that folder picker can create new folder.
            //If you just want to pick files, Read permission is enough.

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SDCARD_PERMISSION);
            }
        }

    }

    void initUI() {
        findViewById(R.id.btn_select_folder).setOnClickListener(this);
        findViewById(R.id.btn_select_file).setOnClickListener(this);
        tvFolder = findViewById(R.id.tv_folder);
        tvFile = findViewById(R.id.tv_file);
        tvFileFromDialog = findViewById(R.id.tv_file_from_dialog);
        tvFolder.setText(getResources().getString(R.string.not_selected, "Folder"));
        tvFile.setText(getResources().getString(R.string.not_selected, "File"));
        tvFileFromDialog.setText(getResources().getString(R.string.not_selected, "File/Folder"));
    }

    public void openDialogFolderPick(View v) {
        FolderPickerDialog pickerDialog = new FolderPickerDialog.Builder(this)
                .dialogWindowWidth(0.5f)
                .dialogWindowHeight(0.9f)
                .cancelable(true)
                .pickFiles(false)
                .setTitle("Select Folder")
                .callBack(this)
                .build();
        pickerDialog.show(FolderPickerDialog.class.getName());
    }

    public void openDialogFilePick(View v) {
        FolderPickerDialog pickerDialog = new FolderPickerDialog.Builder(this)
                .dialogWindowWidth(0.5f)
                .dialogWindowHeight(0.9f)
                .cancelable(true)
                .pickFiles(true)
                .setTitle("Select File")
                .setLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .callBack(this)
                .build();
        pickerDialog.show(FolderPickerDialog.class.getName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_folder:
                pickFolder();
                break;
            case R.id.btn_select_file:
                pickFile();
                break;
        }
    }

    void pickFolder() {
        Intent intent = new Intent(this, FolderPicker.class);
        startActivityForResult(intent, FOLDER_PICKER_CODE);
    }

    void pickFile() {
        Intent intent = new Intent(this, FolderPicker.class);

        //Optional
        intent.putExtra("title", "Select file to upload");
        intent.putExtra("location", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        intent.putExtra("pickFiles", true);
        //Optional

        startActivityForResult(intent, FILE_PICKER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FOLDER_PICKER_CODE) {

            if (resultCode == Activity.RESULT_OK && intent.hasExtra("data")) {
                String folderLocation = "<b>Selected Folder: </b>" + intent.getExtras().getString("data");
                tvFolder.setText(Html.fromHtml(folderLocation));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                tvFolder.setText(getResources().getString(R.string.pick_cancelled, "Folder"));
            }

        } else if (requestCode == FILE_PICKER_CODE) {
            if (resultCode == Activity.RESULT_OK && intent.hasExtra("data")) {
                String fileLocation = "<b>Selected File: </b>" + intent.getExtras().getString("data");
                tvFile.setText(Html.fromHtml(fileLocation));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                tvFile.setText(getResources().getString(R.string.pick_cancelled, "File"));
            }
        }
    }

    @Override
    public void onFileSelected(boolean isFile, String location) {
        if (location == null) {
            tvFileFromDialog.setText(getResources().getString(R.string.pick_cancelled, isFile ? "File" : "Folder"));
            return;
        }
        if (isFile) {
            String fileLocation = "<b>Selected File: </b>" + location;
            tvFileFromDialog.setText(Html.fromHtml(fileLocation));
        } else {
            String folderLocation = "<b>Selected Folder: </b>" + location;
            tvFileFromDialog.setText(Html.fromHtml(folderLocation));
        }
    }
}