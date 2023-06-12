package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

@Suppress("DEPRECATION")
class BiayaPembangunanViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val fragments: List<FragmentAndTitle>,
): FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position].fragment
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragments[position].title
    }

    data class FragmentAndTitle(
        val title: String,
        val fragment: Fragment,
    )
}