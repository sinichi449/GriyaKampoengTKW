package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.FragmentFormPembayaranBinding

@AndroidEntryPoint
class FormPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentFormPembayaranBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFormPembayaranBinding.inflate(inflater, container, false)
        return binding.root
    }


}