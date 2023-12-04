package com.example.socialvocal.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Ici vous pouvez enregistrer votre voix"
    }
    val text: LiveData<String> = _text
}