package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.indenBooking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentDataDiriIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel

@AndroidEntryPoint
class DataDiriIndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentDataDiriIndenBookingBinding
    private val viewModel by activityViewModels<IndenBookingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDataDiriIndenBookingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshDataDiri.setOnRefreshListener {
            sync()
        }

        sync()
    }

    private fun sync() {
        viewModel.getListIndenBooking(
            onProgress = { binding.swipeRefreshDataDiri.isRefreshing = true },
            onComplete = { binding.swipeRefreshDataDiri.isRefreshing = false },
            onFailure = {
                binding.swipeRefreshDataDiri.isRefreshing = false

                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        )
    }
}