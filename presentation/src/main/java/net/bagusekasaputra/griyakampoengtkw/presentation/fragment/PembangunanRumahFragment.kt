package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentPembangunanRumahBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembangunanRumahViewModel

private const val ARG_KAVLING = "kavling"

@AndroidEntryPoint
class PembangunanRumahFragment : Fragment() {

    private lateinit var binding: FragmentPembangunanRumahBinding
    private val viewModel: PembangunanRumahViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString(ARG_KAVLING)?.let { kavling ->
            viewModel.kavlingKode = kavling
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPembangunanRumahBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(kavling: String) =
            PembangunanRumahFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_KAVLING, kavling)
                }
            }
    }
}