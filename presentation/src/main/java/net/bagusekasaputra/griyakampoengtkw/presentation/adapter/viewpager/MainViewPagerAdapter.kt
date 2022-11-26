package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val fragments: List<Fragment>,
): FragmentStatePagerAdapter(fragmentManager) {


    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
}