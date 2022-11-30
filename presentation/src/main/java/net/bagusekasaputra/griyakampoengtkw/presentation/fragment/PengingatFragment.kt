package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.PengingatRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionPengingatBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentPengingatBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.toCalendar
import net.bagusekasaputra.griyakampoengtkw.presentation.toHour
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PengingatViewModel
import java.util.*

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

        pengingatViewModel.listPengingat.observe(requireActivity()) { listPengingat ->
            if (listPengingat != null) {
                setupPengingatRecyclerView(listPengingat)
            }
        }
    }

    private fun setupPengingatRecyclerView(listPengingat: List<Pengingat>) {
        val adapter = PengingatRecyclerAdapter(
            listPengingat = listPengingat,
            onImgNotifClick = { position ->
                pengingatViewModel.turnOffPengingat(
                    pengingat = listPengingat[position],
                    onComplete = {
                        sync()

                        Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                    }
                )
            },
            onItemLongClick = { position ->
                showActionPengingatDialog(listPengingat[position])
            }
        )

        binding.recyclerPengingat.adapter = adapter
        binding.recyclerPengingat.layoutManager = LinearLayoutManager(requireContext())

        adapter.notifyDataSetChanged()
    }

    private fun showActionPengingatDialog(pengingat: Pengingat?) {
        val dialogBinding = DialogActionPengingatBinding.inflate(layoutInflater)
        val pengingatDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), pengingatDialog)

        val editMode = pengingat != null
        if (editMode) {
            dialogBinding.tvDialogPengingatTitle.text = "Ubah Pengingat"

            dialogBinding.edtJudulPengingat.setText(pengingat!!.title)
            dialogBinding.edtTanggal.setText(pengingat.date)
            dialogBinding.edtWaktu.setText(pengingat.time)

            dialogBinding.btnTambahkanPengingat.text = "Ubah"
            dialogBinding.btnHapusPengingat.visibility = View.VISIBLE
        }

        pengingatDialog.show()


        dialogBinding.btnPengingatPilihTanggal.setOnClickListener {
            val inputtedTanggal = dialogBinding.edtTanggal.text.toString()

            val current = if ((editMode) or (inputtedTanggal.isNotEmpty()))
                    inputtedTanggal.toCalendar()
                else
                    Calendar.getInstance()
            val year = current.get(Calendar.YEAR)
            val month = current.get(Calendar.MONTH)
            val day = current.get(Calendar.DAY_OF_MONTH)


            val mListener = DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                val properDay = if (mDay < 10) "0$mDay" else mDay.toString()
                val properMonth = if (mMonth.plus(1) < 10) "0$mMonth" else mMonth.plus(1).toString()

                val result = "$properDay/$properMonth/$mYear"
                dialogBinding.edtTanggal.setText(result)
            }

            DatePickerDialog(requireContext(), R.style.DatePicker,mListener, year, month, day)
                .show()
        }

        dialogBinding.btnPengingatPilihWaktu.setOnClickListener {
            val inputtedWaktu = dialogBinding.edtWaktu.text.toString()

            val current = if ((editMode) or (inputtedWaktu.isNotEmpty()))
                inputtedWaktu.toHour()
            else
                Calendar.getInstance()
            val hour = current.get(Calendar.HOUR_OF_DAY)
            val minute = current.get(Calendar.MINUTE)

            val mListener = TimePickerDialog.OnTimeSetListener { _, mHour, mMinute ->
                val properHour = if (mHour < 10) "0$mHour" else mHour.toString()
                val properMinute = if (mMinute < 10) "0$mMinute" else mMinute.toString()

                val result = "$properHour:$properMinute"
                dialogBinding.edtWaktu.setText(result)
            }

            TimePickerDialog(requireContext(), R.style.DatePicker, mListener, hour, minute, true)
                .show()
        }

        dialogBinding.btnTambahkanPengingat.setOnClickListener {
            // TODO
        }

        dialogBinding.btnHapusPengingat.setOnClickListener {
            // TODO
        }
    }
}