package net.bagusekasaputra.griyakampoengtkw.presentation.detail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    private val fragments = ArrayList<Fragment>()
    private val titles = ArrayList<String>()

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        this.fragments.add(fragment)
        this.titles.add(title)
    }
}