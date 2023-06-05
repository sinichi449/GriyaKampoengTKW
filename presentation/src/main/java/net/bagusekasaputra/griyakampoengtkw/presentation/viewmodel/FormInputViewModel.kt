package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import javax.inject.Inject

@HiltViewModel
class FormInputViewModel @Inject constructor(

): ViewModel() {

    private val _pembayaranSelectedJenisTermin = MutableStateFlow<Pembayaran.JenisPembayaran?>(null)
    val pembayaranSelectedJenisTermin = _pembayaranSelectedJenisTermin.asStateFlow()

    fun setSelectedJenisTermin(jenisTermin: Pembayaran.JenisPembayaran) {
        _pembayaranSelectedJenisTermin.update { jenisTermin }
    }

}