package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.listener.ITableViewListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.presentation.ImageTransport
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.dialog.ModifyIndenBookingDialog
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.indenBooking.IndenBookingTableAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.indenBooking.TableIndenBooking
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.MainViewModel

@AndroidEntryPoint
class IndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentIndenBookingBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: IndenBookingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentIndenBookingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dataMode = mainViewModel.dataMode

        setupViewModel()

        binding.spinnerUrutkan.apply {
            val listOpsiFilter = listOf("Nama", "Tanggal", "Jumlah Uang")
            adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                listOpsiFilter,
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    try {
                        viewModel.sortListIndenBooking(listOpsiFilter[position])
                    } catch (e: Exception) {
                        e.printStackTrace()

                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
        }

        binding.swipeRefreshIndenBooking.setOnRefreshListener {
            sync()
        }

//        binding.extendedFabActions.setOnClickListener {
//            val isExtended = viewModel.showFab.value ?: false
//
//            viewModel.showFab.value = !isExtended
//        }

        binding.fabTambahkan.setOnClickListener {
            showAddIndenBookingDialog()
        }

//        binding.fabUbah.setOnClickListener {
//            // TODO
//            Toast.makeText(requireContext(), "NOT YET IMPLEMENTED!", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun setupViewModel() {
        viewModel.listIndenBookingLive.observe(requireActivity()) {
            it?.also {
                binding.tableViewIndenBooking.setItem(it)
            }
        }

//        viewModel.showFab.observe(requireActivity()) {
//            it?.also { extend ->
//                UiUtils.extendOrShrinkExtendedFab(
//                    extendedFabs = binding.extendedFabActions,
//                    anotherFabs = listOf(
//                        binding.fabTambahkan,
//                        binding.fabUbah,
//                    ),
//                    extend = extend,
//                )
//            }
//        }
    }

    private fun TableView.setItem(listIndenBooking: List<IndenBooking>) {
        val adapter = IndenBookingTableAdapter()
        setAdapter(adapter)

        val tableIndenBooking = TableIndenBooking(listIndenBooking)
        adapter.setAllItems(
            tableIndenBooking.getColumnHeaderItems(),
            tableIndenBooking.getRowHeaderItems(),
            tableIndenBooking.getCellItems(),
        )

        setColumnWidth(TableIndenBooking.COLUMN_NAMA_COSTUMER, 400)
        setColumnWidth(TableIndenBooking.COLUMN_TANGGAL_DIBAYAR, 300) // Tanggal Dibayar
        setColumnWidth(TableIndenBooking.COLUMN_JUMLAH_UANG, 350) // Jumlah Uang
        setColumnWidth(TableIndenBooking.COLUMN_NO_HP, 400) // No Hp
        setColumnWidth(TableIndenBooking.COLUMN_KETERANGAN, 500) // Keterangan

        tableViewListener = object : ITableViewListener {
            override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
                if (column == TableIndenBooking.COLUMN_NO_HP) {
                    val noHp = listIndenBooking[row].noHp
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
                val indenBooking = listIndenBooking[row]
                val pathFoto = indenBooking.fotoPembayaranPath

                if (pathFoto.isEmpty()) {
                    Snackbar.make(binding.root, "Tidak Tersedia Foto Pembayaran Inden Booking!", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    val imageTransport = ImageTransport(
                        sendIntention = GriyaNodes.INTENT_FOTO_INDEN_BOOKING,
                        content = mapOf(
                            Pair("pathFoto", indenBooking.fotoPembayaranPath)
                        ),
                        dataMode = mainViewModel.dataMode,
                    )
                    UiUtils.openFotoFull(requireContext(), imageTransport)
                }
            }

            override fun onRowHeaderDoubleClicked(
                rowHeaderView: RecyclerView.ViewHolder,
                row: Int
            ) {

            }

            override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
                ModifyIndenBookingDialog(listIndenBooking[row])
                    .show(childFragmentManager, null)
            }

        }
    }

    private fun showAddIndenBookingDialog() {
        ModifyIndenBookingDialog().apply {
            isCancelable = false
        }.show(childFragmentManager, null)
    }
    private fun sync() {
        viewModel.getListIndenBooking(
            onProgress = {
                binding.swipeRefreshIndenBooking.isRefreshing = true
            },
            onComplete = {
                binding.swipeRefreshIndenBooking.isRefreshing = false
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