package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.BiayaPembangunanKavlingScreen
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common.FormsDialog
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common.MaterialPembangunanForms
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common.MaterialPembangunanTable
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.common.UpahPekerjaTable
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.theme.GriyaKampoengTkwTheme
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaPembangunanKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.util.NotificationUtil.createNotification
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembangunanKavlingViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ViewModelListener

@AndroidEntryPoint
class BiayaPembangunanKavlingFragment : Fragment() {

    private lateinit var binding: FragmentBiayaPembangunanKavlingBinding

    private val viewModel by viewModels<PembangunanKavlingViewModel>()

    // TODO: Pass Data Mode
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kavlingKode = arguments?.getString(GriyaNodes.INTENT_KAVLING_KODE)
        if (!kavlingKode.isNullOrEmpty()) {
            viewModel.kavlingKode = kavlingKode
            viewModel.dataMode = DataMode.ONLINE // <-- Change this
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBiayaPembangunanKavlingBinding.inflate(inflater, container, false)
        binding.composeViewPembangunanKavling.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(lifecycle))
            setContent {
                GriyaKampoengTkwTheme {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val informasiPembangunan = viewModel.informasiPembangunan.collectAsState()
                        val materialPembangunan = viewModel.materialList.collectAsState()
                        val upahPekerja = viewModel.upahPekerjaList.collectAsState()

                        var mbSelectedRow by remember { mutableStateOf(-1) }

                        BiayaPembangunanKavlingScreen(
                            modifier = Modifier.padding(16.dp),
                            informasiPembangunan = informasiPembangunan.value,
                            materialPembangunanTableView = {
                                MaterialPembangunanTable(
                                    materialPembangunan = materialPembangunan.value,
                                    selectedRow = mbSelectedRow,
                                    onTableRowClicked = {
                                        viewModel.setSelectedMaterialPembangunan(it)
                                        mbSelectedRow = it

                                        // Open Dialog Input
                                        viewModel.updateMaterialPembangunanDialogState(clearSelected = false)
                                    }
                                )
                            },
                            upahPekerjaTableView = {
                                UpahPekerjaTable(
                                    upahPekerja = upahPekerja.value,
                                )
                            }
                        )

                        viewModel.materialPembangunanDialogState.collectAsState().value.also {
                            FormsDialog(
                                show = it,
                                onDismissRequest = {
                                    // Delete previously `selectedMaterialPembangunan`
                                    viewModel.updateMaterialPembangunanDialogState()
                                },
                            ) {
                                MaterialPembangunanForms(
                                    modifier = Modifier.padding(16.dp),
                                    material = viewModel.selectedMaterialPembangunan,
                                    onSubmit = { editMode, result ->
                                        val listener = object : ViewModelListener {
                                            override fun onProgress() {}

                                            override fun onCompleted() {
                                                viewModel.updateMaterialPembangunanDialogState()

                                                sync(SyncRequest.MATERIAL_PEMBANGUNAN)

                                                requireActivity().createNotification {
                                                    setSmallIcon(R.drawable.ic_baseline_check_circle_18)
                                                    setContentTitle("Berhasil ${if (editMode) "Mengubah" else "Menambahkan"}!")
                                                    setContentText("Material \"${result.namaMaterial}\" berhasil ${if (editMode) "diubah" else "ditambahkan"}.")
                                                    setAutoCancel(true)
                                                }
                                            }

                                            override fun onFailed(failMsg: String?) {
                                                viewModel.updateMaterialPembangunanDialogState()

                                                requireActivity().createNotification {
                                                    setSmallIcon(R.drawable.baseline_close_24)
                                                    setContentTitle("Terjadi Kesalahan!")
                                                    setContentText(failMsg ?: "Unknown Error inserting ${result.namaMaterial}")
                                                }
                                            }
                                        }

                                        if (editMode) {
                                            viewModel.editMaterialPembangunan(
                                                oldData = viewModel.selectedMaterialPembangunan!!,
                                                kavling = viewModel.kavlingKode,
                                                nama = result.namaMaterial,
                                                tanggal = result.tanggal,
                                                orderQty = result.orderQty.toDouble(),
                                                satuan = result.satuan,
                                                arrivedQty = result.arrivedQty.toDouble(),
                                                hargaTotal = result.hargaTotal.toLong(),
                                                totalBayar = result.terbayar.toLong(),
                                                keterangan = result.keterangan,
                                                listener = listener,
                                            )
                                        } else {
                                            viewModel.addMaterialPembangunan(
                                                kavling = viewModel.kavlingKode,
                                                nama = result.namaMaterial,
                                                tanggal = result.tanggal,
                                                orderQty = result.orderQty.toDouble(),
                                                satuan = result.satuan,
                                                arrivedQty = result.arrivedQty.toDouble(),
                                                hargaTotal = result.hargaTotal.toLong(),
                                                totalBayar = result.terbayar.toLong(),
                                                keterangan = result.keterangan,
                                                listener = listener,
                                            )
                                        }
                                    },
                                    onDeleteRequest = { keyId ->
                                        val listener = object : ViewModelListener {
                                            override fun onProgress() {}

                                            override fun onCompleted() {
                                                viewModel.updateMaterialPembangunanDialogState(clearSelected = true)

                                                requireActivity().createNotification {
                                                    setSmallIcon(R.drawable.ic_baseline_check_circle_18)
                                                    setContentTitle("Berhasil Menghapus!")
                                                    setContentText("Menghapus $keyId berhasil!")
                                                    setAutoCancel(true)
                                                }

                                                sync(SyncRequest.MATERIAL_PEMBANGUNAN)
                                            }

                                            override fun onFailed(failMsg: String?) {
                                                viewModel.updateMaterialPembangunanDialogState(clearSelected = true)

                                                requireActivity().createNotification {
                                                    setSmallIcon(R.drawable.baseline_close_24)
                                                    setContentTitle("Gagal Menghapus!")
                                                    setContentText(failMsg)
                                                }
                                            }
                                        }

                                        viewModel.deleteMaterialPembangunan(
                                            kavling = viewModel.kavlingKode,
                                            keyId = keyId!!,
                                            listener = listener,
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            swipeRefreshPembangunanKavling.setOnRefreshListener {
                sync()
                swipeRefreshPembangunanKavling.isRefreshing = false
            }

            extendedFabPembangunanKavling.setOnClickListener {
                viewModel.updateExtendedFabState()
            }

            fabUpahPekerja.setOnClickListener {
                Toast.makeText(requireContext(), "Tambahkan Upah Pekerja", Toast.LENGTH_SHORT).show()
            }

            fabMaterialPembangunan.setOnClickListener {
                viewModel.updateMaterialPembangunanDialogState()
            }

            UiUtils.hideExtendedFabOnVerticalScroll(
                nestedScrollView = scrollViewPembangunanKavling,
                extendedFabs = extendedFabPembangunanKavling,
            )
        }

        sync()

        setupViewModel()
    }

    private fun sync(vararg requests: Int = SyncRequest.ALL) {
        val currentKavling = viewModel.kavlingKode
        if (currentKavling.isEmpty()) {
            Snackbar.make(binding.root, "Kavling Kode on Biaya Pembangunan Fragment doesn't received properly!", Snackbar.LENGTH_SHORT)
                .show()
        } else {
            Log.d("PEMBANGUNAN", "Executing synchronization now!")
            requests.forEach { requestCode ->
                when (requestCode) {
                    SyncRequest.MATERIAL_PEMBANGUNAN -> {
                        viewModel.fetchMaterialPembangunan(currentKavling, object : ViewModelListener {
                            override fun onProgress() {}

                            override fun onCompleted() {}

                            override fun onFailed(failMsg: String?) {
                                Toast.makeText(
                                    requireContext(),
                                    "Gagal mendapatkan Material Pembangunan: $failMsg",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    }
                    SyncRequest.UPAH_PEKERJA -> {
                        viewModel.fetchUpahPekerja(currentKavling, object : ViewModelListener {
                            override fun onProgress() {}

                            override fun onCompleted() {}

                            override fun onFailed(failMsg: String?) {
                                Toast.makeText(
                                    requireContext(),
                                    "Gagal mendapatkan Upah Pekerja: $failMsg",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        })
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupViewModel() {
        with(binding) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.fabIsExtended.collect { isExtended ->
                        val fabList = listOf(fabMaterialPembangunan, fabUpahPekerja)

                        if (isExtended) {
                            extendedFabPembangunanKavling.extend()
                            fabList.forEach { it.show() }
                        } else {
                            extendedFabPembangunanKavling.shrink()
                            fabList.forEach { it.hide() }
                        }
                    }
                }
            }
        }
    }

    private object SyncRequest {
        const val MATERIAL_PEMBANGUNAN = 0
        const val UPAH_PEKERJA = 1

        val ALL = intArrayOf(MATERIAL_PEMBANGUNAN, UPAH_PEKERJA)
    }
}