package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CalonPembeli
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentCalonPembeliBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.calonPembeli.CalonPembeliTableAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.calonPembeli.TableCalonPembeli
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
    }

    private fun setupViewModel() {
        viewModel.listCalonPembeliLive.observe(requireActivity()) {
            it?.also { calonPembelis ->
                setupTableView(calonPembelis)
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

        binding.tableViewCalonPembeli.run {
            setColumnWidth(0, 350) // Nama
            setColumnWidth(1, 400) // No Hp
            setColumnWidth(2, 400) // Tiktok
            setColumnWidth(3, 500) // Keterangan
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