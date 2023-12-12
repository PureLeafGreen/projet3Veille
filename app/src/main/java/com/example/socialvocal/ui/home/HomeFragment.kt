package com.example.socialvocal.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialvocal.databinding.FragmentHomeBinding
import com.example.socialvocal.modeles.AudioFile
import com.example.socialvocal.sessionManagement.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var rvAuditeurAudio: RecyclerView
    private lateinit var adapter: AuditeursAudioAdapter
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (SessionManager.getCurrentUser() == null) {
            SessionManager.setCurrentUser("user1")
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val swUser1: Button = binding.swUser1
        val swUser2: Button = binding.swUser2
        val swUser3: Button = binding.swUser3
        rvAuditeurAudio = binding.rvAuditeursAudio
        adapter = AuditeursAudioAdapter(emptyMap())
        rvAuditeurAudio.adapter = adapter
        rvAuditeurAudio.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch {
            val auditeurs = getAuditeursAudio()
            adapter.updateData(auditeurs) // Assuming you have a method in the adapter to update data
        }
        swUser1.setOnClickListener {
            SessionManager.setCurrentUser("user1")
            lifecycleScope.launch {
                val auditeurs = getAuditeursAudio()
                adapter.updateData(auditeurs) // Assuming you have a method in the adapter to update data
            }
            // val intent = Intent(this, MainActivity::class.java)
            // startActivity(intent)
        }
        swUser2.setOnClickListener {
            SessionManager.setCurrentUser("user2")
            lifecycleScope.launch {
                val auditeurs = getAuditeursAudio()
                adapter.updateData(auditeurs) // Assuming you have a method in the adapter to update data
            }
            //val intent = Intent(this, MainActivity::class.java)
            // startActivity(intent)
        }
        swUser3.setOnClickListener {
            SessionManager.setCurrentUser("user3")
            lifecycleScope.launch {
                val auditeurs = getAuditeursAudio()
                adapter.updateData(auditeurs) // Assuming you have a method in the adapter to update data
            }
            // val intent = Intent(this, MainActivity::class.java)
            // startActivity(intent)
        }
    }

    suspend fun getAuditeursAudio(): Map<String, AudioFile> {
        val auditeursAudio = mutableMapOf<String, AudioFile>()
        val user = SessionManager.getCurrentUser()
        val followByUser = mutableListOf<String>()
        val queryFollowers = db.collection("user").document(user!!).collection("following").get().await()

        for (document in queryFollowers.documents) {
            val username = document.getString("username")
            if (username != null) {
                followByUser.add(username)
            }
        }
        if (followByUser.isNotEmpty()) {
            for (followingUser in followByUser) {
                val querySnapshot = db.collection("user").document(followingUser).collection("audio").get().await()
                for (document in querySnapshot.documents) {
                    val audioName = document.id
                    val audio64Based = document.get("audio") as String
                    val audioByteArray = android.util.Base64.decode(audio64Based, android.util.Base64.DEFAULT)
                    auditeursAudio[followingUser] = AudioFile(audioName, audioByteArray)
                }
            }
        }
        return auditeursAudio
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI() {
        // val currentUser = SessionManager.getCurrentUser()
        // if (currentUser != null) {
        //     val intent = Intent(this, MainActivity::class.java)
        //     startActivity(intent)
        // }
    }
}