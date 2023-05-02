package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.listener.ITableViewListener
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CalonPembeli
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentCalonPembeliBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.calonPembeli.CalonPembeliTableAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.calonPembeli.TableCalonPembeli
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.CalonPembeliViewModel

@AndroidEntryPoint
class CalonPembeliFragment : Fragment() {

    private lateinit var binding: FragmentCalonPembeliBinding
    private val viewModel: CalonPembeliViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalonPembeliBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        binding.swipeRefreshCalonPembeli.setOnRefreshListener {
            sync()
        }

        binding.extendedFabActions.setOnClickListener {
            val isExtended = viewModel.showExtendedFab.value ?: false

            binding.extendedFabActions.run {
                if (isExtended) shrink()
                else extend()
            }

            viewModel.showExtendedFab.value = !isExtended
        }
    }

    private fun setupViewModel() {
        viewModel.listCalonPembeliLive.observe(requireActivity()) {
            it?.also { calonPembelis ->
                setupTableView(calonPembelis)
            }
        }

        viewModel.showExtendedFab.observe(requireActivity()) {
            it?.also { extend ->
                UiUtils.extendOrShrinkExtendedFab(
                    extendedFabs = binding.extendedFabActions,
                    anotherFabs = listOf(binding.fabTambahkan, binding.fabUbah),
                    extend = extend
                )
            }
        }
    }

    private fun setupTableView(listCalonPembeli: List<CalonPembeli>) {
        val adapter = CalonPembeliTableAdapter()
        binding.tableViewCalonPembeli.setAdapter(adapter)

        val tableCalonPembeli = TableCalonPembeli(listCalonPembeli)
        adapter.setAllItems(
            tableCalonPembeli.getColumnHeaderItems(),
            tableCalonPembeli.getRowHeaderItems(),
            tableCalonPembeli.getCellItems(),
        )

        // Set Column Width
        binding.tableViewCalonPembeli.run {
            setColumnWidth(0, 350) // Nama
            setColumnWidth(1, 400) // No Hp
            setColumnWidth(2, 400) // Tiktok
            setColumnWidth(3, 500) // Keterangan
        }

        binding.tableViewCalonPembeli.tableViewListener = object : ITableViewListener {
            override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
                // Open whatsapp on the one of "No Hp" column cell
                if (column == 1) {
                    val noHp = listCalonPembeli[row].noHp
                    UiUtils.openWhatsapp(requireContext(), noHp)
                }
            }

            override fun onCellDoubleClicked(
                cellView: RecyclerView.ViewHolder,
                column: Int,
                row: Int
            ) {

            }

            override fun onCellLongPressed(
                cellView: RecyclerView.ViewHolder,
                column: Int,
                row: Int
            ) {

            }

            override fun onColumnHeaderClicked(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onColumnHeaderDoubleClicked(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onColumnHeaderLongPressed(
                columnHeaderView: RecyclerView.ViewHolder,
                column: Int
            ) {

            }

            override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {

            }

            override fun onRowHeaderDoubleClicked(
                rowHeaderView: RecyclerView.ViewHolder,
                row: Int
            ) {

            }

            override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {

            }

        }
    }

    private fun sync() {
        viewModel.getListCalonPembeli(
            onComplete = {
                binding.swipeRefreshCalonPembeli.isRefreshing = false
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