package com.example.socialvocal.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialvocal.R
import com.example.socialvocal.databinding.FragmentDashboardBinding
import com.example.socialvocal.sessionManagement.SessionManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    //Const
    private val RECORD_AUDIO_PERMISSION_CODE: Int = 1001

    private var mediaRecorder: MediaRecorder? = null
    private val binding get() = _binding!!
    private var isRecording = false
    private lateinit var outputFile: File
    private var file: File? = null
    private lateinit var filesDir: File
    private lateinit var rvAudio: RecyclerView
    private lateinit var adapter: AudioAdapter
    var numberOfFiles = 0
    var db = Firebase.firestore

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init filesVariables
        filesDir = requireContext().filesDir
        numberOfFiles = filesDir.listFiles()?.size ?: 0
        // Find your button by its ID
        val recordButton: Button = view.findViewById(R.id.button)
        val deleteAllButton: Button = view.findViewById(R.id.deleteAll)
        //init recyclerView
        rvAudio = view.findViewById(R.id.AudioRecycler) as RecyclerView
        adapter = AudioAdapter(getAllFilesNames())
        rvAudio.adapter = adapter
        rvAudio.layoutManager = LinearLayoutManager(this.context)
        // Set an OnClickListener to the button
        recordButton.setOnClickListener {
            if (!isRecording) {
                startRecording()
            } else {
                stopRecording()
            }
        }
        deleteAllButton.setOnClickListener {
            deleteAllFiles()
            updateRecyclerView()
        }
    }
    private fun startRecording() {
        if (ActivityCompat.checkSelfPermission(
                this.context!!,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.activity!!,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_PERMISSION_CODE
            )
            return
        }
        val userFolder = File(filesDir, SessionManager.getCurrentUser()!!)
        numberOfFiles = userFolder.listFiles()?.size ?: 0
        if (numberOfFiles == 0) {
            numberOfFiles = 1
        } else {
            numberOfFiles++
        }
        //create folder for user
        if (!userFolder.exists()) {
            userFolder.mkdir()
        }
        outputFile = File(userFolder, "audio_record${numberOfFiles}.mp3")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            try {
                prepare()
                start()
                isRecording = true
            } catch (e: IOException) {
                Log.e("Recording", "MediaRecorder preparation failed: ${e.message}")
            }
        }
        binding.button.text = "Arrêter"
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            try {
                stop()
                reset()
                release()
                isRecording = false
                Log.d("Recording", "Recording stopped. File saved at $outputFile")
            } catch (e: Exception) {
                Log.e("Recording", "MediaRecorder stop/release failed: ${e.message}")
            }
        }
        binding.button.text = "Enregistrer"
        updateRecyclerView()
        addFileToDatabase(outputFile.name)
    }

    private fun addFileToDatabase(fileName: String) {
        val userFolder = File(filesDir, SessionManager.getCurrentUser()!!)
        val file = File(userFolder, fileName)
        val inputStream = FileInputStream(file)
        val bytes = inputStream.readBytes()
        inputStream.close()

        val base64String = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)

        val audio = hashMapOf(
            "audio" to base64String
        )

        db.collection("user")
            .document(SessionManager.getCurrentUser()!!)
            .collection("audio")
            .document(fileName)
            .set(audio)
            .addOnSuccessListener { Log.d("database", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("database", "Error writing document", e) }
    }

    private fun retrieveFileFromLocalStorage(fileName: String): ByteArray? {
        try {
            val userFolder = File(filesDir, SessionManager.getCurrentUser()!!)
            file = File(userFolder, fileName)
//            Log.d("file", filesDir.listFiles()?.forEach {
//                Log.d("file", it.delete().toString())
//          }.toString())
            Log.d("file", file!!.toString())
            Log.d("file", file!!.readBytes().toString())
            Log.d("file", file!!.delete().toString())
            Log.d("file", filesDir.listFiles()?.size.toString())
            val inputStream = FileInputStream(file)
            val bytes = inputStream.readBytes()
            inputStream.close()

            return bytes
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getAllFilesNames(): List<String> {
        val files = mutableListOf<String>()
        val userFolder = File(filesDir, SessionManager.getCurrentUser()!!)
        userFolder.listFiles()?.forEach {
            files.add(it.name)
        }
        return files.toList()
    }

    private fun deleteAllFiles() {
        val userFolder = File(filesDir, SessionManager.getCurrentUser()!!)
        userFolder.listFiles()?.forEach {
            it.delete()
        }
        numberOfFiles = 0
        updateRecyclerView()
    }

    private fun deleteFile(fileName: String) {
        File(filesDir, fileName).delete()
    }

    private fun updateRecyclerView() {
        adapter = AudioAdapter(getAllFilesNames())
        rvAudio.adapter = adapter
        rvAudio.layoutManager = LinearLayoutManager(this.context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}