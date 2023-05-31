package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
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

    // This listener need to be removed on onStop()
    // Set visibility of MainActivity's FAB
    private val viewPagerPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            when (position) {
                // On Rekap Fragment
                1 -> binding.fabActions.hide()
                else -> binding.fabActions.show()
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentManagementKavlingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()

        binding.fabActions.shrink()
    }

    private fun setupViewPager() {
        val fragments = listOf(
            KavlingFragment(),
            RekapFragment(),
            BiayaLainFragment(),
        )
        binding.viewPagerManagementKavling.adapter = ManagementKavlingViewPagerAdapter(
            fragmentManager = childFragmentManager,
            fragments = fragments,
        )

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

        // Disable FloatingActionButton on RekapFragment
        binding.viewPagerManagementKavling.addOnPageChangeListener(viewPagerPageChangeListener)
    }

    override fun onStop() {
        binding.viewPagerManagementKavling.removeOnPageChangeListener(viewPagerPageChangeListener)

        super.onStop()
    }
}