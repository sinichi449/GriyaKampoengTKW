package net.bagusekasaputra.griyakampoengtkw.ui.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.DialogTambahDataDiriBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.FragmentDataDiriBinding

@AndroidEntryPoint
class DataDiriFragment : Fragment() {

    private lateinit var binding: FragmentDataDiriBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDataDiriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabTambahDataDiri.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialogBinding = DialogTambahDataDiriBinding.inflate(layoutInflater)
        val dialogView = AlertDialog.Builder(requireActivity())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogView.show()

        dialogBinding.btnTambahkan.setOnClickListener {
            dialogView.dismiss()
            Snackbar.make(requireContext(), binding.root, "Berhasil ditambahkan (fake)", Snackbar.LENGTH_SHORT).show()
        }

        dialogBinding.btnBatal.setOnClickListener {
            dialogView.dismiss()
        }

        setupSpinner(dialogBinding)
    }

    private fun setupSpinner(dialogBinding: DialogTambahDataDiriBinding) {
        val negaraBekerjaList = ArrayList<String>()
        negaraBekerjaList.add("Hongkong")
        negaraBekerjaList.add("Macau")
        negaraBekerjaList.add("Taiwan")
        negaraBekerjaList.add("Singapore")
        negaraBekerjaList.add("Malaysia")
        negaraBekerjaList.add("Arab Saudi")
        negaraBekerjaList.add("Abu Dhabi")

        val arrayAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, negaraBekerjaList
        )

        dialogBinding.spinnerNegaraBekerja.adapter = arrayAdapter
        // TODO: Spinner onclick
    }
}