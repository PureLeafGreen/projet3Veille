package com.example.socialvocal.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.socialvocal.databinding.FragmentHomeBinding
import com.example.socialvocal.sessionManagement.SessionManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        if (SessionManager.getCurrentUser() == null) {
            SessionManager.setCurrentUser("user1")
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("user", SessionManager.getCurrentUser().toString())
        binding.textHome.text = SessionManager.getCurrentUser()
        val swUser1: Button = binding.swUser1
        val swUser2: Button = binding.swUser2
        val swUser3: Button = binding.swUser3
        swUser1.setOnClickListener {
            SessionManager.setCurrentUser("user1")
            binding.textHome.text = SessionManager.getCurrentUser()
            // val intent = Intent(this, MainActivity::class.java)
            // startActivity(intent)
        }
        swUser2.setOnClickListener {
            SessionManager.setCurrentUser("user2")
            binding.textHome.text = SessionManager.getCurrentUser()
            //val intent = Intent(this, MainActivity::class.java)
            // startActivity(intent)
        }
        swUser3.setOnClickListener {
            SessionManager.setCurrentUser("user3")
            binding.textHome.text = SessionManager.getCurrentUser()
            // val intent = Intent(this, MainActivity::class.java)
            // startActivity(intent)
        }
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