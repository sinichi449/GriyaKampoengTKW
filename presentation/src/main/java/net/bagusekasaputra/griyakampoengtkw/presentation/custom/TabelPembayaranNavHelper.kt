package net.bagusekasaputra.griyakampoengtkw.presentation.custom

import android.app.Activity
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.NavHostFragment
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel.TablePembayaranType

class TabelPembayaranNavHelper(
    private val lifecycleOwner: LifecycleOwner,
    fragmentManager: FragmentManager,
    private val pembayaranViewModel: FormPembayaranViewModel,
    containerId: Int,
    vararg triggerViews: View?,
) {

    private var navController = (fragmentManager.findFragmentById(containerId)
            as NavHostFragment).navController
    var listener: TabelPembayaranListener? = null


    init {
        triggerViews.forEach {
            it?.setOnClickListener {
                switchTablePembayaran()
            }
        }

        setupViewModel()
    }

    private fun switchTablePembayaran() {
        val currentTablePembayaranType = pembayaranViewModel.tableTypeLive.value

        if (currentTablePembayaranType == TablePembayaranType.FORM_PEMBAYARAN) {
            pembayaranViewModel.setTableType(TablePembayaranType.PEMBAYARAN_BULANAN)
        } else {
            pembayaranViewModel.setTableType(TablePembayaranType.FORM_PEMBAYARAN)
        }
    }

    private fun setupViewModel() {
        pembayaranViewModel.tableTypeLive.observe(lifecycleOwner) {
            it?.also { tableType ->
                when (tableType) {
                    TablePembayaranType.FORM_PEMBAYARAN -> {
                        navController.navigate(R.id.nav_pembayaran_full_tabel)
                    }
                    TablePembayaranType.PEMBAYARAN_BULANAN -> {
                        navController.navigate(R.id.nav_pembayaran_bulanan_tabel)
                    }
                }

                listener?.onTabelChanged(tableType)
            }
        }
    }


    interface TabelPembayaranListener {
        fun onTabelChanged(tableType: TablePembayaranType)
    }
}