package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
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
import net.bagusekasaputra.griyakampoengtkw.presentation.datetimeToCalendar
import net.bagusekasaputra.griyakampoengtkw.presentation.toCalendar
import net.bagusekasaputra.griyakampoengtkw.presentation.toHour
import net.bagusekasaputra.griyakampoengtkw.presentation.util.AlarmHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PengingatViewModel
import java.util.Calendar

@AndroidEntryPoint
class PengingatFragment : Fragment() {

    private lateinit var binding: FragmentPengingatBinding
    private lateinit var fabActionAdd: ExtendedFloatingActionButton

    private val pengingatViewModel by viewModels<PengingatViewModel>()
    private lateinit var alarmHelper: AlarmHelper

    private val TAG = "DEBUG_ME"

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

        alarmHelper = AlarmHelper(requireContext())

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
                val pengingat = listPengingat[position]
                pengingatViewModel.turnOnOrOffPengingat(
                    pengingat = pengingat,
                    onComplete = {
                        sync()

                        alarmHelper.cancelAlarm(pengingat.id)

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
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                dialogBinding.edtJudulPengingat,
                dialogBinding.edtTanggal,
                dialogBinding.edtWaktu,
            )

            if (isInvalidEdt.not()) {
                dialogBinding.btnTambahkanPengingat.text = "Menyimpan data ..."
                dialogBinding.btnTambahkanPengingat.isEnabled = false
                if (editMode) dialogBinding.btnHapusPengingat.isEnabled = false

                val title = dialogBinding.edtJudulPengingat.text.toString()
                val tanggal = dialogBinding.edtTanggal.text.toString()
                val waktu = dialogBinding.edtWaktu.text.toString()

                if (editMode) {
                    pengingatViewModel.updatePengingat(
                        oldPengingat = pengingat!!,
                        newTitle = title,
                        newContent = getString(R.string.notification_pengingat_content),
                        newDate = tanggal,
                        newTime = waktu,
                        isActive = pengingat.isActive,
                        onComplete = { msg, data ->
                            sync()
                            pengingatDialog.dismiss()

                            if (pengingat.isActive) {
                                alarmHelper.cancelAlarm(data?.id)

                                val calendar = "$tanggal $waktu".datetimeToCalendar()
                                data?.let {
                                    Log.d(TAG, "showActionPengingatDialog: Set pengingat pada ${calendar.time}")
                                    alarmHelper.setMonthyRepeatAlarm(calendar, it)
                                }
                            }

                            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    pengingatViewModel.addPengingat(
                        title = title,
                        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                        date = tanggal,
                        time = waktu,
                        onComplete = { msg, data ->
                            sync()
                            pengingatDialog.dismiss()

                            val calendar = "$tanggal $waktu".datetimeToCalendar()
                            data?.let {
                                Log.d(TAG, "showActionPengingatDialog: Set pengingat pada ${calendar.time}")
                                alarmHelper.setMonthyRepeatAlarm(calendar, it)
                            }

                            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        dialogBinding.btnHapusPengingat.setOnClickListener {
            if (editMode) {
                dialogBinding.btnTambahkanPengingat.text = "Menghapus pengingat ..."
                dialogBinding.btnTambahkanPengingat.isEnabled = false
                dialogBinding.btnHapusPengingat.isEnabled = false

                pengingatViewModel.deletePengingat(
                    oldPengingat = pengingat!!,
                    onComplete = { msg, id ->
                        sync()
                        pengingatDialog.dismiss()

                        id?.let {
                            alarmHelper.cancelAlarm(it)
                        }

                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}