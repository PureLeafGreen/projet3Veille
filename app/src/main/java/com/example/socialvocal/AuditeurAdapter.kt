package com.example.socialvocal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialvocal.sessionManagement.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.socialvocal.Auditeurs.lesAuditeurs as Auditeurs

class AuditeurAdapter(private var userNameList: List<String>) : RecyclerView.Adapter<AuditeurAdapter.ViewHolder>(){
    val db = FirebaseFirestore.getInstance()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.userName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val auditeurListView: View = inflater.inflate(R.layout.custom_layout_auditeur, parent, false)

        // Return a new holder instance
        return ViewHolder(auditeurListView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomAuditeur: String = userNameList.get(position)
        val followButton: Button = holder.itemView.findViewById(R.id.followButton)
        // Set item views based on your views and data model
        val textview = holder.textView
        textview.text = nomAuditeur

        followButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                followUser(nomAuditeur)
            }
        }
    }

    private suspend fun followUser(nomAuditeur: String) {
        db.collection("user").document(SessionManager.getCurrentUser()!!).collection("following").document(nomAuditeur).set(hashMapOf("username" to nomAuditeur))
        val auditeurs = Auditeurs.getAuditeurs()
        updateData(auditeurs)
    }

    override fun getItemCount(): Int {
        return userNameList.size
    }

    fun updateData(auditeurs: List<String>) {
        userNameList = auditeurs
        notifyDataSetChanged()
    }
}