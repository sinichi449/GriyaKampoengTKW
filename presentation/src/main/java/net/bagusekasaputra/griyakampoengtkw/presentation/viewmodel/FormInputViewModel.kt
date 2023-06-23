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

    private val _pembayaranSelectedJenisTermin = MutableStateFlow<Pembayaran.JenisTermin?>(null)
    val pembayaranSelectedJenisTermin = _pembayaranSelectedJenisTermin.asStateFlow()

    fun setSelectedJenisTermin(jenisTermin: Pembayaran.JenisTermin) {
        _pembayaranSelectedJenisTermin.update { jenisTermin }
    }


    private val _pembayaranUntukBulanSekarang = MutableStateFlow(true)
    val pembayaranUntukBulanSekarang = _pembayaranUntukBulanSekarang.asStateFlow()

    fun setPembayaranUntukBulanSekarang(untukBulanSekarang: Boolean) {
        _pembayaranUntukBulanSekarang.update { untukBulanSekarang }
    }
}