package net.bagusekasaputra.griyakampoengtkw.presentation.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormInputPembayaranIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FormUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.UiUtils
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel
import kotlin.random.Random

@AndroidEntryPoint
class FormInputPembayaranIndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentFormInputPembayaranIndenBookingBinding
    private val viewModel by activityViewModels<IndenBookingViewModel>()

    private var currentKeyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentKeyId = arguments?.getString(FormActivity.EXTRAS_KEY_ID_INDEN_BOOKING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFormInputPembayaranIndenBookingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar title
        (requireActivity() as FormActivity).setFormTitle("Pembayaran (Inden Booking)")

        // Setup fab
        with((requireActivity() as FormActivity).getFabDone()) {
            // Hide fab on scroll
            UiUtils.hideFabsOnVerticalScroll(binding.root, this)

            setOnClickListener {
                // TODO
                val progressSnackBar = Snackbar.make(binding.root, "Memproses data pembayaran ...", Snackbar.LENGTH_INDEFINITE)
                lifecycleScope.launch(Dispatchers.Default) {
                    withContext(Dispatchers.Main) {
                        progressSnackBar.show()
                    }

                    delay(2500L)

                    withContext(Dispatchers.Main) {
                        progressSnackBar.dismiss()

                        val isSuccess = Random.nextBoolean()
                        if (isSuccess) {
                            FormUtil.sendResultAndExit(requireActivity(), Activity.RESULT_OK, null)
                        } else {
                            val failMsgIntent = Intent().apply {
                                putExtra(FormActivity.EXTRAS_FAIL_MSG, "Random error!")
                            }
                            FormUtil.sendResultAndExit(requireActivity(),
                                Activity.RESULT_CANCELED,
                                failMsgIntent
                            )
                        }
                    }
                }
            }
        }
    }
}