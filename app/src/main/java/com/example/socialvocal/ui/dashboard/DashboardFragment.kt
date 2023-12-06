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
    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val REQUEST_CODE_PERMISSION = 1001 // You can use any unique value here
    private var retrievedFiles = mutableListOf<File>()
    private var file: File? = null

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

        // Set an OnClickListener to the button
        recordButton.setOnClickListener {
            // Call your function here when the button is pressed
            startRecording()
        }
    }
    private fun startRecording() {
        //ask for permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
            return
        }
        if (!isRecording) {
            val filesDir = requireContext().filesDir
            outputFile = File(filesDir, "audio_record.txt")
            val audioData = ByteArray(bufferSize)
            recorder?.startRecording()
            isRecording = true
            Thread {
                try {
                    val outputStream = FileOutputStream(outputFile)
                    while (isRecording) {
//                        val numberOfBytes = recorder?.read(audioData, 0, bufferSize)
//                        if (numberOfBytes != null) {
//                            outputStream.write(audioData, 0, numberOfBytes)
//                        }
                        //random byte
                        val randomByte = Random.nextInt(0, 255).toByte()
                        outputStream.write(randomByte.toInt())
                        Log.d("send", String(byteArrayOf(randomByte)))
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
            retrieveFileFromLocalStorage("audio_record.txt")
        }
    }

    private fun retrieveFileFromLocalStorage(fileName: String): ByteArray? {
        try {
            val filesDir = requireContext().filesDir
            file = File(filesDir, fileName)
            Log.d("file", file!!.name)
            val inputStream = FileInputStream(file)
            val bytes = inputStream.readBytes()
            inputStream.close()

            return bytes
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}