package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FullImageActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogActionsItemPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ImageViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class ActionPembayaranIndenBookingBottomSheetDialog: BottomSheetDialogFragment() {

    private lateinit var binding: DialogActionsItemPembayaranBinding
    private val viewModel by activityViewModels<IndenBookingViewModel>()
    private val imageViewModel by activityViewModels<ImageViewModel>()
    private var indexPembayaran: Int? = null

    // Need for acquiring max size foto pembayaran compression
    @Inject
    lateinit var sharedPrefs: SharedPreferences

    // Need both of these to fill the register picker results
    private var currentKeyId = ""
    private var currentTermin = ""

    // Need to define here to avoid uninitialized binding
    private var progressBarTambahFoto: ProgressBar? = null

    companion object {
        const val EXTRAS_INDEX_PEMBAYARAN_POSITION = "EXTRAS_INDEX_PEMBAYARAN_POSITION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        indexPembayaran = arguments?.getInt(EXTRAS_INDEX_PEMBAYARAN_POSITION)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogActionsItemPembayaranBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pembayaran = viewModel.pembayaranListIndenBooking.value?.get(indexPembayaran!!)!!

        currentKeyId = viewModel.currentKeyId
        currentTermin = pembayaran.termin
        progressBarTambahFoto = binding.progressbarTambahkanFoto

        // Dialog title
        binding.tvKavlingTermin.text = currentTermin

        if (pembayaran.sudahIsiFotoPembayaran) {
            binding.cardAmbilKuitansi.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    with (binding.switchSudahAmbilKuitansi) {
                        isChecked = !isChecked
                    }
                }
            }

            binding.switchSudahAmbilKuitansi.apply {
                visibility = View.VISIBLE
                isChecked = pembayaran.sudahAmbilKuitansi
                setOnCheckedChangeListener { _, _ ->
                    // TODO
                }
            }

            binding.cardTambahkanFotoPembayaran.visibility = View.GONE

            binding.cardLihatFotoPembayaran.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    val imageTransport = imageViewModel.createImageTransport(
                        sendIntent = GriyaNodes.INTENT_FOTO_PEMBAYARAN_INDEN_BOOKING,
                        content = mapOf(
                            "keyId" to currentKeyId,
                            "termin" to currentTermin,
                        ),
                    )

                    val fullImageIntent = Intent(requireContext(), FullImageActivity::class.java)
                    fullImageIntent.putExtra(GriyaNodes.INTENT_SOURCE_IMAGE, imageTransport)
                    startActivity(fullImageIntent)
                }
            }

            binding.cardHapusFotoPembayaran.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    // TODO
                }
            }
        } else {
            binding.cardAmbilKuitansi.visibility = View.GONE

            binding.cardTambahkanFotoPembayaran.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    // TODO
                }
            }

            binding.cardLihatFotoPembayaran.visibility = View.GONE

            binding.cardHapusFotoPembayaran.visibility = View.GONE
        }

        binding.cardHapusDataPembayaran.setOnClickListener {
            // Show hapus Pembayaran confirmation.
            // Foto pembayaran will also deleted!
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Pembayaran")
                .setMessage("Apakah Anda yakin menghapus pembayaran $currentTermin? " +
                        "Foto pembayaran juga akan terhapus!")
                .setPositiveButton("Ya") { dialogHapus, _ ->
                    dialogHapus.dismiss()

                    testMock(
                        onProgress = {
                            binding.progressbarHapusData.visibility = View.VISIBLE
                            binding.progressbarHapusFoto.visibility = View.VISIBLE
                        },
                        onSuccess = {
                            dialogHapus.dismiss()

                            this@ActionPembayaranIndenBookingBottomSheetDialog.dismiss()
                        },
                        onFailure = {

                        }
                    )
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun testMock(
        onProgress: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        lifecycleScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                onProgress()
            }

            delay(3000L)

            val isSuccess = Random.nextBoolean()
            withContext(Dispatchers.Main) {
                if (isSuccess) {
                    onSuccess()

                    Toast.makeText(requireContext(), "Berhasil!", Toast.LENGTH_SHORT).show()

                    this@ActionPembayaranIndenBookingBottomSheetDialog.dismiss()
                } else {
                    onFailure()

                    Toast.makeText(requireContext(), "Random failure", Toast.LENGTH_LONG).show()

                    this@ActionPembayaranIndenBookingBottomSheetDialog.dismiss()
                }
            }
        }
    }
}