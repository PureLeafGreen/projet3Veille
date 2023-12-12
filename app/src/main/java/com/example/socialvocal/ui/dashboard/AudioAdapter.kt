package com.example.socialvocal.ui.dashboard

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialvocal.R
import com.example.socialvocal.sessionManagement.SessionManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class AudioAdapter(private val fileNameList: List<String>) : RecyclerView.Adapter<AudioAdapter.ViewHolder>() {
    private lateinit var filesDir: File
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.AudioFile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val audioListView: View = inflater.inflate(R.layout.custom_layout_audio, parent, false)
        filesDir = context.filesDir
        // Return a new holder instance
        return ViewHolder(audioListView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomAudio: String = fileNameList.get(position)

        // Set item views based on your views and data model
        val textview = holder.textView
        textview.text = nomAudio
        val listenButton: Button = holder.itemView.findViewById(R.id.buttonListen)
        listenButton.setOnClickListener {
            playAudio(nomAudio)
        }
    }

    override fun getItemCount(): Int {
        return fileNameList.size
    }

    fun playAudio(fileName: String) {
        val userFolder = File(filesDir, SessionManager.getCurrentUser()!!)
        val file = File(userFolder, fileName)

        if (file.exists()) {
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(file.absolutePath)
                mediaPlayer.setOnPreparedListener { player ->
                    // Start playback when the media source is prepared
                    player.start()
                }
                mediaPlayer.setOnCompletionListener { player ->
                    // Release resources after playback is completed
                    player.release()
                }
                mediaPlayer.prepareAsync()
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle IOException
                println("Error: ${e.message}")
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle other exceptions
                println("Error: ${e.message}")
            }
        } else {
            // Handle case where file doesn't exist
            println("File $fileName does not exist in filesDir")
        }
    }
}
