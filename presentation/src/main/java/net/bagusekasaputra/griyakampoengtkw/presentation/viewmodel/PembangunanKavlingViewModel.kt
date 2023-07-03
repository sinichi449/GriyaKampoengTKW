package net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.CheckPembangunanKavlingEligibilityAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.materialPembangunan.AddMaterialPembangunanAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.materialPembangunan.DeleteMaterialPembangunanAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.materialPembangunan.GetAllMaterialPembangunanAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.materialPembangunan.UpdateMaterialPembangunanAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.upahPekerja.GetAllUpahPekerjaAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.InformasiPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.UpahPekerja
import net.bagusekasaputra.griyakampoengtkw.domain.misc.PembayaranBelumMencukupiException
import javax.inject.Inject

@HiltViewModel
class PembangunanKavlingViewModel @Inject constructor(
    private val checkPembangunanKavlingEligibilityUseCase: CheckPembangunanKavlingEligibilityAsyncUseCase,
    /* Material Pembangunan */
    private val getAllMaterialPembangunanUseCase: GetAllMaterialPembangunanAsyncUseCase,
    private val addMaterialPembangunanUseCase: AddMaterialPembangunanAsyncUseCase,
    private val updateMaterialPembangunanUseCase: UpdateMaterialPembangunanAsyncUseCase,
    private val deleteMaterialPembangunanUseCase: DeleteMaterialPembangunanAsyncUseCase,
    /* Upah Pekerja */
    private val getAllUpahPekerjaUseCase: GetAllUpahPekerjaAsyncUseCase,
): ViewModel() {

    var kavlingKode = ""
    var dataMode = DataMode.ONLINE
    var selectedMaterialPembangunan: MaterialPembangunan? = null
        private set

    private var jobCheckElligibility: Job? = null
    private var jobFetchMaterialPembangunan: Job? = null
    private var jobFetchUpahPekerja: Job? = null
    private var jobAddMaterialPembangunan: Job? = null
    private var jobUpdateMaterialPembangunan: Job? = null
    private var jobDeleteMaterialPembangunan: Job? = null

    private val _elligibilityStatus = MutableStateFlow<ElligibiltyStatus?>(null)
    private val _fabIsExtended = MutableStateFlow(false)
    private val _materialPembangunanDialogState = MutableStateFlow(false)

    val elligibilityStatus = _elligibilityStatus.asStateFlow()
    val fabIsExtended = _fabIsExtended.asStateFlow()
    val materialPembangunanDialogState = _materialPembangunanDialogState.asStateFlow()

    private val _informasiPembangunan = MutableStateFlow(InformasiPembangunan.EMPTY(kavlingKode))
    private val _materialList = MutableStateFlow(emptyList<MaterialPembangunan>())
    private val _upahPekerjaList = MutableStateFlow(emptyList<UpahPekerja>())

    val informasiPembangunan = _informasiPembangunan.asStateFlow()
    val materialList = _materialList.asStateFlow()
    val upahPekerjaList = _upahPekerjaList.asStateFlow()

    fun checkElligibility(kavling: String, listener: ViewModelListener) {
        jobCheckElligibility?.cancel()

        listener.onProgress()

        jobCheckElligibility = viewModelScope.launch(Dispatchers.IO) {
            val request = CheckPembangunanKavlingEligibilityAsyncUseCase.Request(kavling)
            checkPembangunanKavlingEligibilityUseCase.execute(request).collect { result ->
                result.onSuccess {
                    _elligibilityStatus.update {
                        ElligibiltyStatus(
                            elligible = true,
                            reason = ""
                        )
                    }

                    withContext(Dispatchers.Main) { listener.onCompleted() }
                }
                result.onFailure {
                    it.printStackTrace()

                    withContext(Dispatchers.Main) {
                        if (it is PembayaranBelumMencukupiException) {
                            _elligibilityStatus.update { _ ->
                                ElligibiltyStatus(
                                    elligible = false,
                                    reason = it.message ?: "NULL"
                                )
                            }

                            listener.onCompleted()
                        } else {
                            listener.onFailed(it.message)
                        }
                    }
                }
            }
        }
    }

    fun fetchMaterialPembangunan(kavling: String, listener: ViewModelListener) {
        jobFetchMaterialPembangunan?.cancel()

        jobFetchMaterialPembangunan = viewModelScope.launch(Dispatchers.Main) {
            val request = GetAllMaterialPembangunanAsyncUseCase.Request(
                untuk = kavling,
                kategori = MaterialPembangunan.Kategori.KAVLING,
                dataMode = dataMode,
            )
            getAllMaterialPembangunanUseCase.execute(request)
                .onStart { listener.onProgress() }
                .onCompletion { throwable ->
                    if (throwable == null) {
                        listener.onCompleted()
                    } else {
                        listener.onFailed(throwable.message)
                    }
                }
                .collect { result ->
                    withContext(Dispatchers.IO) {
                        result.onSuccess { items ->
                            if (!items.isNullOrEmpty()) {
                                _materialList.value = items
                            }
                        }
                        result.onFailure {
                            it.printStackTrace()
                        }
                    }
                }
        }
    }

    fun fetchUpahPekerja(kavling: String, listener: ViewModelListener) {
        jobFetchUpahPekerja?.cancel()

        jobFetchUpahPekerja = viewModelScope.launch(Dispatchers.Main) {
            val request = GetAllUpahPekerjaAsyncUseCase.Request(kavling, dataMode)
            getAllUpahPekerjaUseCase.execute(request)
                .onStart { listener.onProgress() }
                .onCompletion { throwable ->
                    if (throwable == null) {
                        listener.onCompleted()
                    } else {
                        listener.onFailed(throwable.message)
                    }
                }
                .collect { result ->
                    withContext(Dispatchers.IO) {
                        result.onSuccess { items ->
                            if (!items.isNullOrEmpty()) {
                                _upahPekerjaList.value = items
                            }
                        }
                        result.onFailure {
                            it.printStackTrace()
                        }
                    }
                }
        }
    }

    fun addMaterialPembangunan(
        kavling: String,
        nama: String,
        tanggal: String,
        orderQty: Double,
        arrivedQty: Double,
        satuan: String,
        hargaTotal: Long,
        totalBayar: Long,
        keterangan: String,
        listener: ViewModelListener,
    ) {
        jobAddMaterialPembangunan?.cancel()

        jobAddMaterialPembangunan = viewModelScope.launch(Dispatchers.Main) {
            listener.onProgress()

            val materialPembangunan = MaterialPembangunan(
                untuk = kavling,
                kategori = MaterialPembangunan.Kategori.KAVLING,
                namaMaterial = nama,
                tanggal = tanggal.toDate(),
                qty = orderQty,
                satuan = satuan,
                hargaTotal = hargaTotal,
                kelunasan = MaterialPembangunan.getKelunasan(totalBayar, hargaTotal),
                kedatangan = MaterialPembangunan.getKedatangan(arrivedQty, orderQty),
                keterangan = keterangan.ifEmpty { "-" },
            )
            val request = AddMaterialPembangunanAsyncUseCase.Request(materialPembangunan)
            // Must be executed only once
            val result = withContext(Dispatchers.IO) {
                addMaterialPembangunanUseCase.execute(request).first()
                    .onSuccess {
                        withContext(Dispatchers.Main) { listener.onCompleted() }
                    }
                    .onFailure {
                        it.printStackTrace()

                        withContext(Dispatchers.Main) { listener.onFailed(it.message) }
                    }
            }
        }
    }

    fun editMaterialPembangunan(
        oldData: MaterialPembangunan,
        kavling: String,
        nama: String,
        tanggal: String,
        orderQty: Double,
        arrivedQty: Double,
        satuan: String,
        hargaTotal: Long,
        totalBayar: Long,
        keterangan: String,
        listener: ViewModelListener,
    ) {
        jobUpdateMaterialPembangunan?.cancel()

        jobUpdateMaterialPembangunan = viewModelScope.launch(Dispatchers.Main) {
            listener.onProgress()

            val newData = MaterialPembangunan(
                keyId = oldData.keyId,
                untuk = kavling,
                kategori = MaterialPembangunan.Kategori.KAVLING,
                namaMaterial = nama,
                tanggal = tanggal.toDate(),
                qty = orderQty,
                satuan = satuan,
                hargaTotal = hargaTotal,
                kelunasan = MaterialPembangunan.getKelunasan(totalBayar, hargaTotal),
                kedatangan = MaterialPembangunan.getKedatangan(arrivedQty, orderQty),
                keterangan = keterangan.ifEmpty { "-" },
            )
            val request = UpdateMaterialPembangunanAsyncUseCase.Request(
                oldData = oldData,
                newData = newData,
            )
            withContext(Dispatchers.IO) {
                updateMaterialPembangunanUseCase.execute(request).first()
                    .onSuccess {
                        withContext(Dispatchers.Main) { listener.onCompleted() }
                    }
                    .onFailure {
                        withContext(Dispatchers.Main) {
                            it.printStackTrace()

                            listener.onFailed(it.message)
                        }
                    }
            }
        }
    }

    fun deleteMaterialPembangunan(
        kavling: String,
        keyId: String,
        listener: ViewModelListener,
    ) {
        jobDeleteMaterialPembangunan?.cancel()

        listener.onProgress()

        jobDeleteMaterialPembangunan = viewModelScope.launch(Dispatchers.IO) {
            val request = DeleteMaterialPembangunanAsyncUseCase.Request(
                keyId = keyId,
                targetBangunan = kavling,
                kategori = MaterialPembangunan.Kategori.KAVLING,
            )
            deleteMaterialPembangunanUseCase.execute(request).first()
                .onSuccess {
                    withContext(Dispatchers.Main) { listener.onCompleted() }
                }
                .onFailure {
                    it.printStackTrace()

                    withContext(Dispatchers.Main) { listener.onFailed(it.message) }
                }
        }
    }

    fun updateExtendedFabState() {
        _fabIsExtended.update{ !it }
    }

    fun updateMaterialPembangunanDialogState(clearSelected: Boolean = true) {
        _materialPembangunanDialogState.update { !it }

        if (clearSelected) {
            selectedMaterialPembangunan = null
        }
    }

    fun setSelectedMaterialPembangunan(index: Int) {
        selectedMaterialPembangunan = _materialList.value[index]
    }

    data class ElligibiltyStatus(
        val elligible: Boolean,
        val reason: String,
    )
}