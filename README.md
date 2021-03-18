# AndroidFilePicker
This library will help to select file and folder in android

###### Step 1. Add it in your root build.gradle at the end of repositories:
```
allprojects {
   repositories {
     ...
     maven { url 'https://jitpack.io' }
    }
   }
]
```
 ###### Step 2. Add the dependency
 ```
dependencies {
        implementation 'com.github.droidhubworld:AndroidFilePicker:1.0.0'
}
```
###### Default Usage Of Dialog For Folder Pick
```
FolderPickerDialog pickerDialog = new FolderPickerDialog.Builder(this)
                .dialogWindowWidth(0.5f)
                .dialogWindowHeight(0.9f)
                .cancelable(true)
                .pickFiles(false)
                .setTitle("Select Folder")
                .callBack(new FolderPickerDialog.setOnFileOrFolderSelectListener() {
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
                })
                .build();
        pickerDialog.show(FolderPickerDialog.class.getName());
```
###### Default Usage Of Dialog For File Pick
```
FolderPickerDialog pickerDialog = new FolderPickerDialog.Builder(this)
                .dialogWindowWidth(0.5f)
                .dialogWindowHeight(0.9f)
                .cancelable(true)
                .pickFiles(true)
                .setTitle("Select File")
                .setLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .callBack(new FolderPickerDialog.setOnFileOrFolderSelectListener() {
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
                })
                .build();
        pickerDialog.show(FolderPickerDialog.class.getName());
```
###### Default Usage Of Activity For Folder Pick
```
        Intent intent = new Intent(this, FolderPicker.class);
        startActivityForResult(intent, FOLDER_PICKER_CODE);
```
###### Default Usage Of Activity For File Pick
```
        Intent intent = new Intent(this, FolderPicker.class);
        //Optional
        intent.putExtra("title", "Select file to upload");
        intent.putExtra("location", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        intent.putExtra("pickFiles", true);
        //Optional
        startActivityForResult(intent, FILE_PICKER_CODE);
```
###### onActivityResult
```
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
```
