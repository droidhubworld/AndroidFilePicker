package com.droidhubworld.picker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.droidhubworld.picker.adapter.FolderAdapter;
import com.droidhubworld.picker.utils.FileUtils;
import com.droidhubworld.picker.utils.PickerConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FolderPicker extends AppCompatActivity {
    //Folders and Files have separate lists
    ArrayList<FileUtils> folderAndFileList;
    ArrayList<FileUtils> foldersList;
    ArrayList<FileUtils> filesList;

    TextView tvTitle;
    TextView tvLocation;

    String location = Environment.getExternalStorageDirectory().getAbsolutePath();
    boolean pickFiles;
    Intent receivedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_picker);

        if (!isExternalStorageReadable()) {
            Toast.makeText(this, "Storage access permission not given", Toast.LENGTH_LONG).show();
            finish();
        }

        tvTitle = findViewById(R.id.tv_title);
        tvLocation = findViewById(R.id.tv_location);

        try {
            receivedIntent = getIntent();

            if (receivedIntent.hasExtra(PickerConstant.TITLE)) {
                String receivedTitle = receivedIntent.getExtras().getString(PickerConstant.TITLE);
                if (receivedTitle != null) {
                    tvTitle.setText(receivedTitle);
                }
            }

            if (receivedIntent.hasExtra(PickerConstant.LOCATION)) {
                String reqLocation = receivedIntent.getExtras().getString(PickerConstant.LOCATION);
                if (reqLocation != null) {
                    File requestedFolder = new File(reqLocation);
                    if (requestedFolder.exists())
                        location = reqLocation;
                }
            }

            if (receivedIntent.hasExtra(PickerConstant.PICK_FILES)) {
                pickFiles = receivedIntent.getExtras().getBoolean(PickerConstant.PICK_FILES);
                if (pickFiles) {
                    findViewById(R.id.fp_btn_select).setVisibility(View.GONE);
                    findViewById(R.id.fp_btn_new).setVisibility(View.GONE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        loadFilesList(location);
    }

    private void loadFilesList(String location) {
        try {

            File folder = new File(location);

            if (!folder.isDirectory())
                exit();

            tvLocation.setText("Location : " + folder.getAbsolutePath());
            File[] files = folder.listFiles();

            foldersList = new ArrayList<>();
            filesList = new ArrayList<>();

            for (File currentFile : files) {
                if (currentFile.isDirectory()) {
                    FileUtils fileUtils = new FileUtils(currentFile.getName(), true);
                    foldersList.add(fileUtils);
                } else {
                    FileUtils fileUtils = new FileUtils(currentFile.getName(), false);
                    filesList.add(fileUtils);
                }
            }

            // sort & add to final List - as we show folders first add folders first to the final list
            Collections.sort(foldersList, comparatorAscending);
            folderAndFileList = new ArrayList<>();
            folderAndFileList.addAll(foldersList);

            //if we have to show files, then add files also to the final list
            if (pickFiles) {
                Collections.sort(filesList, comparatorAscending);
                folderAndFileList.addAll(filesList);
            }

            showList();

        } catch (Exception e) {
            e.printStackTrace();
        }

    } // load Files List

    /* Checks if external storage is available to at least read */
    boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    Comparator<FileUtils> comparatorAscending = new Comparator<FileUtils>() {
        @Override
        public int compare(FileUtils f1, FileUtils f2) {
            return f1.getName().compareTo(f2.getName());
        }
    };

    void showList() {

        try {
            FolderAdapter FolderAdapter = new FolderAdapter(this, folderAndFileList);
            ListView listView = findViewById(R.id.listView);
            listView.setAdapter(FolderAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    listClick(position);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void listClick(int position) {

        if (pickFiles && !folderAndFileList.get(position).isFolder()) {
            String data = location + File.separator + folderAndFileList.get(position).getName();
            receivedIntent.putExtra("data", data);
            setResult(RESULT_OK, receivedIntent);
            finish();
        } else {
            location = location + File.separator + folderAndFileList.get(position).getName();
            loadFilesList(location);
        }

    }

    public void goBack(View v) {

        if (location != null && !location.equals("") && !location.equals("/")) {
            int start = location.lastIndexOf('/');
            String newLocation = location.substring(0, start);
            location = newLocation;
            loadFilesList(location);
        } else {
            exit();
        }

    }

    void exit() {
        setResult(RESULT_CANCELED, receivedIntent);
        finish();
    }

    void createNewFolder(String filename) {
        try {

            File file = new File(location + File.separator + filename);
            file.mkdirs();
            loadFilesList(location);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error:" + e.toString(), Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void newFolderDialog(View v) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Enter Folder Name");

        EditText et = new EditText(this);
        dialog.setView(et);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Create",
                (arg0, arg1) -> createNewFolder(et.getText().toString()));
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                (arg0, arg1) -> {

                });

        dialog.show();

    }

    public void select(View v) {
        if (pickFiles) {
            Toast.makeText(this, "You have to select a file", Toast.LENGTH_LONG).show();
        } else if (receivedIntent != null) {
            receivedIntent.putExtra("data", location);
            setResult(RESULT_OK, receivedIntent);
            finish();
        }
    }


    public void cancel(View v) {
        exit();
    }

    @Override
    public void onBackPressed() {
        goBack(null);
    }
}