package com.example.socialvocal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuditeursViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Vos auditeurs"
    }
    val text: LiveData<String> = _text
}