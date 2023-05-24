package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.indenBooking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentDataDiriIndenBookingBinding

@AndroidEntryPoint
class DataDiriIndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentDataDiriIndenBookingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDataDiriIndenBookingBinding.inflate(inflater, container, false)

        return binding.root
    }

}