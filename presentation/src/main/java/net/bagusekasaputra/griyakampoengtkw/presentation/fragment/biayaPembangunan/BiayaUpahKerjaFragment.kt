package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.biayaPembangunan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaUpahKerjaBinding

@AndroidEntryPoint
class BiayaUpahKerjaFragment : Fragment() {

    private lateinit var binding: FragmentBiayaUpahKerjaBinding
    private var fabAction: FloatingActionButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBiayaUpahKerjaBinding.inflate(inflater, container, false)

        fabAction = requireActivity().findViewById(R.id.fab_action_biaya_upah_kerja)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAction?.setOnClickListener {
            Snackbar.make(binding.root, "Tambah Biaya Upah Kerja", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()

        fabAction?.visibility = View.VISIBLE
    }

    override fun onPause() {
        fabAction?.visibility = View.GONE

        super.onPause()
    }
}