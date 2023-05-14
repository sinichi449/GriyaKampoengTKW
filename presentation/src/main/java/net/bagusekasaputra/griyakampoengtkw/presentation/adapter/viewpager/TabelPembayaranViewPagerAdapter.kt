package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembayaran.BulananPembayaranFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembayaran.FullPembayaranFragment

class TabelPembayaranViewPagerAdapter(
    fragmentManager: FragmentManager
): FragmentStatePagerAdapter(fragmentManager) {

    var fragments = listOf(
        FullPembayaranFragment(),
        BulananPembayaranFragment(),
    )

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
}