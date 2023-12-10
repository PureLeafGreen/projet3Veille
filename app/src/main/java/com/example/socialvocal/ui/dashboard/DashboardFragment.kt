package com.example.socialvocal.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.socialvocal.R
import com.example.socialvocal.databinding.FragmentDashboardBinding
import android.media.AudioFormat
import android.media.AudioRecord
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var recorder: AudioRecord? = null
    private var isRecording = false
    private lateinit var outputFile: File
    private val bufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
    private val REQUEST_CODE_PERMISSION = 1001 // You can use any unique value here
    private var retrievedFiles = mutableListOf<File>()
    private var file: File? = null
    private lateinit var filesDir: File
    var numberOfFiles = 0

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

        // Find your button by its ID
        val recordButton: Button = view.findViewById(R.id.button)
        val rvAudio = view.findViewById<RecyclerView>(R.id.AudioRecycler)
        val adapter = AudioAdapter(getAllFilesNames())
        rvAudio.adapter = adapter
        rvAudio.layoutManager = LinearLayoutManager(requireContext())
        filesDir = requireContext().filesDir
        numberOfFiles = filesDir.listFiles()?.size ?: 0
        // Set an OnClickListener to the button
        recordButton.setOnClickListener {
            // Call your function here when the button is pressed
            startRecording()
        }
    }
    private fun startRecording() {
        if (!isRecording) {
            if (numberOfFiles == 0) {
                numberOfFiles = 1
            } else {
                numberOfFiles++
            }
            outputFile = File(filesDir, "audio_record${numberOfFiles}.pcm")
            val audioData = ByteArray(bufferSize)
            recorder?.startRecording()
            isRecording = true
            Thread {
                try {
                    val outputStream = FileOutputStream(outputFile)
                    while (isRecording) {
                        val numberOfBytes = recorder?.read(audioData, 0, bufferSize)
                        if (numberOfBytes != null) {
                            outputStream.write(audioData, 0, numberOfBytes)
                        }
//                        //random byte
//                        val randomByte = Random.nextInt(0, 255).toByte()
//                        outputStream.write(randomByte.toInt())
                        Log.d("send", String(byteArrayOf(numberOfBytes?.toByte() ?: 0)))
                    }
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
            binding.button.text = "Arrêter"
        } else {
            isRecording = false
            recorder?.stop()
            recorder?.release()
            binding.button.text = "Enregistrer"
        }
    }

    private fun retrieveFileFromLocalStorage(fileName: String): ByteArray? {
        try {
            file = File(filesDir, fileName)
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
        filesDir.listFiles()?.forEach {
            files.add(it.name)
        }
        return files
    }

    private fun deleteAllFiles() {
        filesDir.listFiles()?.forEach {
            it.delete()
        }
    }

    private fun deleteFile(fileName: String) {
        File(filesDir, fileName).delete()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}