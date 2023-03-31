package com.example.qrgenerator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QrCodeViewmodel :ViewModel() {
    val data = MutableLiveData<String>()

    fun data(item: String) {
        data.value = item
    }
}