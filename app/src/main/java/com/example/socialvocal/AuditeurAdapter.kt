package com.example.socialvocal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AuditeurAdapter(private val userNameList: List<String>) : RecyclerView.Adapter<AuditeurAdapter.ViewHolder>(){
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

        // Set item views based on your views and data model
        val textview = holder.textView
        textview.text = nomAuditeur
    }

    override fun getItemCount(): Int {
        return userNameList.size
    }
}