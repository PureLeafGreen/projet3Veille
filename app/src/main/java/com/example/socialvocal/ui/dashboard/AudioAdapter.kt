package com.example.socialvocal.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialvocal.R

class AudioAdapter(private val fileNameList: List<String>) : RecyclerView.Adapter<AudioAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.AudioFile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val audioListView: View = inflater.inflate(R.layout.custom_layout_audio, parent, false)

        // Return a new holder instance
        return ViewHolder(audioListView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomAudio: String = fileNameList.get(position)

        // Set item views based on your views and data model
        val textview = holder.textView
        textview.text = nomAudio
    }

    override fun getItemCount(): Int {
        return fileNameList.size
    }
}
