package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.indenBooking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.ImageTransport
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.DetailIndenBookingActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FullImageActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentDataDiriIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel

@AndroidEntryPoint
class DataDiriIndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentDataDiriIndenBookingBinding
    private val viewModel by activityViewModels<IndenBookingViewModel>()

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

        binding.imgProfile.setOnClickListener {
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

        // Hide fab on scroll
        val fabAction = (requireActivity() as DetailIndenBookingActivity).getFab()
        UiUtils.hideFabsOnVerticalScroll(binding.scrollViewImageviewAndCard, fabAction)

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
}