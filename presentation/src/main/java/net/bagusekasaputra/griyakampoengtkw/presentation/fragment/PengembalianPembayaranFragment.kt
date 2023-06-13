package net.bagusekasaputra.griyakampoengtkw.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentPengembalianPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.NotificationUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.PengembalianViewModel

@AndroidEntryPoint
class PengembalianPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentPengembalianPembayaranBinding
    private var fabAction: FloatingActionButton? = null

    private val viewModel by viewModels<PengembalianViewModel>()

    private companion object {
        const val CHANNEL_ID = "PENGEMBALIAN_PEMBAYARAN"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPengembalianPembayaranBinding.inflate(inflater, container, false)

        fabAction = requireActivity().findViewById(R.id.fab_action_pengembalian_pembayaran)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            UiUtils.hideFabsOnVerticalScroll(scrollViewContent, fabAction)

            setupWithViewModel()

            fabAction?.setOnClickListener {
                Snackbar.make(
                    binding.root,
                    "Ini pengembalian pembayaran",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            swipeRefreshPengembalianPembayaran.setOnRefreshListener {
                sync()
            }
        }

        sync()
    }

    private fun FragmentPengembalianPembayaranBinding.setupWithViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pengembalianList.collect { items ->
                    Log.d("PENGEMBALIAN_PEMBAYARAN", " Size: ${items?.size}")
                }
            }
        }
    }

    private fun sync() {
        with(binding) {
            viewModel.getAllPengembalianList(
                onLoading = { swipeRefreshPengembalianPembayaran.isRefreshing = true },
                onCompleted = { swipeRefreshPengembalianPembayaran.isRefreshing = false },
                onFailed = {
                    NotificationUtil.createNotification(
                        activity = requireActivity(),
                        title = "Terjadi kesalahan!",
                        text = it ?: "NULL",
                        channelId = CHANNEL_ID,
                        notificationId = 100,
                    )
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()

        fabAction?.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()

        fabAction?.visibility = View.GONE
    }

}