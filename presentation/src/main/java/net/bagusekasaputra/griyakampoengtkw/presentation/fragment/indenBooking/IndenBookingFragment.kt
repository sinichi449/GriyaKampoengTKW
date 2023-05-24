package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.indenBooking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activities.DetailIndenBookingActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.IndenBookingRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.dialog.ModifyIndenBookingDialog
import net.bagusekasaputra.griyakampoengtkw.presentation.util.NotificationUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class IndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentIndenBookingBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: IndenBookingViewModel by activityViewModels()

    private val PROGRESS_CHANNEL = "GktProgress"

    private val uploadBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val title = intent?.extras?.getString("EXTRAS_TITLE")
            val progress = intent?.extras?.getInt("EXTRAS_PROGRESS") ?: 20
            val isComplete = intent?.extras?.getBoolean("EXTRAS_IS_COMPLETED") ?: false
            val textOnComplete = intent?.extras?.getString("EXTRAS_TEXT_ON_COMPLETE")

            val notificationManager = NotificationManagerCompat.from(requireContext())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(PROGRESS_CHANNEL, NotificationUtil.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
            val notification = NotificationCompat.Builder(requireContext(), PROGRESS_CHANNEL).apply {
                setSmallIcon(if (!isComplete) R.drawable.ic_baseline_hourglass_top_24 else R.drawable.ic_baseline_check_circle_18)
                setContentTitle(title)
                if (!isComplete) {
                    setProgress(100, progress, false)
                    setOngoing(true)
                } else {
                    setOngoing(false)
                    setContentText(textOnComplete)
                }
            }.build()

            notificationManager.notify(NotificationUtil.UPLOAD_INDEN_BOOKING_ID, notification)
        }

    }

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localBroadcastManager.registerReceiver(uploadBroadcastReceiver, IntentFilter("net.bagusekasaputra.griyakampoengtkw.ACTION.TRANSFER_PROGRESS"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentIndenBookingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dataMode = mainViewModel.dataMode

        setupViewModel()

        binding.swipeRefreshIndenBooking.setOnRefreshListener {
            sync()
        }

        binding.fabTambahkan.setOnClickListener {
            showAddIndenBookingDialog()
        }

        sync()
    }

    private fun setupViewModel() {
        viewModel.uiModelIndenBooking.observe(requireActivity()) {
            it?.also {
                binding.recyclerViewIndenBooking.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = IndenBookingRecyclerAdapter(it, onClick = { position ->
                        val intent = Intent(requireContext(), DetailIndenBookingActivity::class.java)
                        val namaCostumerExtra = it[position].namaCostumer

                        intent.putExtra(
                            DetailIndenBookingActivity.EXTRAS_NAMA_COSTUMER,
                            namaCostumerExtra
                        )

                        requireActivity().startActivity(intent)
                    })
                }
            }
        }
    }

    private fun showAddIndenBookingDialog() {
        ModifyIndenBookingDialog().apply {
            isCancelable = false
        }.show(childFragmentManager, null)
    }
    private fun sync() {
        viewModel.getListIndenBooking(
            onProgress = {
                binding.swipeRefreshIndenBooking.isRefreshing = true
            },
            onComplete = {
                binding.swipeRefreshIndenBooking.isRefreshing = false
            },
            onFailure = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        )
    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(uploadBroadcastReceiver)

        super.onDestroy()
    }
}