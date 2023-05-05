package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.evrencoskun.tableview.TableView
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.dialog.ModifyIndenBookingDialog
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.indenBooking.IndenBookingTableAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.indenBooking.TableIndenBooking
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

        binding.swipeRefreshIndenBooking.setOnRefreshListener {
            sync()
        }

        binding.extendedFabActions.setOnClickListener {
            val isExtended = viewModel.showFab.value ?: false

            viewModel.showFab.value = !isExtended
        }

        binding.fabTambahkan.setOnClickListener {
            showAddIndenBookingDialog()
        }

        binding.fabUbah.setOnClickListener {
            // TODO
            Toast.makeText(requireContext(), "NOT YET IMPLEMENTED!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        viewModel.listIndenBookingLive.observe(requireActivity()) {
            it?.also {
                binding.tableViewIndenBooking.setItem(it)
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

    private fun TableView.setItem(listIndenBooking: List<IndenBooking>) {
        val adapter = IndenBookingTableAdapter()
        setAdapter(adapter)

        val tableIndenBooking = TableIndenBooking(listIndenBooking)
        adapter.setAllItems(
            tableIndenBooking.getColumnHeaderItems(),
            tableIndenBooking.getRowHeaderItems(),
            tableIndenBooking.getCellItems(),
        )

        // TODO: Set Column width
    }

    private fun showAddIndenBookingDialog() {
        ModifyIndenBookingDialog().apply {
            isCancelable = false
        }.show(childFragmentManager, null)
    }

    private fun sync() {
        viewModel.getListIndenBooking(
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