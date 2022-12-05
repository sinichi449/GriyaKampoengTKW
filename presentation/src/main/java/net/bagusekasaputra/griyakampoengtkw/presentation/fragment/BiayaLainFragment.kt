package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaLainBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaLain.*
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FabHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.BiayaLainViewModel

@AndroidEntryPoint
class BiayaLainFragment: Fragment() {

    private lateinit var binding: FragmentBiayaLainBinding

    private val viewModel: BiayaLainViewModel by viewModels()
    private lateinit var fabActions: ExtendedFloatingActionButton
    private lateinit var fabAddBiayaLain: FloatingActionButton
    private lateinit var fabEditBiayaLain: FloatingActionButton

    private var isFabAllVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBiayaLainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabActions = requireActivity().findViewById(R.id.fab_actions)
        fabAddBiayaLain = requireActivity().findViewById(R.id.fab_tambah_biaya_lain)
        fabEditBiayaLain = requireActivity().findViewById(R.id.fab_edit_biaya_lain)

        val fabHelper = FabHelper(
            fabAction = fabActions,
            fabs = arrayOf(fabAddBiayaLain, fabEditBiayaLain)
        )
        fabHelper.setupFabs()

        setupViewModel()


        binding.swipeRefreshBiayaLain.setOnRefreshListener {
            sync()
        }
    }

    override fun onResume() {
        super.onResume()

        sync()
    }

    private fun sync() {
        viewModel.getAllBiayaLain {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        viewModel.isFinishOperation.observe(requireActivity()) {
            if (it != null) {
                binding.swipeRefreshBiayaLain.isRefreshing = it.not()
            }
        }

        viewModel.listBiayaLain.observe(requireActivity()) {
            if (it != null) {
                setupTableBiayaLain(it)
            }
        }
    }


    private fun setupTableBiayaLain(listBiayaLain: List<BiayaLain>) {
        val adapter = TableBiayaLainViewAdapter()

        binding.tableviewBiayaLain.setAdapter(adapter)

        adapter.setAllItems(
            BlColumnHeader.getColumnHeaders(),
            BlRowHeader.getRowHeaders(listBiayaLain.size),
            BlCell.getListCellItems(listBiayaLain)
        )

        binding.tableviewBiayaLain.apply {
            setColumnWidth(BiayaLainColumnPosition.JENIS_BIAYA, 500)
            setColumnWidth(BiayaLainColumnPosition.HARGA, 300)
            setColumnWidth(BiayaLainColumnPosition.TANGGAL, 300)
        }

        adapter.notifyDataSetChanged()
    }

}