package com.example.socialvocal.ui.home

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialvocal.R
import com.example.socialvocal.modeles.AudioFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class AuditeursAudioAdapter(private var auditeurAudio : Map<String, AudioFile>) : RecyclerView.Adapter<AuditeursAudioAdapter.ViewHolder>() {
    private lateinit var filesDir: File
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.auditeurName)
        val audioTextView: TextView = itemView.findViewById(R.id.auditeurFileName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val auditeurListView: View = inflater.inflate(R.layout.custom_layout_auditeuraudio, parent, false)
        filesDir = context.filesDir

        // Return a new holder instance
        return ViewHolder(auditeurListView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomAuditeur: String = auditeurAudio.keys.elementAt(position)
        val nomAudio: String = auditeurAudio.values.elementAt(position).fileName
        val audioData: ByteArray = auditeurAudio.values.elementAt(position).audio
        // Set item views based on your views and data model
        val usernameTextView = holder.usernameTextView
        val audioTextView = holder.audioTextView
        usernameTextView.text = nomAuditeur
        audioTextView.text = nomAudio

        val listenAudioButton : Button = holder.itemView.findViewById(R.id.listenAuditeur)
        listenAudioButton.setOnClickListener {
            playAudio(audioData)
        }

    }

    private fun playAudio(audioData: ByteArray) {
        try {

            val mediaPlayer = MediaPlayer()
            val tempFile = File.createTempFile("audio", "mp3", filesDir)
            tempFile.deleteOnExit()
            val fos = FileOutputStream(tempFile)
            fos.write(audioData)
            fos.close()
            val fis = FileInputStream(tempFile)
            mediaPlayer.setDataSource(fis.fd)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return auditeurAudio.keys.size
    }

    fun updateData(auditeurs: Map<String, AudioFile>) {
        auditeurAudio = auditeurs
        notifyDataSetChanged()
    }

}