package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentPengembalianPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils

@AndroidEntryPoint
class PengembalianPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentPengembalianPembayaranBinding
    private var fabAction: FloatingActionButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPengembalianPembayaranBinding.inflate(inflater, container, false)

        fabAction = requireActivity().findViewById(R.id.fab_action_pengembalian_pembayaran)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            UiUtils.hideFabsOnVerticalScroll(scrollViewContent, fabAction)
        }
    }

    override fun onResume() {
        super.onResume()

        fabAction?.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()

        fabAction?.visibility = View.GONE
    }

}