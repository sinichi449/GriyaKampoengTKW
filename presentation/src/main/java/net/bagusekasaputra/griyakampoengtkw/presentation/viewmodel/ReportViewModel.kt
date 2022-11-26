package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class ReportViewModel: ViewModel() {

    val isFinishedOperation = MutableLiveData<Boolean>()
}