package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.viewpager.ManagementKavlingViewPagerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentManagementKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.MainViewModel

@AndroidEntryPoint
class ManagementKavlingFragment : Fragment() {

    private lateinit var binding: FragmentManagementKavlingBinding
    private val viewModel: MainViewModel by activityViewModels()
    private var fabActions: ExtendedFloatingActionButton? = null

    // This listener need to be removed on onStop()
    // Set visibility of MainActivity's FAB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentManagementKavlingBinding.inflate(inflater, container, false)

        fabActions = requireActivity().findViewById(R.id.fab_actions)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()

//        binding.fabActions.shrink()
        fabActions?.shrink()
    }

    private fun setupViewPager() {
        val fragments = mutableListOf<Fragment>().apply {
            add(FRAGMENT_KAVLING, KavlingFragment())
            add(FRAGMENT_REKAP, RekapFragment())
            add(FRAGMENT_BIAYA_LAIN, BiayaLainFragment())
        }
        binding.viewPagerManagementKavling.apply {
            adapter = ManagementKavlingViewPagerAdapter(
                fragmentManager = childFragmentManager,
                fragments = fragments,
            )
            offscreenPageLimit = 3

            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    when (position) {
                        FRAGMENT_KAVLING -> {
                            viewModel.shouldNavigateToKavlingFragment.value = false

//                            binding.fabActions.show()
                            fabActions?.show()
                        }
                        FRAGMENT_REKAP -> {
                            viewModel.shouldNavigateToKavlingFragment.value = true

//                            binding.fabActions.hide()
                            fabActions?.hide()
                        }
                        FRAGMENT_BIAYA_LAIN -> {
                            viewModel.shouldNavigateToKavlingFragment.value = true

//                            binding.fabActions.show()
                            fabActions?.show()
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }

        // Integrate TabLayout with ViewPager, set Indicator Animation Mode
        // and set Tab's Icon.
        binding.tabLayoutManagementKavling.run {
            setupWithViewPager(binding.viewPagerManagementKavling)
            tabIndicatorAnimationMode = TabLayout.INDICATOR_ANIMATION_MODE_ELASTIC

            val getIcon = { iconId: Int -> ContextCompat.getDrawable(requireContext(), iconId) }
            getTabAt(0)?.icon = getIcon(R.drawable.ic_baseline_kavling_24)
            getTabAt(1)?.icon = getIcon(R.drawable.ic_baseline_report_24)
            getTabAt(2)?.icon = getIcon(R.drawable.ic_baseline_attach_money_24)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.managementKavlingFragment.value = this
        fabActions?.visibility = View.VISIBLE
    }

    override fun onPause() {
        viewModel.managementKavlingFragment.value = null
        fabActions?.visibility = View.GONE

        super.onPause()
    }

    fun navigateToKavlingFragment() {
        with(viewModel.shouldNavigateToKavlingFragment) {
            if (value == true) {
                binding.viewPagerManagementKavling.currentItem = FRAGMENT_KAVLING

                value = false
            }
        }
    }

    private companion object {
        const val FRAGMENT_KAVLING = 0
        const val FRAGMENT_REKAP = 1
        const val FRAGMENT_BIAYA_LAIN = 2
    }
}