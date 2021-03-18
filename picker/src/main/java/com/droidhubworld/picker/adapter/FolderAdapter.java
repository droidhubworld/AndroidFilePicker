package com.droidhubworld.picker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidhubworld.picker.R;
import com.droidhubworld.picker.utils.FileUtils;

import java.util.ArrayList;

public class FolderAdapter extends ArrayAdapter<FileUtils> {
    Activity mContext;
    ArrayList<FileUtils> mDataList;

    public FolderAdapter(Activity mContext, ArrayList<FileUtils> mDataList) {

        super(mContext, R.layout.file_list_row, mDataList);
        this.mContext = mContext;
        this.mDataList = mDataList;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        convertView = inflater.inflate(R.layout.file_list_row, parent, false);

        ImageView imageView = convertView.findViewById(R.id.iv_icon);
        TextView name = convertView.findViewById(R.id.tv_name);

        if (mDataList.get(position).isFolder()) {
            imageView.setImageResource(R.drawable.ic_folder);
        } else {
            imageView.setImageResource(R.drawable.ic_file);
        }

        name.setText(mDataList.get(position).getName());

        return convertView;
    }
}
