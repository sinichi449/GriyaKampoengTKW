package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.indenBooking

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.presentation.ImageTransport
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.DetailIndenBookingActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FullImageActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentDataDiriIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.util.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.NotificationUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel
import javax.inject.Inject

@AndroidEntryPoint
class DataDiriIndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentDataDiriIndenBookingBinding
    private val viewModel by activityViewModels<IndenBookingViewModel>()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val launcherAddFoto = ImageUtil.createImagePickerLauncherResult(this) { uri ->
        if (uri != null) {
            viewModel.insertFotoIdentitas(
                keyId = viewModel.currentKeyId,
                uri = uri,
                onProgress = {
                    NotificationUtil.createNotification(
                        activity = requireActivity(),
                        title = "Menambahkan Foto Identitas",
                        content = "Mohon tunggu sebentar ...",
                        finished = false
                    )
                },
                onComplete = {
                    NotificationUtil.createNotification(
                        activity = requireActivity(),
                        title = "Selesai menambahkan Foto Identitas!",
                        content = "${viewModel.currentKeyId} telah ditambahkan",
                        finished = true
                    )

                    sync()
                },
                onFailure = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            )
        } else {
            Toast.makeText(requireContext(), "Uri Add Foto Launcher is NULL or Empty!", Toast.LENGTH_LONG).show()
        }
    }

    private val launcherUpdateFoto = ImageUtil.createImagePickerLauncherResult(this) { uri ->
        NotificationUtil.createNotification(
            activity = requireActivity(),
            title = "Mengupdate Foto Identitas",
            content = "Mohon tunggu sebentar ...",
            finished = false
        )

        // TODO
        lifecycleScope.launch(Dispatchers.IO) {
            delay(3000L)

            withContext(Dispatchers.Main) {
                NotificationUtil.createNotification(
                    activity = requireActivity(),
                    title = "Selesai mengupload!",
                    content = "Berhasil mengupdate gambar ${uri.toString()}!",
                    finished = true
                )
            }

            uri?.toFile()?.delete()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDataDiriIndenBookingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshDataDiri.setOnRefreshListener {
            sync()
        }

        // Setup onClick and onHold imgProfile
        with(binding.imgProfile) {
            setOnClickListener {
                // Open full image
                val fullImageIntent = Intent(requireContext(), FullImageActivity::class.java)
                val fullImageTransportData = ImageTransport(
                    sendIntention = GriyaNodes.INTENT_DATA_DIRI_INDEN_BOOKING,
                    content = mapOf(
                        Pair("pathFoto", viewModel.fotoIdentitasUri.value?.toString() ?: "")
                    ),
                    dataMode = viewModel.dataMode,
                )

                fullImageIntent.putExtra(GriyaNodes.INTENT_SOURCE_IMAGE, fullImageTransportData)
                startActivity(fullImageIntent)
            }

            // Enable long click only when foto identitas is available
            if (viewModel.fotoIdentitasUri.value != null) {
                setOnLongClickListener {
                    popUpOnLongPressFotoIdentitas(this)

                    true
                }
            }
        }

        // Hide fab on scroll
        with((requireActivity() as DetailIndenBookingActivity).getFab()) {
            // Hide on scroll
            UiUtils.hideFabsOnVerticalScroll(binding.scrollViewImageviewAndCard, this)

            setOnClickListener {
                val uriFotoIdentitas = viewModel.fotoIdentitasUri.value
                val launcher = if (uriFotoIdentitas != null)
                    launcherUpdateFoto else launcherAddFoto
                val compressionSize = sharedPreferences.getInt("max_size_foto_data_diri", 256)

                ImageUtil.showImagePicker(this@DataDiriIndenBookingFragment,
                    launcher, compressionSize
                )
            }
        }

        setupViewModel()

        sync()
    }

    private fun setupViewModel() {
        viewModel.fotoIdentitasUri.observe(requireActivity()) { fotoIdentitasUri ->
            if (fotoIdentitasUri != null) {
                Glide.with(this)
                    .load(fotoIdentitasUri)
                    .into(binding.imgProfile)
            } else {
                Glide.with(this)
                    .load(R.drawable.avatar_1)
                    .into(binding.imgProfile)
            }
        }

        viewModel.dataDiriIndenBooking.observe(requireActivity()) {
            it?.also { dataDiri ->
                binding.tvNama.text = dataDiri.nama
                binding.tvJenisIdentitas.text = dataDiri.jenisIdentitas
                binding.tvNoIdentitas.text = dataDiri.noIdentitas
                binding.tvNegaraBekerja.text = dataDiri.negaraBekerja
                binding.tvAlamatKerja.text = dataDiri.alamatKerja
                binding.tvAlamatIndo.text = dataDiri.alamatIndo
                binding.tvNoHp.text = dataDiri.noHp
            }
        }
    }

    private fun sync() {
        val currentKeyId = viewModel.currentKeyId
        if ((currentKeyId != "NULL_ID") || (currentKeyId.isNotEmpty())) {
            viewModel.getDataDiri(currentKeyId,
                onProgress = {
                    binding.swipeRefreshDataDiri.isRefreshing = true
                },
                onComplete = {
                    binding.swipeRefreshDataDiri.isRefreshing = false
                },
                onFailure = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            )

            viewModel.getFotoIdentitas(currentKeyId,
                onProgress = {
                    binding.layoutLoadingImage.visibility = View.VISIBLE
                    binding.layoutImageProfile.visibility = View.GONE
                },
                onComplete = {
                    binding.layoutLoadingImage.visibility = View.GONE
                    binding.layoutImageProfile.visibility = View.VISIBLE
                },
                onFailure = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun popUpOnLongPressFotoIdentitas(anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView).apply {
            menuInflater.inflate(R.menu.popup_menu_foto_identitas_inden_booking, menu)
        }

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.hapus_foto_identitas_inden_booking -> {
                    // Show confirmation for deleting foto identitas
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setTitle("Hapus Foto Identitas ${viewModel.namaCostumer}?")
                        setMessage("Apakah Anda yakin ingin menghapus foto identitas ini?")
                        setNegativeButton("Tidak") { dialog, _ ->
                            dialog.dismiss()
                        }
                        setPositiveButton("Ya") { dialog, _ ->
                            Toast.makeText(requireContext(), "OK HAPUS!", Toast.LENGTH_SHORT)
                                .show()

                            sync()

                            dialog.dismiss()
                        }
                    }
                        .create()
                        .show()

                    true
                }
                else -> false
            }
        }
    }
}