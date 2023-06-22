package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.compose.screen.ActionMenusDialog

@AndroidEntryPoint
class ActionPembayaranBottomSheetDialog : ActionBottomSheetDialog() {
    private var pembayaran: Pembayaran? = null

    override val title: String
        get() {
            val kavling = arguments?.getString(EXTRAS_KAVLING)

            pembayaran = arguments?.let {
                BundleCompat.getParcelable(it, EXTRAS_PEMBAYARAN, PembayaranPercelable::class.java)
            }?.item

            return "${pembayaran?.termin}"
        }
    override val menuDialogs: List<ActionMenusDialog>
        get() {
            val dataPembayaranMenus = buildList {
                add(ActionMenusDialog.Menu(R.drawable.ic_baseline_edit_24, "Edit Pembayaran"))
                add(ActionMenusDialog.Menu(R.drawable.baseline_delete_24, "Hapus Pembayaran"))
                add(ActionMenusDialog.Menu(R.drawable.baseline_sort_by_alpha_24, "Ganti Jenis Termin"))
            }
            val fotoPembayaranMenus = buildList {
                if (pembayaran?.sudahIsiFotoPembayaran == true) {
                    add(ActionMenusDialog.Menu(R.drawable.ic_baseline_camera_alt_24, "Ganti Foto"))
                    add(ActionMenusDialog.Menu(R.drawable.baseline_photo_24, "Lihat Foto"))
                    add(ActionMenusDialog.Menu(R.drawable.baseline_broken_image_24, "Hapus Foto"))
                } else {
                    add(ActionMenusDialog.Menu(R.drawable.ic_baseline_camera_alt_24, "Tambahkan Foto"))
                }
            }

            return buildList {
                add(object : ActionMenusDialog {
                    override val header: String
                        get() = "Data Pembayaran"
                    override val menuItems: List<ActionMenusDialog.Menu>
                        get() = dataPembayaranMenus

                })
                add(object : ActionMenusDialog {
                    override val header: String
                        get() = "Foto Pembayaran"
                    override val menuItems: List<ActionMenusDialog.Menu>
                        get() = fotoPembayaranMenus

                })
            }
        }

    override fun onMenuItemClick(menuIndex: Int, itemIndex: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        const val EXTRAS_KAVLING = "EXTRAS_KAVLING"
        const val EXTRAS_PEMBAYARAN = "EXTRAS_PEMBAYARAN"
    }

    @Parcelize
    data class PembayaranPercelable(
        val item: @RawValue Pembayaran
    ): Parcelable {
        companion object {
            fun createPembayaranBundle(kavling: String, pembayaran: Pembayaran): Bundle {
                val pembayaranParcelable = PembayaranPercelable(pembayaran)

                return bundleOf(
                    EXTRAS_KAVLING to kavling,
                    EXTRAS_PEMBAYARAN to pembayaranParcelable,
                )
            }
        }
    }
}