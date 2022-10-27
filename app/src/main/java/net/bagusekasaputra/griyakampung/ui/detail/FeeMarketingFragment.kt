package net.bagusekasaputra.griyakampung.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampung.databinding.FragmentFeeMarketingBinding

@AndroidEntryPoint
class FeeMarketingFragment : Fragment() {

    private lateinit var binding: FragmentFeeMarketingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFeeMarketingBinding.inflate(inflater, container, false)
        return binding.root
    }

}