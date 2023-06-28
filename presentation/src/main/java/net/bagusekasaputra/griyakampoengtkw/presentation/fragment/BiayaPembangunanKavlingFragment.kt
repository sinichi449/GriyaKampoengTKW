package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.theme.GriyaKampoengTkwTheme
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaPembangunanKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PembangunanKavlingViewModel

@AndroidEntryPoint
class BiayaPembangunanKavlingFragment : Fragment() {

    private lateinit var binding: FragmentBiayaPembangunanKavlingBinding

    private val pembangunanViewModel by viewModels<PembangunanKavlingViewModel>()

    // TODO: Pass Data Mode
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kavlingKode = arguments?.getString(GriyaNodes.INTENT_KAVLING_KODE)
        if (kavlingKode.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Kavling Kode on Pembangunan Fragment is not properly received!",
                Toast.LENGTH_LONG
            ).show()
        } else {
            pembangunanViewModel.kavlingKode = kavlingKode
            pembangunanViewModel.dataMode = DataMode.ONLINE // <-- Change this
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBiayaPembangunanKavlingBinding.inflate(inflater, container, false)
        binding.composeViewPembangunanKavling.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(lifecycle))
            setContent {
                GriyaKampoengTkwTheme {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.background
                    ) {

                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}