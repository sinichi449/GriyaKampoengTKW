package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.biayaPembangunan

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
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentBiayaPembangunanBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.BiayaPembangunanViewModel

@AndroidEntryPoint
class BiayaPembangunanFragment : Fragment() {

    private lateinit var binding: FragmentBiayaPembangunanBinding

    private val viewModel by activityViewModels<BiayaPembangunanViewModel>()

    private val materialAndUpahKerjaFragments by lazy {
        buildList {
            add(FragmentAndTitle("Material", BiayaMaterialFragment()))
            add(FragmentAndTitle("Upah Kerja", BiayaUpahKerjaFragment()))
        }
    }

    companion object {
        const val PAGE_BIAYA_MATERIAL = 0
        const val PAGE_UPAH_KERJA = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBiayaPembangunanBinding.inflate(inflater, container, false)

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