package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker

object ImageUtil {

    fun createImagePickerLauncherResult(
        fragment: Fragment,
        onResultOk: (uri: Uri?) -> Unit
    ): ActivityResultLauncher<Intent> {
        return fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    onResultOk(data?.data)
                    Log.d("DEBUG_ME", "ImagePicker(): Pick image in ${data?.data} success!")
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(fragment.requireContext(), ImagePicker.getError(data), Toast.LENGTH_LONG).show()
                    Log.d("DEBUG_ME", "Error image picker: ${ImagePicker.getError(data)}")
                }
                else -> {
                    Toast.makeText(fragment.requireContext(), "Operasi dibatalkan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun showImagePicker(
        fragment: Fragment,
        launcher: ActivityResultLauncher<Intent>,
        compressionSize: Int,
    ) {
        ImagePicker.with(fragment)
            .crop()
            .compress(compressionSize)
            .createIntent { intent ->
                launcher.launch(intent)
            }
    }
}