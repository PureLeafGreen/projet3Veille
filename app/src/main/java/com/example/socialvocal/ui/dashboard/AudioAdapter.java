package com.example.socialvocal.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialvocal.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    private List<String> FileNameList;
    public AudioAdapter(List<String> fileNameList) {
        FileNameList = fileNameList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View AudioListView = inflater.inflate(R.layout.custom_layout_audio, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(AudioListView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AudioAdapter.ViewHolder holder, int position) {
        String nomAudio = FileNameList.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.textView;
        textView.setText(nomAudio);
    }

    @Override
    public int getItemCount() {
        return FileNameList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;
            public ViewHolder(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.AudioFile);
            }
        }
}
