package net.bagusekasaputra.griyakampoengtkw.presentation.activities

import abhishekti7.unicorn.filepicker.UnicornFilePicker
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogCreateBackupBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.SettingsActivityBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.SettingsViewModel
import java.util.Calendar

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbarSetting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    @AndroidEntryPoint
    class SettingsFragment : PreferenceFragmentCompat() {
        private val viewModel: SettingsViewModel by viewModels()
        private val progressDialog by lazy {
            ProgressDialog(requireContext()).apply {
                isIndeterminate = false
            }
        }
        private val PICK_BACKUP_PATH_REQUEST = 4
        private val READ_WRITE_STORAGE_REQUEST = 5
        private var dialogBinding: DialogCreateBackupBinding? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            requestReadWriteExternalStorage()

            val backupData = findPreference<Preference>("backup_data")
            val restoreData = findPreference<Preference>("restore_data")

            backupData?.setOnPreferenceClickListener {
                progressDialog.setTitle("Membackup Data")

                showSetNamaBackupDialog(onBtnOkClick = { backupName, backupAbsolutePath ->
                    viewModel.createBackup(
                        backupName = backupName,
                        backupSavePath = backupAbsolutePath,
                        onFailure = { failReason ->
                            Toast.makeText(
                                requireContext(),
                                "Gagal membuat backup: $failReason",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                })

                true
            }

            restoreData?.setOnPreferenceClickListener {
                // TODO
                Toast.makeText(requireContext(), "Ping pong!", Toast.LENGTH_SHORT).show()

                true
            }

            setupViewModel()
        }

        private fun setupViewModel() {
            viewModel.isBackupComplete.observe(requireActivity()) { isComplete ->
                isComplete?.let {
                    if (it) {
                        progressDialog.dismiss()

                        Toast.makeText(requireContext(), "Berhasil membuat backup!", Toast.LENGTH_SHORT).show()
                    } else{
                        progressDialog.show()
                    }
                }
            }
            viewModel.backupRestoreProgress.observe(requireActivity()) { progress ->
                progress?.let {
                    progressDialog.progress = it.progress
                    progressDialog.setMessage(it.message)
                }
            }
        }

        private fun showSetNamaBackupDialog(onBtnOkClick: (namaBackup: String, backupSaveAbsolutePath: String) -> Unit) {
            dialogBinding = DialogCreateBackupBinding.inflate(layoutInflater)
            val dialogView = MaterialAlertDialogBuilder(requireContext()).apply {
                setView(dialogBinding?.root)
            }.create()

            DialogUtil.additionalDialogSetting(requireContext(), dialogView)

            dialogBinding?.edtNamaBackup?.setText(getDefaultBackupName())
            dialogBinding?.edtSaveFolderPath?.setText(getDefaultBackupPath())

            dialogView.show()

            dialogBinding?.btnBuatBackup?.setOnClickListener {
                val isInvalidEdt = dialogBinding?.let {
                    InputUtil.isNullOrEmptyEditTexts(
                        it.edtNamaBackup,
                        it.edtSaveFolderPath
                    )
                } ?: false

                if (!isInvalidEdt) {
                    val namaBackup = dialogBinding?.edtNamaBackup?.text.toString()
                    val backupAbsolutePath = dialogBinding?.edtSaveFolderPath?.text.toString()

                    onBtnOkClick(namaBackup, backupAbsolutePath)

                    dialogView.dismiss()
                }
            }
            dialogBinding?.btnPilihFolder?.setOnClickListener {
                showBackupSavePathPicker()
            }
            dialogBinding?.btnBatal?.setOnClickListener {
                dialogView.dismiss()
            }

        }

        private fun getDefaultBackupName(): String {
            val calendar = Calendar.getInstance()

            val padWithZero = { num: Int ->
                String.format("%02d", num)
            }

            val tahun = calendar.get(Calendar.YEAR)
            val bulan = padWithZero(calendar.get(Calendar.MONTH) + 1)
            val tanggal = padWithZero(calendar.get(Calendar.DAY_OF_MONTH))
            val jam = padWithZero(calendar.get(Calendar.HOUR_OF_DAY))
            val menit = padWithZero(calendar.get(Calendar.MINUTE))

            return "backup_$tahun$bulan${tanggal}_$jam$menit"
        }

        private fun getDefaultBackupPath(): String {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .absolutePath
        }

        private fun requestReadWriteExternalStorage() {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                READ_WRITE_STORAGE_REQUEST,
            )
        }

        private fun showBackupSavePathPicker() {
            UnicornFilePicker.from(this)
                .addConfigBuilder()
                .selectMultipleFiles(false)
                .showOnlyDirectory(true)
                .setRootDirectory(Environment.getExternalStorageDirectory().absolutePath)
                .showHiddenFiles(false)
                .addItemDivider(true)
                .build()
                .forResult(PICK_BACKUP_PATH_REQUEST)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            when (requestCode) {
                PICK_BACKUP_PATH_REQUEST -> if (resultCode == RESULT_OK) {
                    val arrFilePathStr = data?.getStringArrayListExtra("filePaths")

//                    viewModel.setBackupSavePath(arrFilePathStr?.get(0) ?: "")
                    Log.d("DEBUG_ME", "SettingsActivity: Got Backup Save Path on $arrFilePathStr")
                    dialogBinding?.edtSaveFolderPath?.setText(arrFilePathStr?.get(0))
                }
            }
        }
    }

    private fun goMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        goMainActivity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                goMainActivity()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}