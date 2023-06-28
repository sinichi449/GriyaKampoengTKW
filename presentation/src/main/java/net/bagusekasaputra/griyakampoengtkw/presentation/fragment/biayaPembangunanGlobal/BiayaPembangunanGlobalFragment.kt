package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.biayaPembangunanGlobal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.viewpager.BiayaPembangunanViewPagerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.viewpager.BiayaPembangunanViewPagerAdapter.FragmentAndTitle
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaPembangunanGlobalBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.BiayaPembangunanViewModel

@AndroidEntryPoint
class BiayaPembangunanGlobalFragment : Fragment() {

    private lateinit var binding: FragmentBiayaPembangunanGlobalBinding

    private val viewModel by activityViewModels<BiayaPembangunanViewModel>()

    private val materialAndUpahKerjaFragments by lazy {
        buildList {
            add(FragmentAndTitle("Material", BiayaMaterialFragment()))
            add(FragmentAndTitle("Upah Kerja", BiayaUpahKerjaFragment()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBiayaPembangunanGlobalBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            viewPagerBiayaPembangunan.setupViewPager(
                fragments = materialAndUpahKerjaFragments,
                pageChangeListener = object : SimpleOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        viewModel.setViewPagerPage(position)
                    }
                }
            )

            tabLayoutBiayaPembangunan.setupWith(viewPagerBiayaPembangunan)
        }
    }


    private fun ViewPager.setupViewPager(
        fragments: List<FragmentAndTitle>,
        pageChangeListener: SimpleOnPageChangeListener,
    ) {
        adapter = BiayaPembangunanViewPagerAdapter(
            fragmentManager = childFragmentManager,
            fragments = fragments,
        )
        addOnPageChangeListener(pageChangeListener)
    }

    private fun TabLayout.setupWith(viewPager: ViewPager) {
        setupWithViewPager(viewPager)
        tabIndicatorAnimationMode = TabLayout.INDICATOR_ANIMATION_MODE_ELASTIC
    }
}