package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaPembangunanKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes

@AndroidEntryPoint
class BiayaPembangunanKavlingFragment : Fragment() {

    private lateinit var binding: FragmentBiayaPembangunanKavlingBinding

    private var kavlingKode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        kavlingKode = arguments?.getString(GriyaNodes.INTENT_KAVLING_KODE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBiayaPembangunanKavlingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tvHello.text = kavlingKode
        }
    }
}