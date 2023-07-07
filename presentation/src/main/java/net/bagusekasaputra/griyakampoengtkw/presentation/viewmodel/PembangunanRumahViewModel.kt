package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import javax.inject.Inject

@HiltViewModel
class PembangunanRumahViewModel @Inject constructor(

): ViewModel() {

    var kavlingKode: String = ""
    var dataMode: DataMode = DataMode.ONLINE


}