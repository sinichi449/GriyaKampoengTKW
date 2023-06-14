package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.biayaPembangunan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaMaterialBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.biayaPembangunan.BiayaPembangunanFragment.Companion.PAGE_BIAYA_MATERIAL
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.BiayaPembangunanViewModel

@AndroidEntryPoint
class BiayaMaterialFragment : Fragment() {

    private lateinit var binding: FragmentBiayaMaterialBinding
    private var fabAction: FloatingActionButton? = null

    private val viewModel by activityViewModels<BiayaPembangunanViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBiayaMaterialBinding.inflate(inflater, container, false)

        fabAction = requireActivity().findViewById(R.id.fab_action_biaya_material)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAction?.setOnClickListener {
            Snackbar.make(binding.root, "Tambah Biaya Material", Snackbar.LENGTH_SHORT)
                .show()
        }

        with(binding) {
            setupWithViewModel()

            swipeRefreshBiayaMaterial.setOnRefreshListener { sync() }
        }
    }

    private fun FragmentBiayaMaterialBinding.setupWithViewModel() {
        // If `currentViewPagerPage` is this fragment, set `fabAction visibility to visible`
        // and vice versa if not
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.currentViewPagerPage.collect { currentPage ->
                    if (currentPage == PAGE_BIAYA_MATERIAL) {
                        fabAction?.show()
                    } else {
                        fabAction?.hide()
                    }
                }
            }
        }
    }

    private fun sync() {
        binding.swipeRefreshBiayaMaterial.isRefreshing = false
    }
}