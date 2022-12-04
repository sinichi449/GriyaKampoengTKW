package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentReportAndMiscBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaLain.*

@AndroidEntryPoint
class ReportAndMiscFragment: Fragment() {

    private lateinit var binding: FragmentReportAndMiscBinding
    private lateinit var fabActions: ExtendedFloatingActionButton

    private var isFabAllVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportAndMiscBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabActions = requireActivity().findViewById(R.id.fab_actions)

        setupFloatingActionButton()


        val dummyTableData = getDummyTableBiayaLain()
        val columnHeaders = BlColumnHeader.getColumnHeaders()
        val rowHeaders = BlRowHeader.getRowHeaders(dummyTableData.size)

        setupTableBiayaLain(columnHeaders, rowHeaders, dummyTableData)
    }

    private fun getDummyTableBiayaLain(): List<List<BlCell>> {
        val dummyA = listOf(BlCell("biaya 1"), BlCell("50,000"), BlCell("13/07/2022"))
        val dummyB = listOf(BlCell("biaya 2"), BlCell("150,000"), BlCell("09/09/2022"))

        return listOf(dummyA, dummyB)
    }

    private fun setupTableBiayaLain(
        columnHeaders: List<BlColumnHeader>,
        rowHeaders: List<BlRowHeader>,
        cellItems: List<List<BlCell>>,
    ) {
        val adapter = TableBiayaLainViewAdapter()

        binding.tableviewBiayaLain.setAdapter(adapter)

        adapter.setAllItems(columnHeaders,rowHeaders,cellItems)

        binding.tableviewBiayaLain.apply {
            setColumnWidth(BiayaLainColumnPosition.JENIS_BIAYA, 500)
            setColumnWidth(BiayaLainColumnPosition.HARGA, 300)
            setColumnWidth(BiayaLainColumnPosition.TANGGAL, 300)
        }

        adapter.notifyDataSetChanged()
    }

    private fun setupFloatingActionButton() {
        binding.fabTambahBiayaLain.hide()
        binding.fabEditBiayaLain.hide()

        fabActions.setOnClickListener {
            if (isFabAllVisible) {
                // hide the fabs
                binding.fabTambahBiayaLain.hide()
                binding.fabEditBiayaLain.hide()

                fabActions.shrink()

                isFabAllVisible = false
            } else {
                // show the fabs
                binding.fabTambahBiayaLain.show()
                binding.fabEditBiayaLain.show()

                fabActions.extend()

                isFabAllVisible = true
            }
        }
    }
}