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
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.BiayaPembangunanKavlingScreen
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.MaterialPembangunanInputDialog
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.MaterialPembangunanTable
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.UpahPekerjaTable
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.theme.GriyaKampoengTkwTheme
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaPembangunanKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
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
                        val currentKavling = viewModel.kavlingKode
                        val selectedMaterialPembangunan = viewModel.selectedMaterialPembangunan
                        val inputMaterialPembangunanDialog by viewModel.inputMaterialPembangunanDialog.collectAsState()

                        BiayaPembangunanKavlingScreen(
                            modifier = Modifier.padding(16.dp),
                            informasiPembangunan = informasiPembangunan.value,
                            materialPembangunanTableView = {
                                MaterialPembangunanTable(
                                    materialPembangunan = materialPembangunan.value,
                                    onTableRowClicked = {
                                        viewModel.setSelectedMaterialPembangunan(it)

                                        // Open Dialog Input
                                        viewModel.updateInputMaterialDialog()
                                    }
                                )
                            },
                            upahPekerjaTableView = {
                                UpahPekerjaTable(
                                    upahPekerja = upahPekerja.value,
                                )
                            }
                        )

                        if (inputMaterialPembangunanDialog) {
                            MaterialPembangunanInputDialog(
                                kavling = currentKavling,
                                material = selectedMaterialPembangunan,
                                onSubmit = {
                                    // TODO
                                },
                                onDeleteRequest = { keyId ->
                                    // TODO
                                },
                                onCancelled = {
                                    viewModel.updateInputMaterialDialog()
                                },
                            )
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
                viewModel.updateInputMaterialDialog()
            }

            UiUtils.hideExtendedFabOnVerticalScroll(
                nestedScrollView = scrollViewPembangunanKavling,
                extendedFabs = extendedFabPembangunanKavling,
            )
        }

        sync()

        setupViewModel()
    }

    private fun sync() {
        val currentKavling = viewModel.kavlingKode
        if (currentKavling.isEmpty()) {
            Snackbar.make(binding.root, "Kavling Kode on Biaya Pembangunan Fragment doesn't received properly!", Snackbar.LENGTH_SHORT)
                .show()
        } else {
            Log.d("PEMBANGUNAN", "Executing synchronization now!")
            viewModel.fetchMaterialPembangunan(currentKavling, object : ViewModelListener {
                override fun onProgress() {

                }

                override fun onCompleted() {

                }

                override fun onFailed(failMsg: String?) {
                    Toast.makeText(
                        requireContext(),
                        "Gagal mendapatkan Material Pembangunan: $failMsg",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
            viewModel.fetchUpahPekerja(currentKavling, object : ViewModelListener {
                override fun onProgress() {

                }

                override fun onCompleted() {

                }

                override fun onFailed(failMsg: String?) {
                    Toast.makeText(
                        requireContext(),
                        "Gagal mendapatkan Upah Pekerja: $failMsg",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
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
}