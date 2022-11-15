package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.FragmentBiayaMarketingBinding

@AndroidEntryPoint
class BiayaMarketingFragment : Fragment() {

    private lateinit var binding: FragmentBiayaMarketingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBiayaMarketingBinding.inflate(inflater, container, false)
        return binding.root
    }

}