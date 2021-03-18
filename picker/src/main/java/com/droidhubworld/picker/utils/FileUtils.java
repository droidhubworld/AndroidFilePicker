package com.droidhubworld.picker.utils;

public class FileUtils {
    private String name;
    private boolean folder;

    public FileUtils(String name, boolean folder) {
        this.name = name;
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public boolean isFolder() {
        return folder;
    }
}
