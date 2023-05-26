package net.bagusekasaputra.griyakampoengtkw.presentation.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormInputDataDiriIndenBookingBinding
import java.util.UUID
import kotlin.random.Random

@AndroidEntryPoint
class FormInputDataDiriIndenBookingFragment : Fragment() {

    private lateinit var binding: FragmentFormInputDataDiriIndenBookingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFormInputDataDiriIndenBookingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as FormActivity).setFormTitle("Data Diri (Inden Booking)")
        (requireActivity() as FormActivity).getFabDone().setOnClickListener {
            val isSuccess = Random.nextBoolean()

            if (isSuccess) {
                val dataToSend = Intent().apply {
                    putExtra(FormActivity.EXTRAS_SUCCESS_DATA, UUID.randomUUID().toString())
                }
                requireActivity().setResult(Activity.RESULT_OK, dataToSend)
            } else {
                val dataToSend = Intent().apply {
                    putExtra(FormActivity.EXTRAS_FAIL_MSG, "Random failure!!")
                }
                requireActivity().setResult(Activity.RESULT_CANCELED, dataToSend)
            }

            requireActivity().finish()
        }
    }

}