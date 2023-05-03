package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.evrencoskun.tableview.TableView
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaPribadi
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaPribadiBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaPribadi.BiayaPribadiTableAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaPribadi.TableBiayaPribadi
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.BiayaPribadiViewModel

@AndroidEntryPoint
class BiayaPribadiFragment : Fragment() {

    private lateinit var binding: FragmentBiayaPribadiBinding
    private val viewModel: BiayaPribadiViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBiayaPribadiBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshBiayaPribadi.setOnRefreshListener {
            sync()
        }

        binding.extendedFabActions.setOnClickListener {
            val isExtended = viewModel.showFab.value ?: false

            viewModel.showFab.value = !isExtended
        }

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.listBiayaPribadiLive.observe(requireActivity()) {
            it?.also { listBiayaPribadi ->
                binding.tableViewBiayaPribadi.setItem(listBiayaPribadi)
            }
        }

        viewModel.showFab.observe(requireActivity()) {
            it?.also { extend ->
                UiUtils.extendOrShrinkExtendedFab(
                    extendedFabs = binding.extendedFabActions,
                    anotherFabs = listOf(
                        binding.fabTambahkan,
                        binding.fabUbah,
                    ),
                    extend = extend,
                )
            }
        }
    }

    private fun TableView.setItem(listBiayaPribadi: List<BiayaPribadi>) {
        val adapter = BiayaPribadiTableAdapter()
        setAdapter(adapter)

        val tableBiayaPribadi = TableBiayaPribadi(listBiayaPribadi)
        adapter.setAllItems(
            tableBiayaPribadi.getColumnHeaderItems(),
            tableBiayaPribadi.getRowHeaderItems(),
            tableBiayaPribadi.getCellItems(),
        )

        // TODO: Set column widths
    }

    private fun sync() {
        viewModel.getListBiayaPribadi(
            onComplete = {
                binding.swipeRefreshBiayaPribadi.isRefreshing = false
            },
            onFailure = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        )
    }

    override fun onResume() {
        super.onResume()

        sync()
    }
}