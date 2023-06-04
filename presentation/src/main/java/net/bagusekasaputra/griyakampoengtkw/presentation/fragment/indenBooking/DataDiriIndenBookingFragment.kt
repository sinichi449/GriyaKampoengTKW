@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.indenBooking

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.model.ImageTransport
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.DetailIndenBookingActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FullImageActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentDataDiriIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.util.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.NotificationUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.ImageViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel
import javax.inject.Inject

@AndroidEntryPoint
class DataDiriIndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentDataDiriIndenBookingBinding
    private val viewModel by activityViewModels<IndenBookingViewModel>()
    private val imageViewModel by activityViewModels<ImageViewModel>()

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
        if (uri != null) {
            viewModel.updateFotoIdentitas(
                keyId = viewModel.currentKeyId,
                uri = uri,
                onProgress = {
                    NotificationUtil.createNotification(
                        activity = requireActivity(),
                        title = "Mengubah Foto Identitas",
                        content = "Mohon tunggu sebentar ...",
                        finished = false
                    )
                },
                onComplete = {
                    NotificationUtil.createNotification(
                        activity = requireActivity(),
                        title = "Selesai mengubah Foto Identitas!",
                        content = "${viewModel.currentKeyId} telah diubah!",
                        finished = true
                    )

                    sync()
                },
                onFailure = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            )
        } else {
            Toast.makeText(requireContext(), "Uri Update Foto Launcher is NULL or Empty!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
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
                        Pair("pathFoto", imageViewModel.imageDataDiriIndenBooking.value?.uriStr ?: "")
                    ),
                    dataMode = viewModel.dataMode,
                )

                fullImageIntent.putExtra(GriyaNodes.INTENT_SOURCE_IMAGE, fullImageTransportData)
                startActivity(fullImageIntent)
            }
        }

        // Hide fab on scroll
        with((requireActivity() as DetailIndenBookingActivity).getFab()) {
            // Hide on scroll
            UiUtils.hideFabsOnVerticalScroll(binding.scrollViewImageviewAndCard, this)

            setOnClickListener {
                val imageDataDiri = imageViewModel.imageDataDiriIndenBooking.value
                val launcher = if (imageDataDiri != null)
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
        imageViewModel.imageDataDiriIndenBooking.observe(requireActivity()) { imageDataDiri ->
            with(binding.imgProfile) {
                if (imageDataDiri != null) {
                    setImageURI(Uri.parse(imageDataDiri.uriStr))
                } else {
                    val dummyFotoDrawable = ContextCompat.getDrawable(
                        requireContext(), R.drawable.avatar_1
                    )
                    setImageDrawable(dummyFotoDrawable)
                }
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

            imageViewModel.getImageDataDiriIndenBooking(currentKeyId,
                onProgress = {
                    binding.layoutLoadingImage.visibility = View.VISIBLE
                    binding.layoutImageProfile.visibility = View.GONE
                },
                onSuccess = {
                    binding.layoutLoadingImage.visibility = View.GONE
                    binding.layoutImageProfile.visibility = View.VISIBLE
                },
                onFailure = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_foto_identitas_inden_booking, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.hapus_foto_identitas_inden_booking -> {
                val imageDataDiri = imageViewModel.imageDataDiriIndenBooking.value

                if (imageDataDiri != null) {
                    // Show confirmation for deleting foto identitas
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setTitle("Hapus Foto Identitas ${viewModel.namaCostumer}?")
                        setMessage("Apakah Anda yakin ingin menghapus foto identitas ini?")
                        setNegativeButton("Tidak") { dialog, _ ->
                            dialog.dismiss()
                        }
                        setPositiveButton("Ya") { dialog, _ ->
                            dialog.dismiss()

                            // Delete process
                            val deleteProcessSnackbar = Snackbar.make(binding.root, "Menghapus foto identitas ...", Snackbar.LENGTH_INDEFINITE)

                            viewModel.deleteFotoIdentitas(
                                keyId = viewModel.currentKeyId,
                                uri = Uri.parse(imageDataDiri.uriStr),
                                onProgress = {
                                    deleteProcessSnackbar.show()
                                },
                                onComplete = {
                                    deleteProcessSnackbar.dismiss()

                                    Snackbar.make(binding.root, "Berhasil menghapus foto identitas!", Snackbar.LENGTH_SHORT)
                                        .show()

                                    sync()
                                },
                                onFailure = { failMsg ->
                                    Toast.makeText(requireContext(), failMsg, Toast.LENGTH_LONG)
                                        .show()
                                }
                            )
                        }
                    }
                        .create()
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Foto identitas masih kosong!", Toast.LENGTH_LONG).show()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}