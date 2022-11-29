package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.PengingatRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionPengingatBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentPengingatBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PengingatViewModel

@AndroidEntryPoint
class PengingatFragment : Fragment() {

    private lateinit var binding: FragmentPengingatBinding
    private lateinit var fabActionAdd: ExtendedFloatingActionButton

    private val pengingatViewModel by viewModels<PengingatViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPengingatBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        fabActionAdd = requireActivity().findViewById(R.id.fab_actions)

        binding.swipeRefreshPengingat.setOnRefreshListener {
            pengingatViewModel.pengingatRefreshed.value = false

            sync()
        }

        fabActionAdd.setOnClickListener {
            showActionPengingatDialog(null)
        }
    }

    override fun onResume() {
        super.onResume()

        sync()
    }

    private fun sync() {
        pengingatViewModel.getAllPengingat {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        pengingatViewModel.isFinishOperation.observe(requireActivity()) { finished ->
            if (finished != null) {
                binding.swipeRefreshPengingat.isRefreshing = finished.not()
            }
        }

        pengingatViewModel.pengingatRefreshed.observe(requireActivity()) { isRefreshed ->
            if (isRefreshed != null) {
                if (isRefreshed.not()) sync()
            }
        }

        pengingatViewModel.listPengingat.observe(requireActivity()) { listPengingat ->
            if (listPengingat != null) {
                setupPengingatRecyclerView(listPengingat)
            }
        }
    }

    private fun setupPengingatRecyclerView(listPengingat: List<Pengingat>) {
        val adapter = PengingatRecyclerAdapter(
            listPengingat = listPengingat,
            onItemLongClick = { position ->
                showActionPengingatDialog(listPengingat[position])
            }
        )

        binding.recyclerPengingat.adapter = adapter
        binding.recyclerPengingat.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showActionPengingatDialog(pengingat: Pengingat?) {
        val dialogBinding = DialogActionPengingatBinding.inflate(layoutInflater)
        val pengingatDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), pengingatDialog)

        pengingatDialog.show()
    }
}