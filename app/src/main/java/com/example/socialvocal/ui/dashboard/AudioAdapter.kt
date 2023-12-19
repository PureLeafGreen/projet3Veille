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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialvocal.R
import com.example.socialvocal.sessionManagement.SessionManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class AudioAdapter(private val fileNameList: List<String>, private val fileDeleteListener: FileDeleteListener) : RecyclerView.Adapter<AudioAdapter.ViewHolder>() {
    private lateinit var filesDir: File
    var db = Firebase.firestore
    private lateinit var audioItem: LinearLayout
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.AudioFile)
    }

    interface FileDeleteListener {
        fun deleteFile(fileName: String)
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
        audioItem = holder.itemView.findViewById(R.id.AudioItem)
        listenButton.setOnClickListener {
            playAudio(nomAudio)
        }
        audioItem.setOnLongClickListener {
            deleteFile(it.findViewById<TextView>(R.id.AudioFile).text.toString())
            true
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

    private fun deleteFile(fileName: String) {
        fileDeleteListener.deleteFile(fileName)
        val userFolder = File(filesDir, SessionManager.getCurrentUser()!!)
        userFolder.listFiles()?.forEach {
            if (it.name == fileName) {
                it.delete()
            }
        }
        db.collection("user")
            .document(SessionManager.getCurrentUser()!!)
            .collection("audio")
            .document(fileName)
            .delete()
            .addOnSuccessListener { Log.d("database", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("database", "Error deleting document", e) }
        notifyItemRemoved(fileNameList.indexOf(fileName))
        notifyItemRemoved(audioItem.indexOfChild(audioItem.findViewById(R.id.AudioFile)))
        notifyDataSetChanged()
    }
}
