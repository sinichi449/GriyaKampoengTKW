package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentRekapBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.RekapSmallTabBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.RekapViewModel

@AndroidEntryPoint
class RekapFragment : Fragment() {

    private lateinit var binding: FragmentRekapBinding
    private val viewModel by activityViewModels<RekapViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRekapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            navigateTo(RekapType.Global)
        }

        setupViewModel()

        binding.smallTab.btnRekapGlobal.setOnClickListener {
            navigateTo(RekapType.Global)
        }

        binding.smallTab.btnRekapBesar.setOnClickListener {
            navigateTo(RekapType.Besar)
        }

        binding.swipeRefreshRekap.isEnabled = false
    }

    private fun setupViewModel() {
        viewModel.currentFragment.observe(requireActivity()) {
            if (it != null) {
                binding.smallTab.setSelectedRekap(it)
            }
        }

    }

    private fun navigateTo(rekapType: RekapType) {
        // only allow when current fragment is not the same
        val currentFragment = viewModel.currentFragment.value

        Log.d("DEBUG_ME", "navigateTo: Current Fragment is $currentFragment and you wanna go to $rekapType")

        if (currentFragment != rekapType) {
            val fragment = when (rekapType) {
                RekapType.Global -> RekapGlobalFragment()
                RekapType.Besar -> RekapBesarFragment()
                else -> null
            }
            fragment?.let {
                Log.d("DEBUG_ME", "navigateTo: rekap fragment's transition accepted.")

                childFragmentManager.beginTransaction()
                    .replace(binding.rekapContainer.id, it)
                    .commit()

                viewModel.currentFragment.value = rekapType
            }
        }
    }

    private fun RekapSmallTabBinding.setSelectedRekap(rekapType: RekapType) {
        val getColor = { colorId: Int ->
            ContextCompat.getColor(requireContext(), colorId)
        }
        val purple = getColor(R.color.secondaryColor)
        val white = getColor(R.color.white)

        when (rekapType) {
            RekapType.Global -> {
                this.btnRekapGlobal.apply {
                    setBackgroundColor(purple)
                    setTextColor(white)
                }

                this.btnRekapBesar.apply {
                    setBackgroundColor(white)
                    setTextColor(purple)
                }
            }
            RekapType.Besar -> {
                this.btnRekapGlobal.apply {
                    setBackgroundColor(white)
                    setTextColor(purple)
                }

                this.btnRekapBesar.apply {
                    setBackgroundColor(purple)
                    setTextColor(white)
                }
            }
            else -> {}
        }
    }
}