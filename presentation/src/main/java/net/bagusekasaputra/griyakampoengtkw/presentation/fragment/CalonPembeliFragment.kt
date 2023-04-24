package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentCalonPembeliBinding

@AndroidEntryPoint
class CalonPembeliFragment : Fragment() {

    private lateinit var binding: FragmentCalonPembeliBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalonPembeliBinding.inflate(inflater, container, false)

        return binding.root
    }

}