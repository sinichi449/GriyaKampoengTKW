package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.pembatalan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R

/**
 * A simple [Fragment] subclass.
 * Use the [PembatalanListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class PembatalanListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pembatalan_list, container, false)
    }

}