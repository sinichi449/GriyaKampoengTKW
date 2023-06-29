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
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.theme.GriyaKampoengTkwTheme
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaPembangunanKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembangunanKavlingViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ViewModelListener

@AndroidEntryPoint
class BiayaPembangunanKavlingFragment : Fragment() {

    private lateinit var binding: FragmentBiayaPembangunanKavlingBinding

    private val pembangunanViewModel by viewModels<PembangunanKavlingViewModel>()

    // TODO: Pass Data Mode
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kavlingKode = arguments?.getString(GriyaNodes.INTENT_KAVLING_KODE)
        if (!kavlingKode.isNullOrEmpty()) {
            pembangunanViewModel.kavlingKode = kavlingKode
            pembangunanViewModel.dataMode = DataMode.ONLINE // <-- Change this
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
                        val inputMaterialPembangunanDialog by pembangunanViewModel.inputMaterialPembangunanDialog.collectAsState()

                        BiayaPembangunanKavlingScreen(
                            modifier = Modifier.padding(16.dp),
                            pembangunanViewModel = pembangunanViewModel,
                            onTableUpahRowHeaderClicked = { row ->
                                Toast.makeText(
                                    requireContext(),
                                    "Upah Pekerja $row",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onTableUpahCellClicked = { column, row ->
                                Toast.makeText(
                                    requireContext(),
                                    "Upah Pekerja ${column}:${row}" ,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onTableMaterialCellClicked = { column, row ->
                                Toast.makeText(
                                    requireContext(),
                                    "Material ${column}:${row}" ,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onTableMaterialRowClicked = { row ->
                                val materialPembangunan = pembangunanViewModel.materialList.value[row]
                                pembangunanViewModel.selectedMaterialPembangunan = materialPembangunan

                                pembangunanViewModel.updateInputMaterialDialog()
                            }
                        )

                        if (inputMaterialPembangunanDialog) {
                            MaterialPembangunanInputDialog(
                                kavling = pembangunanViewModel.kavlingKode,
                                material = pembangunanViewModel.selectedMaterialPembangunan,
                                onSubmit = {
                                    // TODO
                                },
                                onDeleteRequest = { keyId ->
                                    // TODO
                                },
                                onCancelled = {
                                    pembangunanViewModel.updateInputMaterialDialog()
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
                pembangunanViewModel.updateExtendedFabState()
            }

            fabUpahPekerja.setOnClickListener {
                Toast.makeText(requireContext(), "Tambahkan Upah Pekerja", Toast.LENGTH_SHORT).show()
            }

            fabMaterialPembangunan.setOnClickListener {
                pembangunanViewModel.updateInputMaterialDialog()
            }
        }

        sync()

        setupViewModel()
    }

    private fun sync() {
        val currentKavling = pembangunanViewModel.kavlingKode
        if (currentKavling.isEmpty()) {
            Snackbar.make(binding.root, "Kavling Kode on Biaya Pembangunan Fragment doesn't received properly!", Snackbar.LENGTH_SHORT)
                .show()
        } else {
            Log.d("PEMBANGUNAN", "Executing synchronization now!")
            pembangunanViewModel.fetchMaterialPembangunan(currentKavling, object : ViewModelListener {
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
            pembangunanViewModel.fetchUpahPekerja(currentKavling, object : ViewModelListener {
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
                    pembangunanViewModel.fabIsExtended.collect { isExtended ->
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