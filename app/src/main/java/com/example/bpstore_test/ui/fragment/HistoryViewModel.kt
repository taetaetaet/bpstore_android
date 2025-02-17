package com.example.bpstore_test.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryViewModel : ViewModel() {

    private val _historyData = MutableLiveData<String>()
    val historyData: LiveData<String> get() = _historyData

    fun loadHistory() {
        _historyData.value = "이전 기록 데이터"
    }
}