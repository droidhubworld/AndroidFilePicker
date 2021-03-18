package com.droidhubworld.picker;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.droidhubworld.picker.adapter.FolderAdapter;
import com.droidhubworld.picker.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class FolderPickerDialog extends BaseDialog implements View.OnClickListener {
    //Folders and Files have separate lists
    ArrayList<FileUtils> folderAndFileList;
    ArrayList<FileUtils> foldersList;
    ArrayList<FileUtils> filesList;

    TextView tvTitle;
    TextView tvLocation;

    String location = Environment.getExternalStorageDirectory().getAbsolutePath();
    private View rootView;
    private Context mContext;
    private int mStyle = -1;
    private boolean mCancelable = true;
    private float mDialogWindowWidth = 0;
    private float mDialogWindowHeight = 0;
    private String title;
    private String reqLocation;
    private boolean pickFiles = false;

    private setOnFileOrFolderSelectListener mCallBack;

    public interface setOnFileOrFolderSelectListener {
        void onFileSelected(boolean isFile, String location);
    }

    public FolderPickerDialog(Context mContext, int mStyle, boolean mCancelable, float mDialogWindowWidth, float mDialogWindowHeight, String title, String reqLocation, boolean pickFiles, setOnFileOrFolderSelectListener mCallBack) {
        this.mContext = mContext;
        this.mStyle = mStyle;
        this.mCancelable = mCancelable;
        this.mDialogWindowWidth = mDialogWindowWidth;
        this.mDialogWindowHeight = mDialogWindowHeight;
        this.title = title;
        this.reqLocation = reqLocation;
        this.pickFiles = pickFiles;
        this.mCallBack = mCallBack;
    }

    public static class Builder {
        private Context mContext;
        private int mStyle = -1;
        private boolean mCancelable = true;
        private float mDialogWindowWidth = 0;
        private float mDialogWindowHeight = 0;
        private String title;
        private String reqLocation;
        private boolean pickFiles = false;
        private setOnFileOrFolderSelectListener mCallBack;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder style(int mStyle) {
            this.mStyle = mStyle;
            return this;
        }

        public Builder cancelable(boolean mCancelable) {
            this.mCancelable = mCancelable;
            return this;
        }

        public Builder dialogWindowWidth(float dialogWindowWidth) {
            this.mDialogWindowWidth = dialogWindowWidth;
            return this;
        }

        public Builder dialogWindowHeight(float dialogWindowHeight) {
            this.mDialogWindowHeight = dialogWindowHeight;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setLocation(String location) {
            this.reqLocation = location;
            return this;
        }

        public Builder pickFiles(boolean pickFiles) {
            this.pickFiles = pickFiles;
            return this;
        }

        public Builder callBack(setOnFileOrFolderSelectListener mCallBack) {
            this.mCallBack = mCallBack;
            return this;
        }

        public FolderPickerDialog build() {
            return new FolderPickerDialog(this.mContext, this.mStyle, this.mCancelable, this.mDialogWindowWidth, this.mDialogWindowHeight, this.title, this.reqLocation, this.pickFiles, this.mCallBack);
        }
    }

    public void show(String TAG) {
        show(((AppCompatActivity) mContext).getSupportFragmentManager(), TAG);
    }

    public void hide() {
        this.dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public int getLayoutId() {
        return mStyle;
    }

    @Override
    public boolean isCancelable() {
        return mCancelable;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_folder_picker, container, false);
        this.rootView = view;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.tv_title);
        tvLocation = view.findViewById(R.id.tv_location);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!isExternalStorageReadable()) {
            Toast.makeText(mContext, "Storage access permission not given", Toast.LENGTH_LONG).show();
            dismiss();
        }

        rootView.findViewById(R.id.btn_go_back).setOnClickListener(this);
        rootView.findViewById(R.id.btn_new).setOnClickListener(this);
        rootView.findViewById(R.id.btn_select).setOnClickListener(this);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(this);

        if (title != null) {
            tvTitle.setText(title);
        }

        if (reqLocation != null) {
            File requestedFolder = new File(reqLocation);
            if (requestedFolder.exists())
                location = reqLocation;
        }

        if (pickFiles) {
            rootView.findViewById(R.id.btn_select).setVisibility(View.GONE);
            rootView.findViewById(R.id.btn_new).setVisibility(View.GONE);
        }
        loadFilesList(location);
    }

    @Override
    public void onResume() {
        super.onResume();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());

        if (mDialogWindowWidth != 0) {
            layoutParams.width = (int) (displayWidth * mDialogWindowWidth);
        }
        if (mDialogWindowHeight != 0) {
            layoutParams.height = (int) (displayHeight * mDialogWindowHeight);
        }

        // Apply the newly created layout parameters to the alert dialog window
        getDialog().getWindow().setAttributes(layoutParams);


    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
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
            FolderAdapter FolderAdapter = new FolderAdapter(getActivity(), folderAndFileList);
            ListView listView = rootView.findViewById(R.id.listView);
            listView.setAdapter(FolderAdapter);

            listView.setOnItemClickListener((parent, view, position, id) -> listClick(position));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void listClick(int position) {

        if (pickFiles && !folderAndFileList.get(position).isFolder()) {
            String data = location + File.separator + folderAndFileList.get(position).getName();
            if (mCallBack != null) {
                mCallBack.onFileSelected(pickFiles, data);
            }
            dismiss();
        } else {
            location = location + File.separator + folderAndFileList.get(position).getName();
            loadFilesList(location);
        }

    }

    public void select() {
        if (pickFiles) {
            Toast.makeText(getContext(), "You have to select a file", Toast.LENGTH_LONG).show();
        } else if (mCallBack != null) {
            mCallBack.onFileSelected(pickFiles, location);
            dismiss();
        }
    }

    public void goBack() {

        if (location != null && !location.equals("") && !location.equals("/")) {
            int start = location.lastIndexOf('/');
            String newLocation = location.substring(0, start);
            location = newLocation;
            loadFilesList(location);
        } else {
            exit();
        }

    }

    void createNewFolder(String filename) {
        try {

            File file = new File(location + File.separator + filename);
            file.mkdirs();
            loadFilesList(location);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Error:" + e.toString(), Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void newFolderDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setTitle("Enter Folder Name");

        EditText et = new EditText(mContext);
        dialog.setView(et);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Create",
                (arg0, arg1) -> createNewFolder(et.getText().toString()));
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                (arg0, arg1) -> {

                });

        dialog.show();

    }

    void exit() {
        if (mCallBack != null) {
            mCallBack.onFileSelected(pickFiles, location);
        }
        dismiss();
    }

    public void cancel() {
        dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_go_back) {
            goBack();
        }
        if (v.getId() == R.id.btn_new) {
            newFolderDialog();
        }
        if (v.getId() == R.id.btn_select) {
            select();
        }
        if (v.getId() == R.id.btn_cancel) {
            cancel();
            dismiss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
