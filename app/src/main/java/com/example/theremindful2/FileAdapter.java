package com.example.theremindful2;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
// First, create an Adapter class
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
    private List<String> fileList;
    private Context context;

    public FileAdapter(Context context) {
        this.context = context;
        this.fileList = new ArrayList<>();
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_main, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        String fileName = fileList.get(position);
        holder.fileNameText.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void addFile(String fileName) {
        fileList.add(fileName);
        notifyDataSetChanged();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameText;

        FileViewHolder(View itemView) {
            super(itemView);
            //fileNameText = itemView.findViewById(R.id.upload_button);
        }
    }
}