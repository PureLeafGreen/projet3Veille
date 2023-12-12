package com.example.socialvocal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialvocal.databinding.FragmentAuditeursBinding
import com.example.socialvocal.databinding.FragmentDashboardBinding
import com.example.socialvocal.sessionManagement.SessionManager
import com.example.socialvocal.ui.dashboard.DashboardViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Auditeurs.newInstance] factory method to
 * create an instance of this fragment.
 */
class Auditeurs : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var rvAuditeur: RecyclerView
    private lateinit var adapter: AuditeurAdapter

    private var _binding: FragmentAuditeursBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val auditeursViewModel =
        ViewModelProvider(this).get(AuditeursViewModel::class.java)

        _binding = FragmentAuditeursBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAuditeurs
        auditeursViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvAuditeur = view.findViewById(R.id.rvAuditeur) as RecyclerView
        adapter = AuditeurAdapter(emptyList())
        rvAuditeur.adapter = adapter
        rvAuditeur.layoutManager = LinearLayoutManager(this.context)

        lifecycleScope.launch {
            val auditeurs = getAuditeurs()
            adapter.updateData(auditeurs) // Assuming you have a method in the adapter to update data
        }
    }

    companion object lesAuditeurs {
        val db = FirebaseFirestore.getInstance()

        suspend fun getAuditeurs(): List<String> {
            val listUser = mutableListOf<String>()
            val listAllUser = SessionManager.getListUser()
            val listDbUser = getDbAuditeurs()
            for (user in listAllUser) {
                if (user != SessionManager.getCurrentUser() && !listDbUser.contains(user)) {
                    listUser.add(user)
                }
            }
            return listUser.toList()
        }

        suspend fun getDbAuditeurs(): List<String> {
            val listDbUser = mutableListOf<String>()
            val querySnapshot = db.collection("user")
                .document(SessionManager.getCurrentUser()!!)
                .collection("following")
                .get()
                .await()

            for (document in querySnapshot.documents) {
                val username = document.getString("username")
                if (username != null) {
                    listDbUser.add(username)
                }
            }
            return listDbUser.toList()
        }
    }
}